package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.dto.CreateResearchPaperRequest;
import com.example.RESEARCH_SERVICE.dto.ResearchPaperResponse;
import com.example.RESEARCH_SERVICE.dto.ResearchPaperSummaryResponse;
import com.example.RESEARCH_SERVICE.dto.UpdateResearchPaperRequest;
import com.example.RESEARCH_SERVICE.entity.CurrentUser;
import com.example.RESEARCH_SERVICE.entity.ResearchCategory;
import com.example.RESEARCH_SERVICE.entity.ResearchPaper;
import com.example.RESEARCH_SERVICE.enums.ResearchStatus;
import com.example.RESEARCH_SERVICE.enums.ResearchVisibility;
import com.example.RESEARCH_SERVICE.exception.AccessDeniedException;
import com.example.RESEARCH_SERVICE.exception.DuplicateResourceException;
import com.example.RESEARCH_SERVICE.exception.InvalidOperationException;
import com.example.RESEARCH_SERVICE.exception.ResourceNotFoundException;
import com.example.RESEARCH_SERVICE.mapper.ResearchPaperMapper;
import com.example.RESEARCH_SERVICE.payload.PagedResponse;
import com.example.RESEARCH_SERVICE.publisher.ResearchEventPublisher;
import com.example.RESEARCH_SERVICE.repository.ResearchCategoryRepository;
import com.example.RESEARCH_SERVICE.repository.ResearchPaperRepository;
import com.example.RESEARCH_SERVICE.utils.ResearchAuditLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ResearchPaperService {

    private final ResearchPaperRepository paperRepository;
    private final ResearchCategoryRepository categoryRepository;
    private final ResearchPaperMapper mapper;
    private final CurrentUserService currentUserService;
    private final ResearchAuditLogger auditLogger;
    private final AuditLogService auditLogService;
    private final ResearchEventPublisher eventPublisher;
//    private final UserServiceClient userServiceClient;

    private void validateSubmissionPermission() {

        String role = currentUserService.getCurrentUser().getRole();

        if (!role.equals("RESEARCHER") && !role.equals("ADMIN")
        ) {
            throw new AccessDeniedException("You are not allowed to submit research papers");
        }
    }

    private void validatePaperAccess(
            ResearchPaper paper
    ) {
        CurrentUser currentUser = currentUserService.getCurrentUser();

        boolean isOwner = Objects.equals(
                paper.getAuthorId(),
                currentUser.getId()
        );

        boolean isAdmin = currentUser.getRole().equalsIgnoreCase("ADMIN");

        boolean isReviewer = Objects.equals(
                paper.getReviewerId(),
                currentUser.getId()
        );

        if (isOwner || isAdmin || isReviewer) {
            return;
        }

        if (paper.getStatus() == ResearchStatus.PUBLISHED
                && paper.getVisibility() == ResearchVisibility.PUBLIC) {
            return;
        }

        throw new AccessDeniedException("You do not have permission to view this paper");
    }

    private Pageable buildPageable(
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return PageRequest.of(page, size, sort);
    }

    private void validateOwnership(
            ResearchPaper paper
    ) {
        CurrentUser currentUser = currentUserService.getCurrentUser();

        if (!Objects.equals(paper.getAuthorId(), currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to modify this paper");
        }
    }

    private void validateEditableStatus(
            ResearchPaper paper
    ) {
        if (paper.getStatus() != ResearchStatus.DRAFT
                &&
                paper.getStatus() != ResearchStatus.REJECTED
        ) {
            throw new InvalidOperationException("Paper can no longer be modified");
        }
    }

    @Transactional
    public ResearchPaperResponse createPaper(
            CreateResearchPaperRequest request
    ) {
        validateSubmissionPermission();

        CurrentUser user = currentUserService.getCurrentUser();

//        UserProfileResponse profile =  userServiceClient.getUserProfile(
//                user.getId()
//        );

        if (paperRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new DuplicateResourceException("Research paper title already exists");
        }

        ResearchCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        ResearchPaper paper =
                ResearchPaper.builder()
                        .title(request.getTitle())
                        .abstractText(request.getAbstractText())
                        .keywords(request.getKeywords())
                        .category(category)
                        .visibility(request.getVisibility())
                        .authorId(user.getId())
                        .authorEmail(user.getEmail())

                        /*
                         TODO:
                         Replace with User Service profile lookup.
                        */
                        .authorName(user.getEmail())
                        .institution("UNKNOWN")
                        .faculty("UNKNOWN")
                        .department("UNKNOWN")
                        .status(ResearchStatus.DRAFT)
                        .versionNumber(1)
                        .viewCount(0L)
                        .downloadCount(0L)
                        .submittedAt(null)
                        .publishedAt(null)
                        .reviewerId(null)
                        .reviewAssignedAt(null)
                        .reviewCompletedAt(null)
                        .build();

        ResearchPaper saved = paperRepository.save(paper);

        auditLogger.logPaperCreated(saved, user.getId());

        eventPublisher.publishResearchCreated(saved);

        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ResearchPaperResponse getPaperById(
            Long paperId
    ) {
        ResearchPaper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException("Research paper not found"));

        validatePaperAccess(paper);

        return mapper.toResponse(paper);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ResearchPaperSummaryResponse> getAllPapers(
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDirection);

        Page<ResearchPaperSummaryResponse> papers = paperRepository.findByStatusAndVisibility(
                ResearchStatus.PUBLISHED,
                ResearchVisibility.PUBLIC,
                pageable
        ).map(mapper::toSummary);

        return new PagedResponse<>(papers);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ResearchPaperSummaryResponse> getMyPapers(
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        CurrentUser user = currentUserService.getCurrentUser();

        Pageable pageable = buildPageable(page, size, sortBy, sortDirection);

        Page<ResearchPaperSummaryResponse> papers = paperRepository.findByAuthorId(
                user.getId(),
                pageable
        ).map(mapper::toSummary);

        return new PagedResponse<>(papers);
    }

    @Transactional
    public ResearchPaperResponse updatePaper(
            Long paperId,
            UpdateResearchPaperRequest request
    ) {
        ResearchPaper paper = paperRepository.findById(paperId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Research paper not found")
                );

        validateOwnership(paper);
        validateEditableStatus(paper);

        ResearchCategory category = categoryRepository.findById(
                        request.getCategoryId()
                )
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!paper.getTitle().equalsIgnoreCase(request.getTitle())
                &&
                paperRepository.existsByTitleIgnoreCase(request.getTitle())
        ) {
            throw new DuplicateResourceException(
                    "Research paper title already exists"
            );
        }

        paper.setTitle(request.getTitle());
        paper.setAbstractText(request.getAbstractText());
        paper.setKeywords(request.getKeywords());
        paper.setVisibility(request.getVisibility());
        paper.setCategory(category);

        ResearchPaper saved = paperRepository.save(paper);
        auditLogger.logPaperUpdated(
                saved,
                currentUserService.getCurrentUser().getId()
        );

        return mapper.toResponse(saved);
    }
}
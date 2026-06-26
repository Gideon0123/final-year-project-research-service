package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.dto.CreateResearchPaperRequest;
import com.example.RESEARCH_SERVICE.dto.ResearchPaperResponse;
import com.example.RESEARCH_SERVICE.entity.CurrentUser;
import com.example.RESEARCH_SERVICE.entity.ResearchCategory;
import com.example.RESEARCH_SERVICE.entity.ResearchPaper;
import com.example.RESEARCH_SERVICE.enums.ResearchStatus;
import com.example.RESEARCH_SERVICE.exception.AccessDeniedException;
import com.example.RESEARCH_SERVICE.exception.DuplicateResourceException;
import com.example.RESEARCH_SERVICE.exception.ResourceNotFoundException;
import com.example.RESEARCH_SERVICE.mapper.ResearchPaperMapper;
import com.example.RESEARCH_SERVICE.publisher.ResearchEventPublisher;
import com.example.RESEARCH_SERVICE.repository.ResearchCategoryRepository;
import com.example.RESEARCH_SERVICE.repository.ResearchPaperRepository;
import com.example.RESEARCH_SERVICE.utils.ResearchAuditLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ResearchPaperService {

    private final ResearchPaperRepository paperRepository;
    private final ResearchCategoryRepository categoryRepository;
    private final ResearchPaperMapper mapper;
    private final CurrentUserService currentUserService;
    private final ResearchAuditLogger auditLogger;
    private final ResearchEventPublisher eventPublisher;
//    private final UserServiceClient userServiceClient;

    private void validateSubmissionPermission() {

        String role = currentUserService.getCurrentUser().getRole();

        if (!role.equals("RESEARCHER") && !role.equals("ADMIN")
        ) {
            throw new AccessDeniedException("You are not allowed to submit research papers");
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
}
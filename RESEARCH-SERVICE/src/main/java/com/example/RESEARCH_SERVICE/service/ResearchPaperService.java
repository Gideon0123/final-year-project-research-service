package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.dto.*;
import com.example.RESEARCH_SERVICE.entity.CurrentUser;
import com.example.RESEARCH_SERVICE.entity.ResearchCategory;
import com.example.RESEARCH_SERVICE.entity.ResearchPaper;
import com.example.RESEARCH_SERVICE.enums.ResearchStatus;
import com.example.RESEARCH_SERVICE.enums.ResearchVisibility;
import com.example.RESEARCH_SERVICE.exception.*;
import com.example.RESEARCH_SERVICE.mapper.ResearchPaperMapper;
import com.example.RESEARCH_SERVICE.payload.PagedResponse;
import com.example.RESEARCH_SERVICE.publisher.ResearchEventPublisher;
import com.example.RESEARCH_SERVICE.repository.ResearchCategoryRepository;
import com.example.RESEARCH_SERVICE.repository.ResearchPaperRepository;
import com.example.RESEARCH_SERVICE.repository.specification.ResearchPaperSpecification;
import com.example.RESEARCH_SERVICE.utils.FileConstants;
import com.example.RESEARCH_SERVICE.utils.ResearchAuditLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
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
    private final ResearchEventPublisher eventPublisher;
    private final FileStorageService fileStorageService;
//    private final UserServiceClient userServiceClient;

    private ResearchPaper getPaperEntity(
            Long paperId
    ) {
        return paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Research paper not found with id: " + paperId)
                );
    }

    private void validateSubmissionPermission() {

        String role = currentUserService.getCurrentUser().getRole();

        if (!role.equals("RESEARCHER") && !role.equals("ADMIN")) {
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

    private void validateUploadStatus(
            ResearchPaper paper
    ) {
        if (paper.getStatus() == ResearchStatus.PUBLISHED
                ||
                paper.getStatus() == ResearchStatus.APPROVED
        ) {
            throw new InvalidOperationException("Paper can no longer be modified");
        }
    }

    private void validatePaperFileExists(
            ResearchPaper paper
    ) {
        if (
                paper.getStorageKey() == null
                ||
                paper.getFileName() == null
                ||
                paper.getFileSize() == null
        ) {
            throw new InvalidOperationException(
                    "Research file has not been uploaded"
            );
        }
    }

    private void validatePdf(
            MultipartFile file
    ) {
        if (file.isEmpty()) {
            throw new InvalidOperationException("File cannot be empty");
        }

        if (!FileConstants.PDF_CONTENT_TYPE.equals(file.getContentType())) {
            throw new InvalidOperationException("Only PDF files are allowed");
        }

        if (file.getSize() > FileConstants.MAX_PDF_SIZE) {
            throw new InvalidOperationException("File exceeds maximum allowed size of 50MB");
        }
    }

    private String buildObjectKey(
            Long paperId,
            Integer version
    ) {
        return String.format(
                "research-papers/%d/v%d.pdf",
                paperId,
                version
        );
    }

    private void validateFileNotAlreadyUploaded(
            ResearchPaper paper
    ) {
        if (paper.getStorageKey() != null) {
            throw new InvalidOperationException("Paper already contains an uploaded file");
        }
    }

    private void  validateSubmissionStatus(
            ResearchPaper paper
    ) {
        if (paper.getStatus() != ResearchStatus.DRAFT) {
            throw new InvalidOperationException(
                    "Cannot Submit This Research Paper"
            );
        }
    }

    private void validateDownloadEligibility(
            ResearchPaper paper
    ) {
        if (paper.getStorageKey() == null) {

            throw new InvalidOperationException(
                    "Research paper has no uploaded document."
            );
        }

        if (paper.getStatus() == ResearchStatus.DELETED) {

            throw new InvalidOperationException(
                    "Research paper is unavailable."
            );
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
        ResearchPaper paper = getPaperEntity(paperId);

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
        ResearchPaper paper = getPaperEntity(paperId);

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
        eventPublisher.publishResearchUpdated(
                saved
        );

        return mapper.toResponse(saved);
    }

    @Transactional
    public void deletePaper(
            Long paperId
    ) {
        ResearchPaper paper = getPaperEntity(paperId);

        validateOwnership(paper);
        validateEditableStatus(paper);

        /*
             TODO:
             if (paper.getStorageKey() != null) {
                 fileStorageService.delete(...)
            }
        */

        paperRepository.delete(paper);
        auditLogger.logPaperDeleted(
                paper,
                currentUserService.getCurrentUser().getId()
        );
        eventPublisher.publishResearchDeleted(
                paper
        );
    }

    @Transactional
    public ResearchPaperResponse submitPaper(
            Long paperId
    ) {
        ResearchPaper paper = getPaperEntity(paperId);

        if (paper.getStorageKey() == null) {
            throw new InvalidOperationException(
                    "Please upload a research paper file before submission"
            );
        }

        validateOwnership(paper);
        validatePaperFileExists(paper);
        validateSubmissionStatus(paper);

        paper.setStatus(ResearchStatus.SUBMITTED);
        paper.setSubmittedAt(LocalDateTime.now());

        ResearchPaper saved = paperRepository.save(paper);

        auditLogger.logPaperSubmitted(
                saved,
                currentUserService.getCurrentUser().getId()
        );

        eventPublisher.publishResearchSubmitted(saved);

        return mapper.toResponse(saved);
    }

    @Transactional
    public ResearchPaperResponse assignReviewer(
            Long paperId,
            AssignReviewerRequest request
    ) {
        ResearchPaper paper = getPaperEntity(paperId);

        if (paper.getStatus() != ResearchStatus.SUBMITTED) {

            throw new InvalidOperationException(
                    "Reviewer can only be assigned to submitted papers"
            );
        }

        if (paper.getReviewerId() != null) {

            throw new InvalidOperationException(
                    "Reviewer already assigned"
            );
        }

        paper.setReviewerId(request.getReviewerId());
        paper.setStatus(ResearchStatus.UNDER_REVIEW);
        paper.setReviewAssignedAt(LocalDateTime.now());
        ResearchPaper saved = paperRepository.save(paper);

        auditLogger.logReviewerAssigned(
                saved,
                request.getReviewerId()
        );

        eventPublisher.publishReviewerAssigned(
                saved
        );

        return mapper.toResponse(saved);
    }

    @Transactional
    public ResearchPaperResponse changeStatus(
            Long paperId,
            ChangeResearchStatusRequest request
    ) {
        ResearchPaper paper = getPaperEntity(paperId);

        if (paper.getReviewerId() == null) {
            throw new InvalidOperationException("No reviewer assigned");
        }

        if (paper.getStatus() != ResearchStatus.UNDER_REVIEW) {
            throw new InvalidOperationException("Paper is not under review");
        }

        if (request.getStatus() != ResearchStatus.APPROVED
                &&
                request.getStatus() != ResearchStatus.REJECTED
        ) {
            throw new InvalidOperationException("Invalid review status");
        }

        paper.setStatus(request.getStatus());
        paper.setReviewCompletedAt(LocalDateTime.now());

        ResearchPaper saved = paperRepository.save(paper);
        auditLogger.logReviewCompleted(saved, currentUserService.getCurrentUser().getId());
        eventPublisher.publishReviewCompleted(saved);
        return mapper.toResponse(saved);
    }

    @Transactional
    public ResearchPaperResponse publishPaper(
            Long paperId
    ) {
        ResearchPaper paper = getPaperEntity(paperId);

        if (paper.getStatus() != ResearchStatus.APPROVED) {
            throw new InvalidOperationException(
                    "Only approved papers can be published"
            );
        }

        paper.setStatus(
                ResearchStatus.PUBLISHED
        );

        paper.setPublishedAt(LocalDateTime.now());

        ResearchPaper saved = paperRepository.save(paper);
        auditLogger.logPaperPublished(saved, currentUserService.getCurrentUser().getId());
        eventPublisher.publishResearchPublished(saved);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ResearchPaperSummaryResponse> search(
            ResearchPaperSearchRequest request,
            Pageable pageable
    ) {
        Specification<ResearchPaper> spec = ResearchPaperSpecification.build(
                request
        );

        Page<ResearchPaper> papers = paperRepository.findAll(spec, pageable);
        return papers.map(mapper::toSummary);
    }


    @Transactional
    public UploadPaperResponse uploadPaper(
            Long paperId,
            MultipartFile file
    ) {
        CurrentUser user = currentUserService.getCurrentUser();

        ResearchPaper paper = getPaperEntity(paperId);

        validateFileNotAlreadyUploaded(paper);
        validateOwnership(paper);
        validateUploadStatus(paper);
        validatePdf(file);

        String objectKey = buildObjectKey(
                paper.getId(),
                paper.getVersionNumber()
        );

        String storageKey = fileStorageService.uploadFile(
                file,
                objectKey
        );

        paper.setFileName(file.getOriginalFilename());
        paper.setContentType(file.getContentType());
        paper.setFileSize(file.getSize());
        paper.setStorageKey(storageKey);

        ResearchPaper saved = paperRepository.save(paper);
        auditLogger.logPaperUploaded(saved, user.getId());
        eventPublisher.publishPaperUploaded(saved);

        return UploadPaperResponse.builder()
                .paperId(saved.getId())
                .fileName(saved.getFileName())
                .contentType(saved.getContentType())
                .fileSize(saved.getFileSize())
                .versionNumber(saved.getVersionNumber())
                .storageKey(saved.getStorageKey())
                .build();
    }

    @Transactional
    public FileDownloadResponse downloadPaper(
            Long paperId
    ) {
        ResearchPaper paper = getPaperEntity(paperId);
        validateDownloadEligibility(paper);

        if (!fileStorageService.exists(paper.getStorageKey())) {

            throw new FileStorageException(
                    "Research file does not exist in storage."
            );
        }

        InputStream inputStream = fileStorageService.downloadFile(
                paper.getStorageKey()
        );

        paper.setDownloadCount(paper.getDownloadCount() + 1);

        paperRepository.save(paper);

        auditLogger.logPaperDownloaded(
                paper,
                currentUserService.getCurrentUser().getId()
        );

        eventPublisher.publishPaperDownloaded(paper);

        return FileDownloadResponse.builder()
                .inputStream(inputStream)
                .fileName(paper.getFileName())
                .contentType(paper.getContentType())
                .fileSize(paper.getFileSize())
                .build();
    }

    @Transactional
    public ResearchPaperResponse deleteUploadedFile(
            Long paperId
    ) {
        ResearchPaper paper = getPaperEntity(paperId);
        validateOwnership(paper);

        if (paper.getStorageKey() == null) {
            throw new InvalidOperationException("No uploaded document exists.");
        }
        if (fileStorageService.exists(paper.getStorageKey())) {
            fileStorageService.deleteFile(paper.getStorageKey());
        }

        paper.setStorageKey(null);
        paper.setFileName(null);
        paper.setContentType(null);
        paper.setFileSize(null);

        ResearchPaper saved = paperRepository.save(paper);
        auditLogger.logPaperFileDeleted(
                saved,
                currentUserService.getCurrentUser().getId()
        );
        eventPublisher.publishPaperFileDeleted(saved);

        return mapper.toResponse(saved);
    }
}
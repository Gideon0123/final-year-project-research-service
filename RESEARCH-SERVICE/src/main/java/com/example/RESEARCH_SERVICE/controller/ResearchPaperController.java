package com.example.RESEARCH_SERVICE.controller;

import com.example.RESEARCH_SERVICE.dto.*;
import com.example.RESEARCH_SERVICE.payload.PagedResponse;
import com.example.RESEARCH_SERVICE.service.ResearchPaperService;
import com.example.RESEARCH_SERVICE.utils.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/research/papers")
@RequiredArgsConstructor
@Validated
public class ResearchPaperController {

    private final ResearchPaperService paperService;

    @PostMapping
    public ResponseEntity <ApiResponse<ResearchPaperResponse>> createPaper(
            @Valid @RequestBody CreateResearchPaperRequest request,
            HttpServletRequest httpRequest
    ) {
        ResearchPaperResponse paperResponse = paperService.createPaper(request);

        ApiResponse<ResearchPaperResponse> apiResponse =
                ApiResponse.<ResearchPaperResponse>builder()
                        .success(true)
                        .message("Research Paper created successfully")
                        .status(HttpStatus.CREATED.value())
                        .data(paperResponse)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/{paperId}")
    public ResponseEntity<ApiResponse<ResearchPaperResponse>> getPaperById(
            @PathVariable Long paperId,
            HttpServletRequest request
    ) {
        ResearchPaperResponse response = paperService.getPaperById(
                paperId
        );

        ApiResponse<ResearchPaperResponse> apiResponse =
                ApiResponse.<ResearchPaperResponse>builder()
                        .success(true)
                        .message("Research paper retrieved successfully")
                        .status(HttpStatus.OK.value())
                        .data(response)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ResearchPaperSummaryResponse>>> getAllPapers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            HttpServletRequest request
    ) {
        int adjustedPage = Math.max(page - 1, 0);
        PagedResponse<ResearchPaperSummaryResponse> papers = paperService.getAllPapers(
                adjustedPage,
                size,
                sortBy,
                sortDirection
        );

        PagedResponse<ResearchPaperSummaryResponse> response =
                PagedResponse.<ResearchPaperSummaryResponse>builder()
                .content(papers.getContent())
                .size(papers.getSize())
                .page(papers.getPage())
                .first(papers.isFirst())
                .last(papers.isLast())
                .totalElements(papers.getTotalElements())
                .totalPages(papers.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<ResearchPaperSummaryResponse>>builder()
                        .success(true)
                        .message("Researches fetched successfully")
                        .status(HttpStatus.OK.value())
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/my-papers")
    public ResponseEntity<
            ApiResponse<PagedResponse<ResearchPaperSummaryResponse>>> getMyPapers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            HttpServletRequest request
    ) {
        int adjustedPage = Math.max(page - 1, 0);
        PagedResponse<ResearchPaperSummaryResponse> papers = paperService.getMyPapers(
                adjustedPage,
                size,
                sortBy,
                sortDirection
        );

        PagedResponse<ResearchPaperSummaryResponse> response =
                PagedResponse.<ResearchPaperSummaryResponse>builder()
                        .content(papers.getContent())
                        .size(papers.getSize())
                        .page(papers.getPage())
                        .first(papers.isFirst())
                        .last(papers.isLast())
                        .totalElements(papers.getTotalElements())
                        .totalPages(papers.getTotalPages())
                        .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<ResearchPaperSummaryResponse>>builder()
                        .success(true)
                        .message("My Research Papers fetched successfully")
                        .status(HttpStatus.OK.value())
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PutMapping("/{paperId}")
    public ResponseEntity<ApiResponse<ResearchPaperResponse>> updatePaper(
            @PathVariable Long paperId,
            @Valid @RequestBody UpdateResearchPaperRequest request,
            HttpServletRequest httpRequest
    ) {
        ResearchPaperResponse paperResponse = paperService.updatePaper(paperId, request);

        return ResponseEntity.ok(
                ApiResponse.<ResearchPaperResponse>builder()
                        .success(true)
                        .status(HttpStatus.OK.value())
                        .message("Research Paper updated successfully")
                        .data(paperResponse)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @DeleteMapping("/{paperId}")
    public ResponseEntity<ApiResponse<Void>> deletePaper(
            @PathVariable Long paperId,
            HttpServletRequest request
    ) {
        paperService.deletePaper(paperId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Research paper deleted successfully")
                .status(HttpStatus.OK.value())
                .path(request.getRequestURI())
                .traceId(TraceIdUtil.generate())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{paperId}/submit")
    public ResponseEntity<ApiResponse<ResearchPaperResponse>> submitPaper(
            @PathVariable Long paperId,
            HttpServletRequest request
    ) {
        ResearchPaperResponse response = paperService.submitPaper(
                paperId
        );

        return ResponseEntity.ok(
                ApiResponse.<ResearchPaperResponse>builder()
                        .success(true)
                        .status(HttpStatus.OK.value())
                        .message("Research Paper Submitted Successfully")
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{paperId}/assign-reviewer")
    public ResponseEntity<ApiResponse<ResearchPaperResponse>> assignReviewer(
            @PathVariable Long paperId,
            @Valid @RequestBody AssignReviewerRequest request,
            HttpServletRequest httpRequest
    ) {
        ResearchPaperResponse response = paperService.assignReviewer(
                paperId, request
        );

        return ResponseEntity.ok(
                ApiResponse.<ResearchPaperResponse>builder()
                        .success(true)
                        .status(HttpStatus.OK.value())
                        .message("Research Paper Reviewer Successfully Assigned")
                        .data(response)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @PatchMapping("/{paperId}/status")
    public ResponseEntity<ApiResponse<ResearchPaperResponse>> changeStatus(
            @PathVariable Long paperId,
            @Valid @RequestBody ChangeResearchStatusRequest request,
            HttpServletRequest httpRequest
    ) {
        ResearchPaperResponse response = paperService.changeStatus(
                paperId, request
        );

        return ResponseEntity.ok(
                ApiResponse.<ResearchPaperResponse>builder()
                        .success(true)
                        .status(HttpStatus.OK.value())
                        .message("Research Paper Status Successfully Changed")
                        .data(response)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @PatchMapping("/{paperId}/publish")
    public ResponseEntity<ApiResponse<ResearchPaperResponse>> publishPaper(
            @PathVariable Long paperId,
            HttpServletRequest httpRequest
    ) {
        ResearchPaperResponse response = paperService.publishPaper(paperId);

        return ResponseEntity.ok(
                ApiResponse.<ResearchPaperResponse>builder()
                        .success(true)
                        .status(HttpStatus.OK.value())
                        .message("Research Paper Status Successfully Published")
                        .data(response)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<ResearchPaperSummaryResponse>>> search(
            ResearchPaperSearchRequest request,

            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,

            HttpServletRequest httpRequest
    ) {
        int adjustedPage = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(
                adjustedPage,
                size,
                Sort.by(sortBy)
        );

        PagedResponse<ResearchPaperSummaryResponse> papers = paperService.search(request, pageable);

        PagedResponse<ResearchPaperSummaryResponse> response =
                PagedResponse.<ResearchPaperSummaryResponse>builder()
                        .content(papers.getContent())
                        .size(papers.getSize())
                        .page(papers.getPage())
                        .first(papers.isFirst())
                        .last(papers.isLast())
                        .totalElements(papers.getTotalElements())
                        .totalPages(papers.getTotalPages())
                        .build();

        return ResponseEntity.ok(

                ApiResponse.<PagedResponse<ResearchPaperSummaryResponse>>builder()
                        .success(true)
                        .message("Research Papers fetched successfully")
                        .status(HttpStatus.OK.value())
                        .data(response)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PostMapping(
            value = "/{paperId}/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<UploadPaperResponse>> uploadPaper(
            @PathVariable Long paperId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) {
        UploadPaperResponse response = paperService.uploadPaper(
                paperId,
                file
        );

        return ResponseEntity.ok(
                ApiResponse.<UploadPaperResponse>builder()
                        .success(true)
                        .status(HttpStatus.OK.value())
                        .message("Paper uploaded successfully")
                        .data(response)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/{paperId}/download")
    public ResponseEntity<InputStreamResource> downloadPaper(
            @PathVariable Long paperId
    ) {
        FileDownloadResponse response = paperService.downloadPaper(paperId);

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(response.getContentType())
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                response.getFileName() +
                                "\""
                )
                .contentLength(response.getFileSize())
                .body(new InputStreamResource(response.getInputStream()));
    }

    @DeleteMapping("/{paperId}/file")
    public ResponseEntity<ApiResponse<ResearchPaperResponse>> deleteUploadedFile(
            @PathVariable Long paperId,
            HttpServletRequest request
    ) {
        ResearchPaperResponse response = paperService.deleteUploadedFile(
                paperId
        );

        return ResponseEntity.ok(
                ApiResponse.<ResearchPaperResponse>builder()
                        .success(true)
                        .message("Research document deleted successfully.")
                        .status(HttpStatus.OK.value())
                        .data(response)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
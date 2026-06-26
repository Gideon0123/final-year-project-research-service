package com.example.RESEARCH_SERVICE.controller;

import com.example.RESEARCH_SERVICE.dto.*;
import com.example.RESEARCH_SERVICE.payload.PagedResponse;
import com.example.RESEARCH_SERVICE.service.ResearchPaperService;
import com.example.RESEARCH_SERVICE.utils.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
}
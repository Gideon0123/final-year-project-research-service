package com.example.RESEARCH_SERVICE.controller;

import com.example.RESEARCH_SERVICE.dto.ApiResponse;
import com.example.RESEARCH_SERVICE.dto.CreateResearchPaperRequest;
import com.example.RESEARCH_SERVICE.dto.ResearchPaperResponse;
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
                        .status(201)
                        .data(paperResponse)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

}
package com.example.RESEARCH_SERVICE.controller;

import com.example.RESEARCH_SERVICE.dto.ApiResponse;
import com.example.RESEARCH_SERVICE.dto.CategoryResponse;
import com.example.RESEARCH_SERVICE.dto.CreateCategoryRequest;
import com.example.RESEARCH_SERVICE.dto.UpdateCategoryRequest;
import com.example.RESEARCH_SERVICE.payload.PagedResponse;
import com.example.RESEARCH_SERVICE.service.ResearchCategoryService;
import com.example.RESEARCH_SERVICE.utils.Idempotent;
import com.example.RESEARCH_SERVICE.utils.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/research/categories")
@RequiredArgsConstructor
public class ResearchCategoryController {

    private final ResearchCategoryService categoryService;

    @Idempotent(ttlMinutes = 2)
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request,
            HttpServletRequest httpRequest
    ) {
        CategoryResponse categoryResponse = categoryService.createCategory(request);

        ApiResponse<CategoryResponse> apiResponse =
                ApiResponse.<CategoryResponse>builder()
                        .success(true)
                        .message("Category created successfully")
                        .status(HttpStatus.CREATED.value())
                        .data(categoryResponse)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok().body(apiResponse);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(
            @PathVariable Long id,
            HttpServletRequest httpRequest
    ) {
        CategoryResponse categoryResponse = categoryService.getCategoryById(id);

        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .success(true)
                        .message("Category Fetched successfully")
                        .status(HttpStatus.OK.value())
                        .data(categoryResponse)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<CategoryResponse>>> getAllCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            HttpServletRequest request
    ) {
        int adjustedPage = Math.max(page - 1, 0);
        PagedResponse<CategoryResponse> categories =
                categoryService.getAllCategories(adjustedPage, size, sortBy);
        PagedResponse<CategoryResponse> response = PagedResponse.<CategoryResponse>builder()
                .content(categories.getContent())
                .size(categories.getSize())
                .page(categories.getPage())
                .first(categories.isFirst())
                .last(categories.isLast())
                .totalElements(categories.getTotalElements())
                .totalPages(categories.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<CategoryResponse>>builder()
                        .success(true)
                        .message("Categories fetched successfully")
                        .status(HttpStatus.OK.value())
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @Idempotent(ttlMinutes = 2)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @RequestBody UpdateCategoryRequest request,
            HttpServletRequest httpRequest
    ) {
        CategoryResponse categoryResponse = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .success(true)
                        .status(HttpStatus.OK.value())
                        .message("Category updated successfully")
                        .data(categoryResponse)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable Long id
    ) {
        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<CategoryResponse>>> searchCategory(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,

            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,

            HttpServletRequest request
    ) {

        int adjustedPage = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(
                adjustedPage,
                size,
                Sort.by(sortBy)
        );

        PagedResponse<CategoryResponse> categories = categoryService.searchCategories(
                keyword,
                id,
                name,
                pageable
        );

        PagedResponse<CategoryResponse> response = PagedResponse.<CategoryResponse>builder()
                .content(categories.getContent())
                .size(categories.getSize())
                .page(categories.getPage())
                .first(categories.isFirst())
                .last(categories.isLast())
                .totalElements(categories.getTotalElements())
                .totalPages(categories.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<CategoryResponse>>builder()
                        .success(true)
                        .message("Categories fetched successfully")
                        .status(HttpStatus.OK.value())
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
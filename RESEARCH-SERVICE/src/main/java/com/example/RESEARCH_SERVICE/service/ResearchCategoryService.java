package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.dto.CategoryResponse;
import com.example.RESEARCH_SERVICE.dto.CreateCategoryRequest;
import com.example.RESEARCH_SERVICE.dto.CurrentUser;
import com.example.RESEARCH_SERVICE.dto.UpdateCategoryRequest;
import com.example.RESEARCH_SERVICE.entity.ResearchCategory;
import com.example.RESEARCH_SERVICE.exception.AccessDeniedException;
import com.example.RESEARCH_SERVICE.exception.CategoryAlreadyExistsException;
import com.example.RESEARCH_SERVICE.exception.CategoryNotFoundException;
import com.example.RESEARCH_SERVICE.mapper.ResearchCategoryMapper;
import com.example.RESEARCH_SERVICE.payload.PagedResponse;
import com.example.RESEARCH_SERVICE.repository.ResearchCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ResearchCategoryService {

    private final ResearchCategoryRepository categoryRepository;
    private final ResearchCategoryMapper mapper;
    private final CurrentUserService currentUserService;

    public CategoryResponse createCategory(
            CreateCategoryRequest request
    ) {
        CurrentUser user = currentUserService.getCurrentUser();

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new AccessDeniedException("Only admins can create categories");
        }

        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new CategoryAlreadyExistsException("Category already exists");
        }

        ResearchCategory category = ResearchCategory.builder()
                .name(request.name())
                .description(request.description())
                .build();

        categoryRepository.save(category);

        return mapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(
            Long id
    ) {
        ResearchCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        return mapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public PagedResponse<CategoryResponse> getAllCategories(
            int page, int size, String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<CategoryResponse> categoryPage = categoryRepository.findAll(pageable)
                .map(mapper::toResponse);

        return new PagedResponse<>(categoryPage);
    }

    public CategoryResponse updateCategory(
            Long id,
            UpdateCategoryRequest request
    ) {
        CurrentUser user = currentUserService.getCurrentUser();

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {

            throw new AccessDeniedException("Only admins can update categories");
        }

        ResearchCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        if (request.name() != null && !request.name().equalsIgnoreCase(category.getName())) {

            if (categoryRepository.existsByNameIgnoreCase(request.name())) {
                throw new CategoryAlreadyExistsException("Category already exists");
            }
            category.setName(request.name());
        }

        if (request.description() != null) {
            category.setDescription(request.description());
        }

        categoryRepository.save(category);

        return mapper.toResponse(category);
    }

    public void deleteCategory(
            Long id
    ) {
        CurrentUser user = currentUserService.getCurrentUser();

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new AccessDeniedException("Only admins can delete categories");
        }

        ResearchCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        categoryRepository.delete(category);
    }
}

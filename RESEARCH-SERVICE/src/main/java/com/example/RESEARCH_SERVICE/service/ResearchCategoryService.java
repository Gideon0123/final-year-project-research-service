package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.dto.CategoryResponse;
import com.example.RESEARCH_SERVICE.dto.CreateCategoryRequest;
import com.example.RESEARCH_SERVICE.entity.CurrentUser;
import com.example.RESEARCH_SERVICE.dto.UpdateCategoryRequest;
import com.example.RESEARCH_SERVICE.entity.ResearchCategory;
import com.example.RESEARCH_SERVICE.exception.AccessDeniedException;
import com.example.RESEARCH_SERVICE.exception.CategoryAlreadyExistsException;
import com.example.RESEARCH_SERVICE.exception.CategoryNotFoundException;
import com.example.RESEARCH_SERVICE.mapper.ResearchCategoryMapper;
import com.example.RESEARCH_SERVICE.payload.PagedResponse;
import com.example.RESEARCH_SERVICE.repository.ResearchCategoryRepository;
import com.example.RESEARCH_SERVICE.repository.specification.ResearchCategorySpecBuilder;
import com.example.RESEARCH_SERVICE.utils.CacheKeys;
import com.example.RESEARCH_SERVICE.utils.CacheNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    @Caching(
            evict = {
                    @CacheEvict(
                            value = CacheNames.RESEARCH_CATEGORY,
                            key = CacheKeys.CATEGORY_ALL
                    ),
                    @CacheEvict(
                            value = CacheNames.RESEARCH_CATEGORY_SEARCH,
                            allEntries = true
                    )
            }
    )
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

    @Cacheable(
            value = CacheNames.RESEARCH_CATEGORY,
            key = CacheKeys.CATEGORY_ID
    )
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(
            Long id
    ) {
        ResearchCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        System.out.println("Coming From DataBase 2");

        return mapper.toResponse(category);
    }

    @Cacheable(
            value = CacheNames.RESEARCH_CATEGORY,
            key = CacheKeys.CATEGORY_ALL
    )
    @Transactional(readOnly = true)
    public PagedResponse<CategoryResponse> getAllCategories(
            int page, int size, String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<CategoryResponse> categoryPage = categoryRepository.findAll(pageable)
                .map(mapper::toResponse);
        System.out.println("Coming From DataBase 1");

        return new PagedResponse<>(categoryPage);
    }

    @Caching(
            evict = {

                    @CacheEvict(
                            value = CacheNames.RESEARCH_CATEGORY,
                            key = CacheKeys.CATEGORY_ID
                    ),

                    @CacheEvict(
                            value = CacheNames.RESEARCH_CATEGORY,
                            key = CacheKeys.CATEGORY_ALL
                    ),

                    @CacheEvict(
                            value = CacheNames.RESEARCH_CATEGORY_SEARCH,
                            allEntries = true
                    )
            }
    )
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

    @Caching(
            evict = {
                    @CacheEvict(
                            value = CacheNames.RESEARCH_CATEGORY,
                            key = CacheKeys.CATEGORY_ID
                    ),
                    @CacheEvict(
                            value = CacheNames.RESEARCH_CATEGORY,
                            key = CacheKeys.CATEGORY_ALL
                    ),
                    @CacheEvict(
                            value = CacheNames.RESEARCH_CATEGORY_SEARCH,
                            allEntries = true
                    )
            }
    )
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

    @Cacheable(
            value = CacheNames.RESEARCH_CATEGORY_SEARCH,
            key = CacheKeys.CATEGORY_SEARCH
    )
    @Transactional
    public PagedResponse<CategoryResponse> searchCategories(
            String keyword,
            Long id,
            String name,
            Pageable pageable
    ) {
        Specification<ResearchCategory> spec = ResearchCategorySpecBuilder.build(
                keyword,
                id,
                name
        );

        Page<CategoryResponse> page = categoryRepository.findAll(spec, pageable).map(mapper::toResponse);

        return new PagedResponse<>(page);
    }
}

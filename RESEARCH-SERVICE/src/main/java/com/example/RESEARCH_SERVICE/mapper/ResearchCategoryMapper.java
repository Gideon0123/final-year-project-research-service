package com.example.RESEARCH_SERVICE.mapper;

import com.example.RESEARCH_SERVICE.dto.CategoryResponse;
import com.example.RESEARCH_SERVICE.entity.ResearchCategory;
import org.springframework.stereotype.Component;

@Component
public class ResearchCategoryMapper {

    public CategoryResponse toResponse(
            ResearchCategory category
    ) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
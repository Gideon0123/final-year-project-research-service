package com.example.RESEARCH_SERVICE.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(

        @NotBlank
        String name,

        String description
) {
}

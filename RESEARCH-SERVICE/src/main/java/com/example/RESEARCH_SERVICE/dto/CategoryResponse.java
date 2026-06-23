package com.example.RESEARCH_SERVICE.dto;

import lombok.Builder;

@Builder
public record CategoryResponse(

        Long id,

        String name,

        String description
) {
}
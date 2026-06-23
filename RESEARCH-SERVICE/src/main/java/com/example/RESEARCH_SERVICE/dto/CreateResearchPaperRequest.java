package com.example.RESEARCH_SERVICE.dto;

import com.example.RESEARCH_SERVICE.enums.ResearchVisibility;
import jakarta.validation.constraints.NotBlank;

public record CreateResearchPaperRequest(

        @NotBlank
        String title,

        @NotBlank
        String abstractText,

        String keywords,

        Long categoryId,

        ResearchVisibility visibility
) {
}

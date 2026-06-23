package com.example.RESEARCH_SERVICE.dto;

import com.example.RESEARCH_SERVICE.enums.ResearchVisibility;

public record UpdateResearchPaperRequest(

        String title,

        String abstractText,

        String keywords,

        Long categoryId,

        ResearchVisibility visibility
) {
}
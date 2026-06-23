package com.example.RESEARCH_SERVICE.dto;

import com.example.RESEARCH_SERVICE.enums.ResearchStatus;
import com.example.RESEARCH_SERVICE.enums.ResearchVisibility;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResearchPaperResponse(

        Long id,

        String title,

        String abstractText,

        String keywords,

        String authorName,

        String authorEmail,

        String institution,

        String faculty,

        String department,

        String category,

        ResearchStatus status,

        ResearchVisibility visibility,

        Integer versionNumber,

        LocalDateTime submittedAt,

        LocalDateTime publishedAt,

        Long downloadCount,

        Long viewCount
) {
}
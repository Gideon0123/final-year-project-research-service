package com.example.RESEARCH_SERVICE.dto;

import com.example.RESEARCH_SERVICE.enums.ResearchStatus;
import com.example.RESEARCH_SERVICE.enums.ResearchVisibility;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResearchPaperSummaryResponse(

        Long id,
        String title,
        String authorName,
        String institution,
        String category,
        ResearchStatus status,
        ResearchVisibility visibility,
        LocalDateTime submittedAt,
        Long downloadCount,
        Long viewCount

) {}
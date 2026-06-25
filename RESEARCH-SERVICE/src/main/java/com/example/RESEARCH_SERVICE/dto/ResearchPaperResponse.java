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
        Long authorId,
        String authorName,
        String authorEmail,
        String institution,
        String faculty,
        String department,
        String category,
        String fileName,
        String contentType,
        Long fileSize,
        String storageKey,
        ResearchStatus status,
        ResearchVisibility visibility,
        Integer versionNumber,
        Long reviewerId,
        LocalDateTime submittedAt,
        LocalDateTime publishedAt,
        LocalDateTime reviewAssignedAt,
        LocalDateTime reviewCompletedAt,
        Long downloadCount,
        Long viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}
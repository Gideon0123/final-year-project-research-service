package com.example.RESEARCH_SERVICE.dto;

import lombok.Builder;

@Builder
public record ResearchFileResponse(

        Long paperId,
        String fileName,
        String contentType,
        Long fileSize

) {
}
package com.example.RESEARCH_SERVICE.dto;

import lombok.Builder;

@Builder
public record UploadPaperResponse(

        Long paperId,
        String fileName,
        String contentType,
        Long fileSize,
        Integer versionNumber,
        String storageKey
) {
}
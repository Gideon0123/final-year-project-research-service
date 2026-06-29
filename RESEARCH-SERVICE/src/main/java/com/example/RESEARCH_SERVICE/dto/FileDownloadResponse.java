package com.example.RESEARCH_SERVICE.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@AllArgsConstructor
@Builder
public class FileDownloadResponse {

    private InputStream inputStream;
    private String fileName;
    private String contentType;
    private Long fileSize;
}
package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.dto.ResearchFileResponse;

import java.io.InputStream;

public interface ResearchFileService {

    InputStream downloadResearchFile(Long paperId);
    ResearchFileResponse getResearchFileMetadata(Long paperId);
//    ResearchFileResponse getResearchFile(Long paperId);
}
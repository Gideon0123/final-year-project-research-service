package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.dto.ResearchCandidateResponse;
import com.example.RESEARCH_SERVICE.dto.ResearchFileResponse;

import java.io.InputStream;
import java.util.List;

public interface ResearchFileService {

    InputStream downloadResearchFile(Long paperId);
    ResearchFileResponse getResearchFileMetadata(Long paperId);
    List<ResearchCandidateResponse> getPublishedCandidates();
//    ResearchFileResponse getResearchFile(Long paperId);
}
package com.example.RESEARCH_SERVICE.mapper;

import com.example.RESEARCH_SERVICE.dto.ResearchPaperResponse;
import com.example.RESEARCH_SERVICE.entity.ResearchPaper;
import org.springframework.stereotype.Component;

@Component
public class ResearchPaperMapper {

    public ResearchPaperResponse toResponse(
            ResearchPaper paper
    ) {

        return ResearchPaperResponse.builder()
                .id(paper.getId())
                .title(paper.getTitle())
                .abstractText(paper.getAbstractText())
                .keywords(paper.getKeywords())
                .authorName(paper.getAuthorName())
                .authorEmail(paper.getAuthorEmail())
                .institution(paper.getInstitution())
                .faculty(paper.getFaculty())
                .department(paper.getDepartment())
                .status(paper.getStatus())
                .visibility(paper.getVisibility())
                .versionNumber(paper.getVersionNumber())
                .submittedAt(paper.getSubmittedAt())
                .publishedAt(paper.getPublishedAt())
                .downloadCount(paper.getDownloadCount())
                .viewCount(paper.getViewCount())
                .build();
    }
}
package com.example.RESEARCH_SERVICE.mapper;

import com.example.RESEARCH_SERVICE.dto.ResearchPaperResponse;
import com.example.RESEARCH_SERVICE.dto.ResearchPaperSummaryResponse;
import com.example.RESEARCH_SERVICE.entity.ResearchCategory;
import com.example.RESEARCH_SERVICE.entity.ResearchPaper;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-27T10:33:11+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class ResearchPaperMapperImpl implements ResearchPaperMapper {

    @Override
    public ResearchPaperResponse toResponse(ResearchPaper paper) {
        if ( paper == null ) {
            return null;
        }

        ResearchPaperResponse.ResearchPaperResponseBuilder researchPaperResponse = ResearchPaperResponse.builder();

        researchPaperResponse.category( paperCategoryName( paper ) );
        researchPaperResponse.id( paper.getId() );
        researchPaperResponse.title( paper.getTitle() );
        researchPaperResponse.abstractText( paper.getAbstractText() );
        researchPaperResponse.keywords( paper.getKeywords() );
        researchPaperResponse.authorId( paper.getAuthorId() );
        researchPaperResponse.authorName( paper.getAuthorName() );
        researchPaperResponse.authorEmail( paper.getAuthorEmail() );
        researchPaperResponse.institution( paper.getInstitution() );
        researchPaperResponse.faculty( paper.getFaculty() );
        researchPaperResponse.department( paper.getDepartment() );
        researchPaperResponse.fileName( paper.getFileName() );
        researchPaperResponse.contentType( paper.getContentType() );
        researchPaperResponse.fileSize( paper.getFileSize() );
        researchPaperResponse.storageKey( paper.getStorageKey() );
        researchPaperResponse.status( paper.getStatus() );
        researchPaperResponse.visibility( paper.getVisibility() );
        researchPaperResponse.versionNumber( paper.getVersionNumber() );
        researchPaperResponse.reviewerId( paper.getReviewerId() );
        researchPaperResponse.submittedAt( paper.getSubmittedAt() );
        researchPaperResponse.publishedAt( paper.getPublishedAt() );
        researchPaperResponse.reviewAssignedAt( paper.getReviewAssignedAt() );
        researchPaperResponse.reviewCompletedAt( paper.getReviewCompletedAt() );
        researchPaperResponse.downloadCount( paper.getDownloadCount() );
        researchPaperResponse.viewCount( paper.getViewCount() );
        researchPaperResponse.createdAt( paper.getCreatedAt() );
        researchPaperResponse.updatedAt( paper.getUpdatedAt() );

        return researchPaperResponse.build();
    }

    @Override
    public ResearchPaperSummaryResponse toSummary(ResearchPaper paper) {
        if ( paper == null ) {
            return null;
        }

        ResearchPaperSummaryResponse.ResearchPaperSummaryResponseBuilder researchPaperSummaryResponse = ResearchPaperSummaryResponse.builder();

        researchPaperSummaryResponse.category( paperCategoryName( paper ) );
        researchPaperSummaryResponse.id( paper.getId() );
        researchPaperSummaryResponse.title( paper.getTitle() );
        researchPaperSummaryResponse.authorName( paper.getAuthorName() );
        researchPaperSummaryResponse.institution( paper.getInstitution() );
        researchPaperSummaryResponse.status( paper.getStatus() );
        researchPaperSummaryResponse.visibility( paper.getVisibility() );
        researchPaperSummaryResponse.submittedAt( paper.getSubmittedAt() );
        researchPaperSummaryResponse.downloadCount( paper.getDownloadCount() );
        researchPaperSummaryResponse.viewCount( paper.getViewCount() );

        return researchPaperSummaryResponse.build();
    }

    private String paperCategoryName(ResearchPaper researchPaper) {
        ResearchCategory category = researchPaper.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getName();
    }
}

package com.example.RESEARCH_SERVICE.publisher;

import com.example.RESEARCH_SERVICE.entity.ResearchPaper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ResearchEventPublisherImpl implements ResearchEventPublisher {

    @Override
    public void publishResearchCreated(
            ResearchPaper paper
    ) {
        log.info(
                "Research paper created: {}",
                paper.getId()
        );
    }

    @Override
    public void publishResearchUpdated(ResearchPaper paper) {
        log.info(
                "Research paper updated: {}",
                paper.getId()
        );
    }

    @Override
    public void publishResearchDeleted(ResearchPaper paper) {
        log.info(
                "Research paper deleted: {}",
                paper.getId()
        );
    }

    @Override
    public void publishResearchSubmitted(ResearchPaper paper) {
        log.info(
                "Research paper submitted: {}",
                paper.getId()
        );
    }

    @Override
    public void publishReviewerAssigned(ResearchPaper paper) {
        log.info(
                "Reviewer Assigned: {}",
                paper.getId()
        );
    }

    @Override
    public void publishReviewCompleted(ResearchPaper paper) {
        log.info(
                "Review Completed: {}",
                paper.getId()
        );
    }

    @Override
    public void publishResearchPublished(ResearchPaper paper) {
        log.info(
                "Research Published: {}",
                paper.getId()
        );
    }

    @Override
    public void publishPaperUploaded(
            ResearchPaper paper
    ) {
        log.info(
                "Publishing paper uploaded event for paper {}",
                paper.getId()
        );
    }
}
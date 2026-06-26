package com.example.RESEARCH_SERVICE.utils;

import com.example.RESEARCH_SERVICE.entity.ResearchPaper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResearchAuditLogger {

    public void logPaperCreated(
            ResearchPaper paper,
            Long actorId
    ) {

        log.info(
                """
                RESEARCH CREATED
                paperId={}
                title={}
                actor={}
                """,
                paper.getId(),
                paper.getTitle(),
                actorId
        );
    }

    public void logPaperUpdated(
            ResearchPaper paper,
            Long actorId
    ) {

        log.info(
                """
                RESEARCH UPDATED
                paperId={}
                title={}
                actor={}
                """,
                paper.getId(),
                paper.getTitle(),
                actorId
        );
    }

    public void logPaperDeleted(
            ResearchPaper paper,
            Long actorId
    ) {

        log.info(
                """
                RESEARCH DELETED
                paperId={}
                title={}
                actor={}
                """,
                paper.getId(),
                paper.getTitle(),
                actorId
        );
    }

    public void logPaperSubmitted(
            ResearchPaper paper,
            Long actorId
    ) {

        log.info(
                """
                RESEARCH SUBMITTED
                paperId={}
                title={}
                actor={}
                """,
                paper.getId(),
                paper.getTitle(),
                actorId
        );
    }
}
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
}
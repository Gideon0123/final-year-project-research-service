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
}
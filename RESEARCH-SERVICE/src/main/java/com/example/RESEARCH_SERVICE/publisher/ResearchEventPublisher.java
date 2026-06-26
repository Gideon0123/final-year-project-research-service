package com.example.RESEARCH_SERVICE.publisher;

import com.example.RESEARCH_SERVICE.entity.ResearchPaper;

public interface ResearchEventPublisher {

    void publishResearchCreated(ResearchPaper paper);
    void publishResearchUpdated(ResearchPaper paper);
    void publishResearchDeleted(ResearchPaper paper);
    void publishResearchSubmitted(ResearchPaper paper);

}
package com.example.RESEARCH_SERVICE.publisher;

import com.example.RESEARCH_SERVICE.entity.ResearchPaper;

public interface ResearchEventPublisher {

    void publishResearchCreated(ResearchPaper paper);

}
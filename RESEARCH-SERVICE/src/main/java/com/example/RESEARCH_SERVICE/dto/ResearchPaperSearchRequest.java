package com.example.RESEARCH_SERVICE.dto;

import com.example.RESEARCH_SERVICE.enums.ResearchStatus;
import com.example.RESEARCH_SERVICE.enums.ResearchVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchPaperSearchRequest {

    private String keyword;
    private Long categoryId;
    private ResearchStatus status;
    private ResearchVisibility visibility;
    private Long authorId;
    private Long reviewerId;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
}
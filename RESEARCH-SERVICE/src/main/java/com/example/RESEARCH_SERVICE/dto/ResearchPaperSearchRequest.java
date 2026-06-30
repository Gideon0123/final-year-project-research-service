package com.example.RESEARCH_SERVICE.dto;

import com.example.RESEARCH_SERVICE.enums.ResearchStatus;
import com.example.RESEARCH_SERVICE.enums.ResearchVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

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

    public String toCacheKey() {

        return String.join(":",
                Objects.toString(keyword, ""),
                Objects.toString(categoryId, ""),
                Objects.toString(status, ""),
                Objects.toString(visibility, ""),
                Objects.toString(authorId, ""),
                Objects.toString(reviewerId, ""),
                Objects.toString(createdBefore, ""),
                Objects.toString(createdAfter, "")
        );

    }
}
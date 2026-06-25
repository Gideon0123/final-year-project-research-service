package com.example.RESEARCH_SERVICE.dto;

import com.example.RESEARCH_SERVICE.enums.ResearchVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateResearchPaperRequest {

        @NotBlank
        private String title;

        @NotBlank
        private String abstractText;

        @NotBlank
        private String keywords;

        @NotNull
        private Long categoryId;

        @NotNull
        private ResearchVisibility visibility;
}
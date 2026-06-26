package com.example.RESEARCH_SERVICE.dto;

import com.example.RESEARCH_SERVICE.enums.ResearchStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeResearchStatusRequest {

    @NotNull
    private ResearchStatus status;
}
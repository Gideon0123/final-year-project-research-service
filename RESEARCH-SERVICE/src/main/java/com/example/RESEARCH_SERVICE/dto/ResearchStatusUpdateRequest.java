package com.example.RESEARCH_SERVICE.dto;

import com.example.RESEARCH_SERVICE.enums.ResearchStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchStatusUpdateRequest {

    private ResearchStatus status;

}
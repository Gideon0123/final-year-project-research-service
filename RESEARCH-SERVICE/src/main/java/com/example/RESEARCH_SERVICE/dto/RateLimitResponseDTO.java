package com.example.RESEARCH_SERVICE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateLimitResponseDTO {

    private boolean allowed;
    private long remaining;
    private long limit;
    private long resetTime;

}
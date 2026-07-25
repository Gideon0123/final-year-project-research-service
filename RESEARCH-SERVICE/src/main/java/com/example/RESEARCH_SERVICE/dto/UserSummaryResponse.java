package com.example.RESEARCH_SERVICE.dto;

import lombok.Builder;

@Builder
public record UserSummaryResponse(

        Long id,
        String fullName,
        String email,
        String institution,
        String faculty,
        String department

) {}
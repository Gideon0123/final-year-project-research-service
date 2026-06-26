package com.example.RESEARCH_SERVICE.dto;

import lombok.Builder;

@Builder
public record UserProfileResponse(

        Long id,

        String fullName,

        String email,

        String institution,

        String faculty,

        String department

) {}
package com.example.RESEARCH_SERVICE.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CurrentUser {

    private Long id;

    private String email;

    private String role;
}
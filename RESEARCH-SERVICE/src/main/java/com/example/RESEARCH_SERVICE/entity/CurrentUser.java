package com.example.RESEARCH_SERVICE.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@AllArgsConstructor
public class CurrentUser {

    private Long id;
    private String email;
    private String role;
}
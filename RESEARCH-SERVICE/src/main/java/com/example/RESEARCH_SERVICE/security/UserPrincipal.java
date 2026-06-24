package com.example.RESEARCH_SERVICE.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserPrincipal {

    private Long userId;
    private String email;
    private String role;
}
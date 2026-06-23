package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.dto.CurrentUser;
import com.example.RESEARCH_SERVICE.exception.AccessDeniedException;
import com.example.RESEARCH_SERVICE.utils.GatewayHeaders;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final HttpServletRequest request;

    public CurrentUser getCurrentUser() {

        String userId = request.getHeader(
                GatewayHeaders.USER_ID
        );

        String email = request.getHeader(
                GatewayHeaders.USER_EMAIL
        );

        String role = request.getHeader(
                GatewayHeaders.USER_ROLE
        );

        if (userId == null || email == null) {

            throw new AccessDeniedException("User information missing");
        }

        return CurrentUser.builder()
                .id(Long.valueOf(userId))
                .email(email)
                .role(role)
                .build();
    }
}
package com.example.RESEARCH_SERVICE.utils;

import com.example.RESEARCH_SERVICE.entity.CurrentUser;
import com.example.RESEARCH_SERVICE.entity.RequestFingerprint;
import com.example.RESEARCH_SERVICE.exception.MissingIdempotencyKeyException;
import com.example.RESEARCH_SERVICE.service.CurrentUserService;
import com.example.RESEARCH_SERVICE.service.IdempotencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
public class IdempotencyAspect {

    private final HttpServletRequest request;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;
    private final CurrentUserService currentUserService;

    private String extractKey() {

        String key = request.getHeader("Idempotency-Key");

        if (key == null || key.isBlank()) {
            throw new MissingIdempotencyKeyException(
                    "Idempotency-Key header is required."
            );
        }

        return key;
    }

//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//    CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
    CurrentUser user = currentUserService.getCurrentUser();

    Long userId = user.getId();

    private Long getUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        assert authentication != null;
        CurrentUser principal = (CurrentUser) authentication.getPrincipal();

        assert principal != null;
        return principal.getId();

    }

    private RequestFingerprint buildFingerprint() {

        return RequestFingerprint.builder()
                .userId(getUserId())
                .httpMethod(request.getMethod())
                .endpoint(request.getRequestURI())
                .requestBody(
                        request.getAttribute(
                                CachedBodyFilter.REQUEST_BODY
                        ).toString()
                )
                .build();
    }

}

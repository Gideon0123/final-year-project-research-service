package com.example.RESEARCH_SERVICE.utils;

import com.example.RESEARCH_SERVICE.entity.IdempotencyResult;
import com.example.RESEARCH_SERVICE.entity.RequestFingerprint;
import com.example.RESEARCH_SERVICE.exception.MissingIdempotencyKeyException;
import com.example.RESEARCH_SERVICE.service.CurrentUserService;
import com.example.RESEARCH_SERVICE.service.IdempotencyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private final HttpServletRequest request;

    private final CurrentUserService currentUserService;

    private final IdempotencyService idempotencyService;

    private final ObjectMapper objectMapper;

    @Around("@annotation(com.example.RESEARCH_SERVICE.utils.Idempotent)")
    public Object handleIdempotency(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {

        System.out.println("INSIDE IDEMPOTENCY ASPECT");

        String key = extractKey();

        Long userId = getUserId();

        RequestFingerprint fingerprint =
                buildFingerprint(joinPoint);

        IdempotencyResult result =
                idempotencyService.begin(
                        key,
                        fingerprint
                );

        if (result.isCompleted()) {
            return idempotencyService.buildCachedResponse(
                    userId,
                    key
            );
        }

        Object response;

        try {

            response = joinPoint.proceed();

        }
        catch (Throwable ex){

            idempotencyService.fail(

                    userId,

                    key

            );

            throw ex;

        }

        idempotencyService.complete(
                userId,
                key,
                response
        );

        return response;

    }

    private String extractKey() {

        String key =
                request.getHeader("Idempotency-Key");

        if (key == null || key.isBlank()) {

            throw new MissingIdempotencyKeyException(
                    "Idempotency-Key header is required."
            );

        }

        return key;

    }

    private Long getUserId() {

        return currentUserService
                .getCurrentUser()
                .getId();

    }

    private RequestFingerprint buildFingerprint(

            ProceedingJoinPoint joinPoint

    ) throws JsonProcessingException {

        List<Object> args = Arrays.stream(joinPoint.getArgs())

                .filter(arg -> !(arg instanceof HttpServletRequest))

                .filter(arg -> !(arg instanceof HttpServletResponse))

                .filter(arg -> !(arg instanceof BindingResult))

                .toList();

        return RequestFingerprint.builder()

                .userId(getUserId())

                .httpMethod(request.getMethod())

                .endpoint(request.getRequestURI())

                .requestBody(
                        objectMapper.writeValueAsString(args)
                )

                .build();

    }

}
package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.entity.IdempotencyRecord;
import com.example.RESEARCH_SERVICE.entity.IdempotencyResult;
import com.example.RESEARCH_SERVICE.entity.RequestFingerprint;
import com.example.RESEARCH_SERVICE.enums.IdempotencyState;
import com.example.RESEARCH_SERVICE.enums.IdempotencyStatus;
import com.example.RESEARCH_SERVICE.exception.IdempotencyConflictException;
import com.example.RESEARCH_SERVICE.exception.IdempotencyProcessingException;
import com.example.RESEARCH_SERVICE.repository.IdempotencyRepository;
import com.example.RESEARCH_SERVICE.utils.IdempotencyProperties;
import com.example.RESEARCH_SERVICE.utils.IdempotencyStateResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRepository repository;
    private final FingerprintService fingerprintService;
    private final IdempotencyProperties properties;
    private final ObjectMapper objectMapper;
    private final IdempotencyStateResolver stateResolver;

    public IdempotencyResult begin(
            String idempotencyKey,
            RequestFingerprint fingerprint
    ) {
        Long userId = fingerprint.getUserId();

        String fingerprintHash =
                fingerprintService.generate(fingerprint);

        Optional<IdempotencyRecord> existing =
                repository.find(
                        userId,
                        idempotencyKey
                );

        IdempotencyState state = stateResolver.resolve(
                existing,
                fingerprintHash
        );

        return switch (state) {

            case NOT_FOUND -> {

                createProcessingRecord(
                        userId,
                        idempotencyKey,
                        fingerprintHash
                );

                yield IdempotencyResult.builder()
                        .proceed(true)
                        .build();
            }

            case PROCESSING ->
                    throw new IdempotencyProcessingException(
                            "Request already processing."
                    );

            case FINGERPRINT_MISMATCH ->
                    throw new IdempotencyConflictException(
                            "Idempotency-Key already used with another request."
                    );

            case COMPLETED ->

                    IdempotencyResult.builder()
                            .completed(true)
                            .record(existing.get())
                            .build();
        };

    }

    public String getCachedResponse(
            Long userId,
            String key
    ) {
        return repository.find(userId, key)
                .orElseThrow()
                .getResponseBody();
    }

    public void complete(
            Long userId,
            String key,
            Object response,
            int status
    ) throws JsonProcessingException {
        IdempotencyRecord record = repository.find(userId, key)
                .orElseThrow();

        record.setStatus(IdempotencyStatus.COMPLETED);
        record.setResponseBody(objectMapper.writeValueAsString(response));
        record.setContentType(MediaType.APPLICATION_JSON_VALUE);
        record.setHttpStatus(status);
        record.setCompletedAt(LocalDateTime.now());

        repository.save(
                userId,
                key,
                record,
                Duration.ofMinutes(properties.getExpirationMinutes())
        );

    }

    public void fail(
            Long userId,
            String key
    ) {
        repository.delete(
                userId,
                key
        );
    }

    public ResponseEntity<String> buildCachedResponse(
            Long userId,
            String key
    ) {

        IdempotencyRecord record =
                repository.find(userId, key)
                        .orElseThrow();

        return ResponseEntity
                .status(record.getHttpStatus())
                .contentType(
                        MediaType.valueOf(record.getContentType())
                )
                .body(record.getResponseBody());

    }

    private void createProcessingRecord(
            Long userId,
            String key,
            String fingerprint
    ) {
        IdempotencyRecord record =
                IdempotencyRecord.builder()
                        .userId(userId)
                        .key(key)
                        .fingerprint(fingerprint)
                        .status(IdempotencyStatus.PROCESSING)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now()
                                .plusMinutes(
                                        properties.getExpirationMinutes()
                                )
                        )
                        .build();

        repository.save(
                userId,
                key,
                record,
                Duration.ofMinutes(
                        properties.getExpirationMinutes()
                )
        );
    }

    private void validateFingerprint(
            IdempotencyRecord record,
            String currentFingerprint
    ) {
        if (!record.getFingerprint().equals(currentFingerprint)) {
            throw new IdempotencyConflictException(
                    "Idempotency-Key has already been used with another request."
            );

        }

    }

    public int getCachedStatus(
            Long userId,
            String key
    ) {
        return repository.find(userId, key)
                .orElseThrow()
                .getHttpStatus();
    }

}
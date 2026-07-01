package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.entity.IdempotencyRecord;
import com.example.RESEARCH_SERVICE.entity.IdempotencyResult;
import com.example.RESEARCH_SERVICE.entity.RequestFingerprint;
import com.example.RESEARCH_SERVICE.enums.IdempotencyStatus;
import com.example.RESEARCH_SERVICE.exception.IdempotencyConflictException;
import com.example.RESEARCH_SERVICE.exception.IdempotencyProcessingException;
import com.example.RESEARCH_SERVICE.repository.IdempotencyRepository;
import com.example.RESEARCH_SERVICE.utils.IdempotencyProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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

    public IdempotencyResult begin(
            String idempotencyKey,
            RequestFingerprint fingerprint
    ) {
        String fingerprintHash = fingerprintService.generate(fingerprint);
        Optional<IdempotencyRecord> existing = repository.find(fingerprint.getUserId(), idempotencyKey);

        if (existing.isEmpty()) {
            createProcessingRecord(
                    idempotencyKey,
                    fingerprintHash
            );

            return IdempotencyResult.builder()
                    .proceed(true)
                    .completed(false)
                    .build();

        }

        IdempotencyRecord record = existing.get();

        validateFingerprint(
                record,
                fingerprintHash
        );

        if (record.getStatus() == IdempotencyStatus.PROCESSING) {
            throw new IdempotencyProcessingException(
                    "Request is already being processed."
            );
        }

        if (record.getStatus() == IdempotencyStatus.COMPLETED) {

            return IdempotencyResult.builder()
                    .completed(true)
                    .record(record)
                    .build();

        }

        createProcessingRecord(idempotencyKey, fingerprintHash);

        return IdempotencyResult.builder()
                .proceed(true)
                .build();

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

    private void createProcessingRecord(
            String key,
            String fingerprint
    ) {
        IdempotencyRecord record = IdempotencyRecord.builder()
                .key(key)
                .status(IdempotencyStatus.PROCESSING)
                .fingerprint(fingerprint)
                .createdAt(LocalDateTime.now())
                .expiresAt(
                        LocalDateTime.now()
                                .plusMinutes(
                                        properties.getExpirationMinutes()
                                )

                )
                .build();

        Duration ttl = Duration.ofMinutes(
                properties.getExpirationMinutes()
        );

        repository.save(record.getUserId(), key, record, ttl);
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
package com.example.RESEARCH_SERVICE.entity;

import com.example.RESEARCH_SERVICE.enums.IdempotencyStatus;
import lombok.*;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyRecord {

    private Long userId;

    private String key;

    private String fingerprint;

    private IdempotencyStatus status;

    /**
     * Cached response body (JSON)
     */
    private String responseBody;

    /**
     * Cached HTTP status
     */
    private Integer httpStatus;

    /**
     * Usually application/json
     */
    @Builder.Default
    private String contentType = MediaType.APPLICATION_JSON_VALUE;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    private LocalDateTime expiresAt;

}
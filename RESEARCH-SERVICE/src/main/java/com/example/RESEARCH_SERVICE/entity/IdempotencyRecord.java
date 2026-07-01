package com.example.RESEARCH_SERVICE.entity;

import com.example.RESEARCH_SERVICE.enums.IdempotencyStatus;
import lombok.*;
import org.springframework.http.MediaType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyRecord implements Serializable {

    /**
     * Idempotency key supplied by client.
     */
    private String key;

    /**
     * Request fingerprint.
     */
    private String fingerprint;

    /**
     * Processing state.
     */
    private IdempotencyStatus status;

    /**
     * HTTP Status.
     */
    private Integer httpStatus;

    /**
     * Serialized JSON response.
     */
    private String responseBody;

    /**
     * Response Content-Type.
     */
    private String contentType = MediaType.APPLICATION_JSON_VALUE;

    /**
     * User who owns this request.
     */
    private Long userId;

    /**
     * Endpoint.
     */
    private String endpoint;

    /**
     * HTTP Method.
     */
    private String httpMethod;

    /**
     * Created.
     */
    private LocalDateTime createdAt;

    /**
     * Last updated.
     */
    private LocalDateTime updatedAt;

    private LocalDateTime expiresAt;

    private LocalDateTime completedAt;

}
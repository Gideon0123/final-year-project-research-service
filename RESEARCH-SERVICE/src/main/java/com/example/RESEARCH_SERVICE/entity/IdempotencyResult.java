package com.example.RESEARCH_SERVICE.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IdempotencyResult {

    /**
     * true if a cached response should be returned
     */
    private boolean completed;

    /**
     * true if request may proceed
     */
    private boolean proceed;

    /**
     * Existing Redis record
     */
    private IdempotencyRecord record;

}
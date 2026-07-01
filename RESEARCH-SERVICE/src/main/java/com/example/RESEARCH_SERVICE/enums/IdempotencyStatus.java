package com.example.RESEARCH_SERVICE.enums;

public enum IdempotencyStatus {

    /**
     * Request currently executing.
     */
    PROCESSING,

    /**
     * Successfully completed.
     */
    COMPLETED,

    /**
     * Failed.
     */
    FAILED

}
package com.example.RESEARCH_SERVICE.utils;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * Header containing the idempotency key.
     */
    String header() default "Idempotency-Key";

    /**
     * TTL of cached response.
     * seconds
     */
    long ttl() default 86400;

    /**
     * Whether request fingerprint should be validated.
     */
    boolean validatePayload() default true;

}
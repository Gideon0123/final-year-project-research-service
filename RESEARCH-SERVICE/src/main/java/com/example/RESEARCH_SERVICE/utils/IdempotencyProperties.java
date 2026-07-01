package com.example.RESEARCH_SERVICE.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "idempotency")
public class IdempotencyProperties {

    /**
     * Default expiration in minutes.
     */
    private long expirationMinutes = 30;

}
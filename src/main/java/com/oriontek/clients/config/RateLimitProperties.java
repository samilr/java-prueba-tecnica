package com.oriontek.clients.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rate-limit")
public record RateLimitProperties(Limit login, Limit standard) {

    public record Limit(int capacity, int refillPeriodSeconds) {}
}

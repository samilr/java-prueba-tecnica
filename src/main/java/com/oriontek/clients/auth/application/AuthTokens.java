package com.oriontek.clients.auth.application;

public record AuthTokens(String accessToken, String refreshToken, long expiresInSeconds) {}

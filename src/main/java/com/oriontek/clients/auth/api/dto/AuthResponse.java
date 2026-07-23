package com.oriontek.clients.auth.api.dto;

public record AuthResponse(
        String accessToken, String refreshToken, String tokenType, long expiresIn) {}

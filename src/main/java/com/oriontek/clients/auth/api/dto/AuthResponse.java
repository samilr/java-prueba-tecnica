package com.oriontek.clients.auth.api.dto;

public record AuthResponse(
        String accessToken, String refreshToken, String tokenType, long expiresIn) {

    public static AuthResponse of(String accessToken, String refreshToken, long expiresIn) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresIn);
    }
}

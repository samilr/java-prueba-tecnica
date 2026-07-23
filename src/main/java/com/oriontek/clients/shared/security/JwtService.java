package com.oriontek.clients.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtProperties properties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String username, String role) {
        return buildToken(username, role, TokenType.ACCESS, properties.accessTokenExpiration());
    }

    public String generateRefreshToken(String username, String role) {
        return buildToken(username, role, TokenType.REFRESH, properties.refreshTokenExpiration());
    }

    public long accessTokenExpirationSeconds() {
        return properties.accessTokenExpiration();
    }

    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    public String extractRole(String token) {
        return parse(token).get("role", String.class);
    }

    public TokenType extractTokenType(String token) {
        return TokenType.valueOf(parse(token).get("type", String.class));
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private String buildToken(
            String username, String role, TokenType type, long expirationSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(properties.issuer())
                .subject(username)
                .claim("role", role)
                .claim("type", type.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(signingKey)
                .compact();
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(properties.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

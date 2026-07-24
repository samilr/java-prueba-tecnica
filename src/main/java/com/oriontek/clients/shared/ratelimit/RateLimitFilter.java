package com.oriontek.clients.shared.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oriontek.clients.shared.web.ApiError;
import com.oriontek.clients.shared.web.ApiResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class RateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/v1/auth/login";
    private static final String API_PREFIX = "/api/v1/";

    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitFilter(RateLimitProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(API_PREFIX);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        boolean isLogin = LOGIN_PATH.equals(request.getRequestURI());
        RateLimitProperties.Limit limit = isLogin ? properties.login() : properties.standard();
        String key = (isLogin ? "login:" : "api:") + resolveClient(request, isLogin);

        Bucket bucket = buckets.computeIfAbsent(key, k -> newBucket(limit));
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.setHeader(
                    "X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
            return;
        }

        long retryAfterSeconds =
                Math.max(1, TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
        writeTooManyRequests(response, retryAfterSeconds);
    }

    private String resolveClient(HttpServletRequest request, boolean isLogin) {
        if (!isLogin) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null
                    && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                return "user:" + authentication.getName();
            }
        }
        return "ip:" + clientIp(request);
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private Bucket newBucket(RateLimitProperties.Limit limit) {
        Bandwidth bandwidth =
                Bandwidth.classic(
                        limit.capacity(),
                        Refill.greedy(
                                limit.capacity(), Duration.ofSeconds(limit.refillPeriodSeconds())));
        return Bucket.builder().addLimit(bandwidth).build();
    }

    private void writeTooManyRequests(HttpServletResponse response, long retryAfterSeconds)
            throws IOException {
        ApiError error =
                ApiError.of(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Demasiadas solicitudes",
                        "Se ha excedido el límite de solicitudes, intente nuevamente en "
                                + retryAfterSeconds
                                + " segundos");
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), ApiResponse.failure(error));
    }
}

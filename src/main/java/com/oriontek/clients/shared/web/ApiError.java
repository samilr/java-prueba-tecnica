package com.oriontek.clients.shared.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        int status,
        String title,
        String detail,
        String type,
        List<FieldError> errors,
        Instant timestamp) {

    private static final String TYPE_PREFIX = "https://oriontek.com/problems/";

    public record FieldError(String field, String message) {}

    public static ApiError of(HttpStatus status, String title, String detail) {
        return new ApiError(
                status.value(), title, detail, TYPE_PREFIX + status.value(), null, Instant.now());
    }

    public static ApiError of(
            HttpStatus status, String title, String detail, List<FieldError> errors) {
        return new ApiError(
                status.value(),
                title,
                detail,
                TYPE_PREFIX + status.value(),
                errors == null || errors.isEmpty() ? null : errors,
                Instant.now());
    }
}

package com.oriontek.clients.shared.web;

import java.net.URI;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public final class ProblemDetails {

    private static final String TYPE_PREFIX = "https://oriontek.com/problems/";

    private ProblemDetails() {}

    public static ProblemDetail of(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create(TYPE_PREFIX + status.value()));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }
}

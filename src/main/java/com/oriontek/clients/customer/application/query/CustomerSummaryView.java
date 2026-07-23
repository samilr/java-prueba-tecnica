package com.oriontek.clients.customer.application.query;

import com.oriontek.clients.customer.domain.CustomerStatus;
import java.time.Instant;
import java.util.UUID;

public record CustomerSummaryView(
        UUID id,
        String name,
        String email,
        String phone,
        String identificationNumber,
        CustomerStatus status,
        long addressCount,
        Instant createdAt,
        Instant updatedAt) {}

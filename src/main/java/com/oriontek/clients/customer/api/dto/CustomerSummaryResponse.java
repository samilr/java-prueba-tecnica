package com.oriontek.clients.customer.api.dto;

import com.oriontek.clients.customer.domain.CustomerStatus;
import java.time.Instant;
import java.util.UUID;

public record CustomerSummaryResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String identificationNumber,
        CustomerStatus status,
        int addressCount,
        Instant createdAt,
        Instant updatedAt) {}

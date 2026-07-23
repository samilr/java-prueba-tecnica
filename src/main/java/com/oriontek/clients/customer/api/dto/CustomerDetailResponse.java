package com.oriontek.clients.customer.api.dto;

import com.oriontek.clients.customer.domain.CustomerStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CustomerDetailResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String identificationNumber,
        CustomerStatus status,
        List<AddressResponse> addresses,
        Instant createdAt,
        Instant updatedAt) {}

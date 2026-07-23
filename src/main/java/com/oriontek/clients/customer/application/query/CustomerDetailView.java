package com.oriontek.clients.customer.application.query;

import com.oriontek.clients.customer.domain.CustomerStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CustomerDetailView(
        UUID id,
        String name,
        String email,
        String phone,
        String identificationNumber,
        CustomerStatus status,
        List<AddressView> addresses,
        Instant createdAt,
        Instant updatedAt) {}

package com.oriontek.clients.customer.api.dto;

import com.oriontek.clients.customer.domain.AddressType;
import java.util.UUID;

public record AddressResponse(
        UUID id,
        String street,
        String city,
        String state,
        String country,
        String postalCode,
        AddressType type,
        boolean primary) {}

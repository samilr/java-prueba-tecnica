package com.oriontek.clients.customer.application.query;

import com.oriontek.clients.customer.domain.AddressType;
import java.util.UUID;

public record AddressView(
        UUID id,
        String street,
        String city,
        String state,
        String country,
        String postalCode,
        AddressType type,
        boolean primary) {}

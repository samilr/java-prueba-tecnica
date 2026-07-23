package com.oriontek.clients.customer.application.command;

import com.oriontek.clients.customer.domain.AddressType;

public record AddressInput(
        String street,
        String city,
        String state,
        String country,
        String postalCode,
        AddressType type,
        boolean primary) {}

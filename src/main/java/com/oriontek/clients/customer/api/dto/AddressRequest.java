package com.oriontek.clients.customer.api.dto;

import com.oriontek.clients.customer.application.command.AddressInput;
import com.oriontek.clients.customer.domain.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank @Size(max = 200) String street,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 100) String state,
        @Size(max = 100) String country,
        @Size(max = 20) String postalCode,
        @NotNull AddressType type,
        boolean primary) {

    public AddressInput toInput() {
        return new AddressInput(street, city, state, country, postalCode, type, primary);
    }
}

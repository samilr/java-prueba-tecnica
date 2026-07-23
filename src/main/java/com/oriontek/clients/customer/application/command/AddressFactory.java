package com.oriontek.clients.customer.application.command;

import com.oriontek.clients.customer.domain.Address;
import org.springframework.util.StringUtils;

final class AddressFactory {

    private static final String DEFAULT_COUNTRY = "República Dominicana";

    private AddressFactory() {}

    static Address from(AddressInput input) {
        String country = StringUtils.hasText(input.country()) ? input.country() : DEFAULT_COUNTRY;
        return Address.create(
                input.street(),
                input.city(),
                input.state(),
                country,
                input.postalCode(),
                input.type(),
                input.primary());
    }
}

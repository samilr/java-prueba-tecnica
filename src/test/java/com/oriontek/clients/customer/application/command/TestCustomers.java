package com.oriontek.clients.customer.application.command;

import com.oriontek.clients.customer.domain.Address;
import com.oriontek.clients.customer.domain.AddressType;
import com.oriontek.clients.customer.domain.Customer;

final class TestCustomers {

    private TestCustomers() {}

    static Customer withPrimaryAndSecondaryAddress() {
        Customer customer = Customer.create("Juan", "juan@x.com", "809", "00112345678");
        customer.addAddress(
                Address.create(
                        "Calle 1",
                        "Santo Domingo",
                        "DN",
                        "República Dominicana",
                        "10000",
                        AddressType.HOME,
                        true));
        customer.addAddress(
                Address.create(
                        "Calle 2",
                        "Santiago",
                        "Santiago",
                        "República Dominicana",
                        "51000",
                        AddressType.WORK,
                        false));
        return customer;
    }

    static AddressInput address(boolean primary) {
        return new AddressInput(
                "Calle Nueva",
                "La Romana",
                "La Romana",
                null,
                "22000",
                AddressType.BILLING,
                primary);
    }
}

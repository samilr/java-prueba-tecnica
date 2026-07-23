package com.oriontek.clients.customer.application.command;

import java.util.List;

public record CreateCustomerCommand(
        String name,
        String email,
        String phone,
        String identificationNumber,
        List<AddressInput> addresses) {}

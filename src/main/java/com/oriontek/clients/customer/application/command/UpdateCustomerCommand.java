package com.oriontek.clients.customer.application.command;

import java.util.UUID;

public record UpdateCustomerCommand(
        UUID customerId, String name, String email, String phone, String identificationNumber) {}

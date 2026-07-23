package com.oriontek.clients.customer.application.command;

import java.util.UUID;

public record DeleteCustomerCommand(UUID customerId) {}

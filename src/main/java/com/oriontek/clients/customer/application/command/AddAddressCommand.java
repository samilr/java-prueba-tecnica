package com.oriontek.clients.customer.application.command;

import java.util.UUID;

public record AddAddressCommand(UUID customerId, AddressInput address) {}

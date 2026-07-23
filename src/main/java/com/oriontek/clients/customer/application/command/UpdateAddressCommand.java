package com.oriontek.clients.customer.application.command;

import java.util.UUID;

public record UpdateAddressCommand(UUID customerId, UUID addressId, AddressInput address) {}

package com.oriontek.clients.customer.application.command;

import java.util.UUID;

public record RemoveAddressCommand(UUID customerId, UUID addressId) {}

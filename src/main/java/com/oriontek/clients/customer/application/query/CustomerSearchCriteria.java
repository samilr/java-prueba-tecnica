package com.oriontek.clients.customer.application.query;

import com.oriontek.clients.customer.domain.CustomerStatus;

public record CustomerSearchCriteria(
        String name, String email, String city, CustomerStatus status) {}

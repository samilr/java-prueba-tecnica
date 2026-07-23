package com.oriontek.clients.customer.application.query;

import org.springframework.data.domain.Pageable;

public record SearchCustomersQuery(CustomerSearchCriteria criteria, Pageable pageable) {}

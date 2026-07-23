package com.oriontek.clients.customer.application.query;

import com.oriontek.clients.customer.domain.CustomerSearchCriteria;
import org.springframework.data.domain.Pageable;

public record SearchCustomersQuery(CustomerSearchCriteria criteria, Pageable pageable) {}

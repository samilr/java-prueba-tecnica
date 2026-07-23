package com.oriontek.clients.customer.domain;

public record CustomerSearchCriteria(
        String name, String email, String city, CustomerStatus status) {}

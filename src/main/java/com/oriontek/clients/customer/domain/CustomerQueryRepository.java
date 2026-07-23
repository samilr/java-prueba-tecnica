package com.oriontek.clients.customer.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerQueryRepository {

    Optional<Customer> findDetailById(UUID id);

    Page<Customer> search(CustomerSearchCriteria criteria, Pageable pageable);
}

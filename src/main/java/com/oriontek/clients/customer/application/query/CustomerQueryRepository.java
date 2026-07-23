package com.oriontek.clients.customer.application.query;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerQueryRepository {

    Optional<CustomerDetailView> findDetailById(UUID id);

    Page<CustomerSummaryView> search(CustomerSearchCriteria criteria, Pageable pageable);
}

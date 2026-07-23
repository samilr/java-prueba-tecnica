package com.oriontek.clients.customer.application.query;

import com.oriontek.clients.shared.cqrs.QueryHandler;
import com.oriontek.clients.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCustomerByIdHandler
        implements QueryHandler<GetCustomerByIdQuery, CustomerDetailView> {

    private final CustomerQueryRepository queryRepository;

    @Override
    @Transactional(readOnly = true)
    public CustomerDetailView handle(GetCustomerByIdQuery query) {
        return queryRepository
                .findDetailById(query.customerId())
                .orElseThrow(() -> ResourceNotFoundException.of("Cliente", query.customerId()));
    }
}

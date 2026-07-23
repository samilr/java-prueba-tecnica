package com.oriontek.clients.customer.application.query;

import com.oriontek.clients.customer.api.dto.CustomerDetailResponse;
import com.oriontek.clients.customer.application.CustomerMapper;
import com.oriontek.clients.customer.domain.CustomerQueryRepository;
import com.oriontek.clients.shared.cqrs.QueryHandler;
import com.oriontek.clients.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCustomerByIdHandler
        implements QueryHandler<GetCustomerByIdQuery, CustomerDetailResponse> {

    private final CustomerQueryRepository queryRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public CustomerDetailResponse handle(GetCustomerByIdQuery query) {
        return queryRepository
                .findDetailById(query.customerId())
                .map(customerMapper::toDetail)
                .orElseThrow(() -> ResourceNotFoundException.of("Cliente", query.customerId()));
    }
}

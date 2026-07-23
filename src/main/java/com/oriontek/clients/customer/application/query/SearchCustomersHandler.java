package com.oriontek.clients.customer.application.query;

import com.oriontek.clients.customer.api.dto.CustomerSummaryResponse;
import com.oriontek.clients.customer.application.CustomerMapper;
import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.customer.domain.CustomerQueryRepository;
import com.oriontek.clients.shared.cqrs.QueryHandler;
import com.oriontek.clients.shared.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchCustomersHandler
        implements QueryHandler<SearchCustomersQuery, PageResponse<CustomerSummaryResponse>> {

    private final CustomerQueryRepository queryRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CustomerSummaryResponse> handle(SearchCustomersQuery query) {
        Page<Customer> page = queryRepository.search(query.criteria(), query.pageable());
        return PageResponse.from(page.map(customerMapper::toSummary));
    }
}

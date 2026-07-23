package com.oriontek.clients.customer.application.query;

import com.oriontek.clients.shared.cqrs.QueryHandler;
import com.oriontek.clients.shared.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchCustomersHandler
        implements QueryHandler<SearchCustomersQuery, PageResponse<CustomerSummaryView>> {

    private final CustomerQueryRepository queryRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CustomerSummaryView> handle(SearchCustomersQuery query) {
        return PageResponse.from(queryRepository.search(query.criteria(), query.pageable()));
    }
}

package com.oriontek.clients.customer.api;

import com.oriontek.clients.customer.application.query.CustomerDetailView;
import com.oriontek.clients.customer.application.query.CustomerSearchCriteria;
import com.oriontek.clients.customer.application.query.CustomerSummaryView;
import com.oriontek.clients.customer.application.query.GetCustomerByIdHandler;
import com.oriontek.clients.customer.application.query.GetCustomerByIdQuery;
import com.oriontek.clients.customer.application.query.SearchCustomersHandler;
import com.oriontek.clients.customer.application.query.SearchCustomersQuery;
import com.oriontek.clients.customer.domain.CustomerStatus;
import com.oriontek.clients.shared.pagination.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Gestión de clientes y sus direcciones")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@RequiredArgsConstructor
public class CustomerQueryController {

    private final GetCustomerByIdHandler getCustomerByIdHandler;
    private final SearchCustomersHandler searchCustomersHandler;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener el detalle de un cliente con sus direcciones")
    public CustomerDetailView getById(@PathVariable UUID id) {
        return getCustomerByIdHandler.handle(new GetCustomerByIdQuery(id));
    }

    @GetMapping
    @Operation(summary = "Listar clientes paginados con filtros y ordenamiento")
    public PageResponse<CustomerSummaryView> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) CustomerStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        CustomerSearchCriteria criteria = new CustomerSearchCriteria(name, email, city, status);
        return searchCustomersHandler.handle(new SearchCustomersQuery(criteria, pageable));
    }
}

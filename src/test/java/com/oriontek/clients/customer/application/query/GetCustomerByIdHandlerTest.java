package com.oriontek.clients.customer.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.oriontek.clients.customer.application.CustomerMapper;
import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.customer.domain.CustomerQueryRepository;
import com.oriontek.clients.shared.exception.ResourceNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetCustomerByIdHandlerTest {

    @Mock private CustomerQueryRepository queryRepository;

    private final CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

    @Test
    void returnsDetailWhenFound() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.create("Juan", "juan@x.com", "809", "00112345678");
        when(queryRepository.findDetailById(id)).thenReturn(Optional.of(customer));
        GetCustomerByIdHandler handler =
                new GetCustomerByIdHandler(queryRepository, customerMapper);

        var result = handler.handle(new GetCustomerByIdQuery(id));

        assertThat(result.email()).isEqualTo("juan@x.com");
    }

    @Test
    void failsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(queryRepository.findDetailById(id)).thenReturn(Optional.empty());
        GetCustomerByIdHandler handler =
                new GetCustomerByIdHandler(queryRepository, customerMapper);

        assertThatThrownBy(() -> handler.handle(new GetCustomerByIdQuery(id)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

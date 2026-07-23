package com.oriontek.clients.customer.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.oriontek.clients.customer.domain.CustomerStatus;
import com.oriontek.clients.shared.exception.ResourceNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetCustomerByIdHandlerTest {

    @Mock private CustomerQueryRepository queryRepository;

    @InjectMocks private GetCustomerByIdHandler handler;

    private CustomerDetailView view(UUID id) {
        return new CustomerDetailView(
                id,
                "Juan",
                "juan@x.com",
                "809",
                "00112345678",
                CustomerStatus.ACTIVE,
                List.of(),
                Instant.now(),
                Instant.now());
    }

    @Test
    void returnsDetailWhenFound() {
        UUID id = UUID.randomUUID();
        when(queryRepository.findDetailById(id)).thenReturn(Optional.of(view(id)));

        CustomerDetailView result = handler.handle(new GetCustomerByIdQuery(id));

        assertThat(result.email()).isEqualTo("juan@x.com");
    }

    @Test
    void failsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(queryRepository.findDetailById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new GetCustomerByIdQuery(id)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

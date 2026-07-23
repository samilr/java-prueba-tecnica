package com.oriontek.clients.customer.application.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.customer.domain.CustomerRepository;
import com.oriontek.clients.shared.exception.ConflictException;
import com.oriontek.clients.shared.exception.ResourceNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateCustomerHandlerTest {

    @Mock private CustomerRepository customerRepository;

    @InjectMocks private UpdateCustomerHandler handler;

    private UpdateCustomerCommand command(UUID id) {
        return new UpdateCustomerCommand(id, "Nuevo Nombre", "nuevo@x.com", "809", "00199999999");
    }

    @Test
    void updatesExistingCustomer() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.create("Viejo", "viejo@x.com", "809", "00111111111");
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmailAndIdNot("nuevo@x.com", id)).thenReturn(false);
        when(customerRepository.existsByIdentificationNumberAndIdNot("00199999999", id))
                .thenReturn(false);

        handler.handle(command(id));

        verify(customerRepository).save(customer);
    }

    @Test
    void failsWhenCustomerNotFound() {
        UUID id = UUID.randomUUID();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command(id)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void failsWhenEmailBelongsToAnotherCustomer() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.create("Viejo", "viejo@x.com", "809", "00111111111");
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmailAndIdNot("nuevo@x.com", id)).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(command(id))).isInstanceOf(ConflictException.class);
        verify(customerRepository, never()).save(any());
    }
}

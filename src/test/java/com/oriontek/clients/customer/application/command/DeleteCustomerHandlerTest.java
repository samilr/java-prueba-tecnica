package com.oriontek.clients.customer.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.customer.domain.CustomerRepository;
import com.oriontek.clients.customer.domain.CustomerStatus;
import com.oriontek.clients.shared.exception.ResourceNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteCustomerHandlerTest {

    @Mock private CustomerRepository customerRepository;

    @InjectMocks private DeleteCustomerHandler handler;

    @Test
    void deactivatesTheCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer customer = TestCustomers.withPrimaryAndSecondaryAddress();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        handler.handle(new DeleteCustomerCommand(customerId));

        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.INACTIVE);
        verify(customerRepository).save(customer);
    }

    @Test
    void failsWhenCustomerDoesNotExist() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new DeleteCustomerCommand(customerId)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(customerRepository, never()).save(any());
    }
}

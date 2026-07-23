package com.oriontek.clients.customer.application.command;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.customer.domain.CustomerRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UpdateAddressHandlerTest {

    @Mock private CustomerRepository customerRepository;

    @InjectMocks private UpdateAddressHandler handler;

    @Test
    void persistsTheUpdatedAddress() {
        UUID customerId = UUID.randomUUID();
        Customer customer = TestCustomers.withPrimaryAndSecondaryAddress();
        UUID addressId = UUID.randomUUID();
        ReflectionTestUtils.setField(customer.getAddresses().get(1), "id", addressId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        handler.handle(
                new UpdateAddressCommand(customerId, addressId, TestCustomers.address(true)));

        verify(customerRepository).save(customer);
    }
}

package com.oriontek.clients.customer.application.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oriontek.clients.customer.domain.Address;
import com.oriontek.clients.customer.domain.AddressType;
import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.customer.domain.CustomerRepository;
import com.oriontek.clients.shared.exception.ResourceNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AddressCommandHandlersTest {

    @Mock private CustomerRepository customerRepository;

    @InjectMocks private AddAddressHandler addAddressHandler;
    @InjectMocks private UpdateAddressHandler updateAddressHandler;
    @InjectMocks private RemoveAddressHandler removeAddressHandler;
    @InjectMocks private DeleteCustomerHandler deleteCustomerHandler;

    private Customer customerWithTwoAddresses() {
        Customer customer = Customer.create("Juan", "juan@x.com", "809", "00112345678");
        Address primary =
                customer.addAddress(
                        Address.create(
                                "Calle 1",
                                "Santo Domingo",
                                "DN",
                                "República Dominicana",
                                "10000",
                                AddressType.HOME,
                                true));
        Address secondary =
                customer.addAddress(
                        Address.create(
                                "Calle 2",
                                "Santiago",
                                "Santiago",
                                "República Dominicana",
                                "51000",
                                AddressType.WORK,
                                false));
        ReflectionTestUtils.setField(primary, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(secondary, "id", UUID.randomUUID());
        return customer;
    }

    private AddressInput input(boolean primary) {
        return new AddressInput(
                "Calle Nueva",
                "La Romana",
                "La Romana",
                null,
                "22000",
                AddressType.BILLING,
                primary);
    }

    @Test
    void addAddressPersistsWhenCustomerExists() {
        UUID customerId = UUID.randomUUID();
        Customer customer = customerWithTwoAddresses();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        addAddressHandler.handle(new AddAddressCommand(customerId, input(false)));

        verify(customerRepository).save(customer);
    }

    @Test
    void addAddressFailsWhenCustomerMissing() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(
                        () ->
                                addAddressHandler.handle(
                                        new AddAddressCommand(customerId, input(false))))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateAddressPersistsWhenCustomerExists() {
        UUID customerId = UUID.randomUUID();
        Customer customer = customerWithTwoAddresses();
        UUID addressId = customer.getAddresses().get(1).getId();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        updateAddressHandler.handle(new UpdateAddressCommand(customerId, addressId, input(true)));

        verify(customerRepository).save(customer);
    }

    @Test
    void removeAddressPersistsWhenSecondaryRemoved() {
        UUID customerId = UUID.randomUUID();
        Customer customer = customerWithTwoAddresses();
        UUID secondaryId = customer.getAddresses().get(1).getId();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        removeAddressHandler.handle(new RemoveAddressCommand(customerId, secondaryId));

        verify(customerRepository).save(customer);
    }

    @Test
    void deleteCustomerDeactivatesWhenExists() {
        UUID customerId = UUID.randomUUID();
        Customer customer = customerWithTwoAddresses();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        deleteCustomerHandler.handle(new DeleteCustomerCommand(customerId));

        verify(customerRepository).save(customer);
    }

    @Test
    void deleteCustomerFailsWhenMissing() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(
                        () -> deleteCustomerHandler.handle(new DeleteCustomerCommand(customerId)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(customerRepository, never()).save(any());
    }
}

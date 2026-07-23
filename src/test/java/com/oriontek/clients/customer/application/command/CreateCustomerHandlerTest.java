package com.oriontek.clients.customer.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oriontek.clients.customer.domain.AddressType;
import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.customer.domain.CustomerRepository;
import com.oriontek.clients.shared.exception.BusinessRuleException;
import com.oriontek.clients.shared.exception.ConflictException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateCustomerHandlerTest {

    @Mock private CustomerRepository customerRepository;

    @InjectMocks private CreateCustomerHandler handler;

    private AddressInput address() {
        return new AddressInput(
                "Calle 1",
                "Santo Domingo",
                "DN",
                "República Dominicana",
                "10000",
                AddressType.HOME,
                true);
    }

    private CreateCustomerCommand command(List<AddressInput> addresses) {
        return new CreateCustomerCommand(
                "Juan Pérez", "juan@x.com", "809-555-0001", "00112345678", addresses);
    }

    @Test
    void createsCustomerAndReturnsId() {
        when(customerRepository.existsByEmail("juan@x.com")).thenReturn(false);
        when(customerRepository.existsByIdentificationNumber("00112345678")).thenReturn(false);
        UUID generated = UUID.randomUUID();
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(
                        invocation -> {
                            Customer c = invocation.getArgument(0);
                            org.springframework.test.util.ReflectionTestUtils.setField(
                                    c, "id", generated);
                            return c;
                        });

        UUID result = handler.handle(command(List.of(address())));

        assertThat(result).isEqualTo(generated);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void rejectsDuplicateEmail() {
        when(customerRepository.existsByEmail("juan@x.com")).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(command(List.of(address()))))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("email");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void rejectsDuplicateIdentification() {
        when(customerRepository.existsByEmail("juan@x.com")).thenReturn(false);
        when(customerRepository.existsByIdentificationNumber("00112345678")).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(command(List.of(address()))))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("identificación");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void rejectsCustomerWithoutAddresses() {
        assertThatThrownBy(() -> handler.handle(command(List.of())))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("al menos una dirección");
        verify(customerRepository, never()).save(any());
    }
}

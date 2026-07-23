package com.oriontek.clients.customer.application.command;

import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.customer.domain.CustomerRepository;
import com.oriontek.clients.shared.cqrs.CommandHandler;
import com.oriontek.clients.shared.exception.BusinessRuleException;
import com.oriontek.clients.shared.exception.ConflictException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCustomerHandler implements CommandHandler<CreateCustomerCommand, UUID> {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public UUID handle(CreateCustomerCommand command) {
        if (command.addresses() == null || command.addresses().isEmpty()) {
            throw new BusinessRuleException("El cliente debe tener al menos una dirección");
        }
        if (customerRepository.existsByEmail(command.email())) {
            throw new ConflictException(
                    "Ya existe un cliente con el email %s".formatted(command.email()));
        }
        if (customerRepository.existsByIdentificationNumber(command.identificationNumber())) {
            throw new ConflictException(
                    "Ya existe un cliente con la identificación %s"
                            .formatted(command.identificationNumber()));
        }

        Customer customer =
                Customer.create(
                        command.name(),
                        command.email(),
                        command.phone(),
                        command.identificationNumber());
        command.addresses().stream().map(AddressFactory::from).forEach(customer::addAddress);

        return customerRepository.save(customer).getId();
    }
}

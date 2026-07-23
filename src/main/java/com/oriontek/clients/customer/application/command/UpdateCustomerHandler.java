package com.oriontek.clients.customer.application.command;

import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.customer.domain.CustomerRepository;
import com.oriontek.clients.shared.cqrs.CommandHandler;
import com.oriontek.clients.shared.exception.ConflictException;
import com.oriontek.clients.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCustomerHandler implements CommandHandler<UpdateCustomerCommand, Void> {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public Void handle(UpdateCustomerCommand command) {
        Customer customer =
                customerRepository
                        .findById(command.customerId())
                        .orElseThrow(
                                () ->
                                        ResourceNotFoundException.of(
                                                "Cliente", command.customerId()));

        if (customerRepository.existsByEmailAndIdNot(command.email(), command.customerId())) {
            throw new ConflictException(
                    "Ya existe un cliente con el email %s".formatted(command.email()));
        }
        if (customerRepository.existsByIdentificationNumberAndIdNot(
                command.identificationNumber(), command.customerId())) {
            throw new ConflictException(
                    "Ya existe un cliente con la identificación %s"
                            .formatted(command.identificationNumber()));
        }

        customer.updateDetails(
                command.name(), command.email(), command.phone(), command.identificationNumber());
        customerRepository.save(customer);
        return null;
    }
}

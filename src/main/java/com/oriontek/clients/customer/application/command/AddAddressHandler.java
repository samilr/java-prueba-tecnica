package com.oriontek.clients.customer.application.command;

import com.oriontek.clients.customer.domain.Address;
import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.customer.domain.CustomerRepository;
import com.oriontek.clients.shared.cqrs.CommandHandler;
import com.oriontek.clients.shared.exception.ResourceNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddAddressHandler implements CommandHandler<AddAddressCommand, UUID> {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public UUID handle(AddAddressCommand command) {
        Customer customer =
                customerRepository
                        .findById(command.customerId())
                        .orElseThrow(
                                () ->
                                        ResourceNotFoundException.of(
                                                "Cliente", command.customerId()));
        Address added = customer.addAddress(AddressFactory.from(command.address()));
        customerRepository.save(customer);
        return added.getId();
    }
}

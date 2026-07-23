package com.oriontek.clients.customer.application.command;

import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.customer.domain.CustomerRepository;
import com.oriontek.clients.shared.cqrs.CommandHandler;
import com.oriontek.clients.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RemoveAddressHandler implements CommandHandler<RemoveAddressCommand, Void> {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public Void handle(RemoveAddressCommand command) {
        Customer customer =
                customerRepository
                        .findById(command.customerId())
                        .orElseThrow(
                                () ->
                                        ResourceNotFoundException.of(
                                                "Cliente", command.customerId()));
        customer.removeAddress(command.addressId());
        customerRepository.save(customer);
        return null;
    }
}

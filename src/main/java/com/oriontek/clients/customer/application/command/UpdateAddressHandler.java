package com.oriontek.clients.customer.application.command;

import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.customer.domain.CustomerRepository;
import com.oriontek.clients.shared.cqrs.CommandHandler;
import com.oriontek.clients.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UpdateAddressHandler implements CommandHandler<UpdateAddressCommand, Void> {

    private static final String DEFAULT_COUNTRY = "República Dominicana";

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public Void handle(UpdateAddressCommand command) {
        Customer customer =
                customerRepository
                        .findById(command.customerId())
                        .orElseThrow(
                                () ->
                                        ResourceNotFoundException.of(
                                                "Cliente", command.customerId()));
        var input = command.address();
        String country = StringUtils.hasText(input.country()) ? input.country() : DEFAULT_COUNTRY;
        customer.updateAddress(
                command.addressId(),
                input.street(),
                input.city(),
                input.state(),
                country,
                input.postalCode(),
                input.type(),
                input.primary());
        customerRepository.save(customer);
        return null;
    }
}

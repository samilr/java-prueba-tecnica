package com.oriontek.clients.customer.domain;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByIdentificationNumber(String identificationNumber);

    boolean existsByIdentificationNumberAndIdNot(String identificationNumber, UUID id);
}

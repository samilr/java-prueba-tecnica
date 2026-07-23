package com.oriontek.clients.customer.infrastructure.persistence;

import com.oriontek.clients.customer.domain.Customer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerJpaRepository
        extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);

    boolean existsByIdentificationNumber(String identificationNumber);

    boolean existsByIdentificationNumberAndIdNot(String identificationNumber, UUID id);

    @EntityGraph(attributePaths = "addresses")
    Optional<Customer> findWithAddressesById(UUID id);
}

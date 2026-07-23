package com.oriontek.clients.customer.infrastructure.persistence;

import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.customer.domain.CustomerQueryRepository;
import com.oriontek.clients.customer.domain.CustomerRepository;
import com.oriontek.clients.customer.domain.CustomerSearchCriteria;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository, CustomerQueryRepository {

    private final CustomerJpaRepository jpaRepository;

    @Override
    public Customer save(Customer customer) {
        return jpaRepository.save(customer);
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpaRepository.findWithAddressesById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, UUID id) {
        return jpaRepository.existsByEmailIgnoreCaseAndIdNot(email, id);
    }

    @Override
    public boolean existsByIdentificationNumber(String identificationNumber) {
        return jpaRepository.existsByIdentificationNumber(identificationNumber);
    }

    @Override
    public boolean existsByIdentificationNumberAndIdNot(String identificationNumber, UUID id) {
        return jpaRepository.existsByIdentificationNumberAndIdNot(identificationNumber, id);
    }

    @Override
    public Optional<Customer> findDetailById(UUID id) {
        return jpaRepository.findWithAddressesById(id);
    }

    @Override
    public Page<Customer> search(CustomerSearchCriteria criteria, Pageable pageable) {
        return jpaRepository.findAll(CustomerSpecifications.fromCriteria(criteria), pageable);
    }
}

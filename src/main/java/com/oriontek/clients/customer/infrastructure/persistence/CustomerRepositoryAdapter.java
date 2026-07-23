package com.oriontek.clients.customer.infrastructure.persistence;

import com.oriontek.clients.customer.application.CustomerMapper;
import com.oriontek.clients.customer.application.query.CustomerDetailView;
import com.oriontek.clients.customer.application.query.CustomerQueryRepository;
import com.oriontek.clients.customer.application.query.CustomerSearchCriteria;
import com.oriontek.clients.customer.application.query.CustomerSummaryView;
import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.customer.domain.CustomerRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository, CustomerQueryRepository {

    private final CustomerJpaRepository jpaRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Customer save(Customer customer) {
        return jpaRepository.save(customer);
    }

    @Override
    public Customer saveAndFlush(Customer customer) {
        return jpaRepository.saveAndFlush(customer);
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
    public Optional<CustomerDetailView> findDetailById(UUID id) {
        return jpaRepository.findWithAddressesById(id).map(customerMapper::toDetail);
    }

    @Override
    public Page<CustomerSummaryView> search(CustomerSearchCriteria criteria, Pageable pageable) {
        Page<Customer> page =
                jpaRepository.findAll(CustomerSpecifications.fromCriteria(criteria), pageable);
        Map<UUID, Long> addressCounts = countAddresses(page.getContent());
        return page.map(
                customer ->
                        customerMapper.toSummary(
                                customer, addressCounts.getOrDefault(customer.getId(), 0L)));
    }

    private Map<UUID, Long> countAddresses(List<Customer> customers) {
        if (customers.isEmpty()) {
            return Map.of();
        }
        List<UUID> ids = customers.stream().map(Customer::getId).toList();
        return jpaRepository.countAddressesByCustomerIds(ids).stream()
                .collect(
                        Collectors.toMap(
                                CustomerJpaRepository.AddressCountRow::getCustomerId,
                                CustomerJpaRepository.AddressCountRow::getTotal));
    }
}

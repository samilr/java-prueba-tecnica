package com.oriontek.clients.customer.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.oriontek.clients.customer.domain.Address;
import com.oriontek.clients.customer.domain.AddressType;
import com.oriontek.clients.customer.domain.Customer;
import com.oriontek.clients.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootTest
class CustomerRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired private CustomerJpaRepository repository;

    private Customer newCustomer(String email, String identification) {
        Customer customer = Customer.create("Cliente Test", email, "809-555-0000", identification);
        customer.addAddress(
                Address.create(
                        "Calle 1",
                        "Santo Domingo",
                        "DN",
                        "República Dominicana",
                        "10000",
                        AddressType.HOME,
                        true));
        customer.addAddress(
                Address.create(
                        "Calle 2",
                        "Santiago",
                        "Santiago",
                        "República Dominicana",
                        "51000",
                        AddressType.WORK,
                        false));
        return customer;
    }

    @Test
    void persistsCustomerWithAddressesAndAudit() {
        Customer saved = repository.saveAndFlush(newCustomer("persist@test.com", "70000000001"));

        Customer found = repository.findWithAddressesById(saved.getId()).orElseThrow();
        assertThat(found.getId()).isNotNull();
        assertThat(found.getCreatedAt()).isNotNull();
        assertThat(found.getVersion()).isNotNull();
        assertThat(found.getAddresses()).hasSize(2);
        assertThat(found.getAddresses().stream().filter(Address::isPrimary).count()).isEqualTo(1);
    }

    @Test
    void enforcesUniqueEmail() {
        repository.saveAndFlush(newCustomer("dup@test.com", "70000000002"));

        assertThatThrownBy(
                        () -> repository.saveAndFlush(newCustomer("dup@test.com", "70000000003")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}

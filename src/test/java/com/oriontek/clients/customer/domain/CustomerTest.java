package com.oriontek.clients.customer.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.oriontek.clients.shared.exception.BusinessRuleException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class CustomerTest {

    private Address address(String city, boolean primary) {
        return Address.create(
                "Calle " + city,
                city,
                "Estado",
                "República Dominicana",
                "10000",
                AddressType.HOME,
                primary);
    }

    private void assignId(Address address, UUID id) {
        ReflectionTestUtils.setField(address, "id", id);
    }

    @Test
    void firstAddressBecomesPrimaryEvenIfNotFlagged() {
        Customer customer = Customer.create("Juan", "juan@x.com", "809", "001");

        customer.addAddress(address("Santo Domingo", false));

        assertThat(customer.getAddresses()).hasSize(1);
        assertThat(customer.getAddresses().get(0).isPrimary()).isTrue();
    }

    @Test
    void addingNewPrimaryDemotesPreviousPrimary() {
        Customer customer = Customer.create("Juan", "juan@x.com", "809", "001");
        customer.addAddress(address("Santo Domingo", true));

        customer.addAddress(address("Santiago", true));

        long primaries = customer.getAddresses().stream().filter(Address::isPrimary).count();
        assertThat(primaries).isEqualTo(1);
        assertThat(customer.getAddresses().get(1).isPrimary()).isTrue();
    }

    @Test
    void cannotRemoveLastAddress() {
        Customer customer = Customer.create("Juan", "juan@x.com", "809", "001");
        Address only = customer.addAddress(address("Santo Domingo", true));
        assignId(only, UUID.randomUUID());

        assertThatThrownBy(() -> customer.removeAddress(only.getId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("sin direcciones");
    }

    @Test
    void cannotRemovePrimaryAddressWithoutReassigning() {
        Customer customer = Customer.create("Juan", "juan@x.com", "809", "001");
        Address primary = customer.addAddress(address("Santo Domingo", true));
        Address secondary = customer.addAddress(address("Santiago", false));
        assignId(primary, UUID.randomUUID());
        assignId(secondary, UUID.randomUUID());

        assertThatThrownBy(() -> customer.removeAddress(primary.getId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("primaria");
    }

    @Test
    void canRemoveSecondaryAddress() {
        Customer customer = Customer.create("Juan", "juan@x.com", "809", "001");
        Address primary = customer.addAddress(address("Santo Domingo", true));
        Address secondary = customer.addAddress(address("Santiago", false));
        assignId(primary, UUID.randomUUID());
        assignId(secondary, UUID.randomUUID());

        customer.removeAddress(secondary.getId());

        assertThat(customer.getAddresses()).hasSize(1);
        assertThat(customer.getAddresses().get(0)).isEqualTo(primary);
    }

    @Test
    void updateAddressCanPromoteToPrimary() {
        Customer customer = Customer.create("Juan", "juan@x.com", "809", "001");
        Address primary = customer.addAddress(address("Santo Domingo", true));
        Address secondary = customer.addAddress(address("Santiago", false));
        assignId(primary, UUID.randomUUID());
        assignId(secondary, UUID.randomUUID());

        customer.updateAddress(
                secondary.getId(),
                "Nueva",
                "Santiago",
                "Santiago",
                "República Dominicana",
                "51000",
                AddressType.WORK,
                true);

        assertThat(secondary.isPrimary()).isTrue();
        assertThat(primary.isPrimary()).isFalse();
    }

    @Test
    void deactivateSetsInactiveStatus() {
        Customer customer = Customer.create("Juan", "juan@x.com", "809", "001");

        customer.deactivate();

        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.INACTIVE);
    }
}

package com.oriontek.clients.customer;

import static org.assertj.core.api.Assertions.assertThat;

import com.oriontek.clients.AbstractIntegrationTest;
import com.oriontek.clients.customer.api.dto.AddressResponse;
import com.oriontek.clients.customer.api.dto.CustomerDetailResponse;
import com.oriontek.clients.customer.application.command.AddAddressCommand;
import com.oriontek.clients.customer.application.command.AddAddressHandler;
import com.oriontek.clients.customer.application.command.AddressInput;
import com.oriontek.clients.customer.application.command.CreateCustomerCommand;
import com.oriontek.clients.customer.application.command.CreateCustomerHandler;
import com.oriontek.clients.customer.application.command.UpdateAddressCommand;
import com.oriontek.clients.customer.application.command.UpdateAddressHandler;
import com.oriontek.clients.customer.application.query.GetCustomerByIdHandler;
import com.oriontek.clients.customer.application.query.GetCustomerByIdQuery;
import com.oriontek.clients.customer.domain.AddressType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PrimaryAddressReassignmentIntegrationTest extends AbstractIntegrationTest {

    @Autowired private CreateCustomerHandler createCustomerHandler;
    @Autowired private AddAddressHandler addAddressHandler;
    @Autowired private UpdateAddressHandler updateAddressHandler;
    @Autowired private GetCustomerByIdHandler getCustomerByIdHandler;

    private AddressInput address(String city, boolean primary) {
        return new AddressInput(
                "Calle " + city, city, "Estado", null, "10000", AddressType.HOME, primary);
    }

    private UUID createCustomer(String suffix) {
        return createCustomerHandler.handle(
                new CreateCustomerCommand(
                        "Cliente " + suffix,
                        "primary" + suffix + "@test.com",
                        "809-555-0000",
                        suffix,
                        List.of(address("Santo Domingo", true))));
    }

    private CustomerDetailResponse detail(UUID id) {
        return getCustomerByIdHandler.handle(new GetCustomerByIdQuery(id));
    }

    private long primaryCount(CustomerDetailResponse detail) {
        return detail.addresses().stream().filter(AddressResponse::primary).count();
    }

    @Test
    void addingNewPrimaryAddressDemotesThePreviousOne() {
        UUID customerId = createCustomer("80000000001");

        addAddressHandler.handle(new AddAddressCommand(customerId, address("Santiago", true)));

        CustomerDetailResponse detail = detail(customerId);
        assertThat(detail.addresses()).hasSize(2);
        assertThat(primaryCount(detail)).isEqualTo(1);
        assertThat(
                        detail.addresses().stream()
                                .filter(AddressResponse::primary)
                                .findFirst()
                                .orElseThrow()
                                .city())
                .isEqualTo("Santiago");
    }

    @Test
    void promotingExistingAddressToPrimaryDemotesThePreviousOne() {
        UUID customerId = createCustomer("80000000002");
        addAddressHandler.handle(new AddAddressCommand(customerId, address("La Romana", false)));
        UUID secondaryId =
                detail(customerId).addresses().stream()
                        .filter(a -> !a.primary())
                        .findFirst()
                        .orElseThrow()
                        .id();

        updateAddressHandler.handle(
                new UpdateAddressCommand(customerId, secondaryId, address("La Romana", true)));

        CustomerDetailResponse detail = detail(customerId);
        assertThat(primaryCount(detail)).isEqualTo(1);
        assertThat(
                        detail.addresses().stream()
                                .filter(AddressResponse::primary)
                                .findFirst()
                                .orElseThrow()
                                .id())
                .isEqualTo(secondaryId);
    }
}

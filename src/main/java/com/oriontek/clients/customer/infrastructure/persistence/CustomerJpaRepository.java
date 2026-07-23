package com.oriontek.clients.customer.infrastructure.persistence;

import com.oriontek.clients.customer.domain.Customer;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerJpaRepository
        extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);

    boolean existsByIdentificationNumber(String identificationNumber);

    boolean existsByIdentificationNumberAndIdNot(String identificationNumber, UUID id);

    @EntityGraph(attributePaths = "addresses")
    Optional<Customer> findWithAddressesById(UUID id);

    @Query(
            """
            select a.customer.id as customerId, count(a.id) as total
            from Address a
            where a.customer.id in :ids
            group by a.customer.id
            """)
    List<AddressCountRow> countAddressesByCustomerIds(@Param("ids") Collection<UUID> ids);

    interface AddressCountRow {
        UUID getCustomerId();

        long getTotal();
    }
}

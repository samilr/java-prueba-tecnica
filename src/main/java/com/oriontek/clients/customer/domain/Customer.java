package com.oriontek.clients.customer.domain;

import com.oriontek.clients.shared.exception.BusinessRuleException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "customers")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id @GeneratedValue @UuidGenerator private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(name = "identification_number", nullable = false, unique = true, length = 20)
    private String identificationNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerStatus status;

    @Version private Long version;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = jakarta.persistence.FetchType.LAZY)
    private final List<Address> addresses = new ArrayList<>();

    private Customer(String name, String email, String phone, String identificationNumber) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.identificationNumber = identificationNumber;
        this.status = CustomerStatus.ACTIVE;
    }

    public static Customer create(
            String name, String email, String phone, String identificationNumber) {
        return new Customer(name, email, phone, identificationNumber);
    }

    public void updateDetails(
            String name, String email, String phone, String identificationNumber) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.identificationNumber = identificationNumber;
    }

    public void deactivate() {
        this.status = CustomerStatus.INACTIVE;
    }

    public List<Address> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }

    public void demotePrimaryAddresses() {
        addresses.forEach(existing -> existing.markPrimary(false));
    }

    public Address addAddress(Address address) {
        if (addresses.isEmpty()) {
            address.markPrimary(true);
        } else if (address.isPrimary()) {
            addresses.forEach(existing -> existing.markPrimary(false));
        }
        address.assignTo(this);
        addresses.add(address);
        return address;
    }

    public Address updateAddress(
            UUID addressId,
            String street,
            String city,
            String state,
            String country,
            String postalCode,
            AddressType type,
            boolean makePrimary) {
        Address address = requireAddress(addressId);
        address.update(street, city, state, country, postalCode, type);
        if (makePrimary && !address.isPrimary()) {
            addresses.forEach(existing -> existing.markPrimary(false));
            address.markPrimary(true);
        }
        return address;
    }

    public void removeAddress(UUID addressId) {
        Address address = requireAddress(addressId);
        if (addresses.size() == 1) {
            throw new BusinessRuleException("El cliente no puede quedar sin direcciones");
        }
        if (address.isPrimary()) {
            throw new BusinessRuleException(
                    "No se puede eliminar la dirección primaria sin reasignar otra como primaria");
        }
        addresses.remove(address);
    }

    private Address requireAddress(UUID addressId) {
        return addresses.stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElseThrow(
                        () ->
                                new BusinessRuleException(
                                        "La dirección %s no pertenece al cliente"
                                                .formatted(addressId)));
    }
}

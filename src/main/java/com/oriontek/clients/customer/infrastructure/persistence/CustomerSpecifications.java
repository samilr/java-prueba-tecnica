package com.oriontek.clients.customer.infrastructure.persistence;

import com.oriontek.clients.customer.application.query.CustomerSearchCriteria;
import com.oriontek.clients.customer.domain.Customer;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class CustomerSpecifications {

    private CustomerSpecifications() {}

    public static Specification<Customer> fromCriteria(CustomerSearchCriteria criteria) {
        return Specification.allOf(
                nameContains(criteria.name()),
                emailContains(criteria.email()),
                cityContains(criteria.city()),
                hasStatus(criteria));
    }

    private static Specification<Customer> nameContains(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Customer> emailContains(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    private static Specification<Customer> cityContains(String city) {
        if (!StringUtils.hasText(city)) {
            return null;
        }
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }
            var addresses = root.join("addresses", JoinType.LEFT);
            return cb.like(cb.lower(addresses.get("city")), "%" + city.toLowerCase() + "%");
        };
    }

    private static Specification<Customer> hasStatus(CustomerSearchCriteria criteria) {
        if (criteria.status() == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), criteria.status());
    }
}

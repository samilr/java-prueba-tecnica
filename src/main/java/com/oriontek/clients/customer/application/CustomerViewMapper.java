package com.oriontek.clients.customer.application;

import com.oriontek.clients.customer.application.query.AddressView;
import com.oriontek.clients.customer.application.query.CustomerDetailView;
import com.oriontek.clients.customer.application.query.CustomerSummaryView;
import com.oriontek.clients.customer.domain.Address;
import com.oriontek.clients.customer.domain.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CustomerViewMapper {

    @Mapping(target = "addressCount", source = "addressCount")
    CustomerSummaryView toSummary(Customer customer, long addressCount);

    CustomerDetailView toDetail(Customer customer);

    AddressView toAddressView(Address address);
}

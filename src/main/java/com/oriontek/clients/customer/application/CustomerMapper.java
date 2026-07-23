package com.oriontek.clients.customer.application;

import com.oriontek.clients.customer.api.dto.AddressResponse;
import com.oriontek.clients.customer.api.dto.CustomerDetailResponse;
import com.oriontek.clients.customer.api.dto.CustomerSummaryResponse;
import com.oriontek.clients.customer.domain.Address;
import com.oriontek.clients.customer.domain.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CustomerMapper {

    @Mapping(target = "addressCount", expression = "java(customer.getAddresses().size())")
    CustomerSummaryResponse toSummary(Customer customer);

    CustomerDetailResponse toDetail(Customer customer);

    AddressResponse toAddressResponse(Address address);
}

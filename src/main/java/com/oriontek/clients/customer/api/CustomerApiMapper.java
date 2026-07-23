package com.oriontek.clients.customer.api;

import com.oriontek.clients.customer.api.dto.AddressRequest;
import com.oriontek.clients.customer.api.dto.CreateCustomerRequest;
import com.oriontek.clients.customer.api.dto.UpdateCustomerRequest;
import com.oriontek.clients.customer.application.command.AddAddressCommand;
import com.oriontek.clients.customer.application.command.AddressInput;
import com.oriontek.clients.customer.application.command.CreateCustomerCommand;
import com.oriontek.clients.customer.application.command.RemoveAddressCommand;
import com.oriontek.clients.customer.application.command.UpdateAddressCommand;
import com.oriontek.clients.customer.application.command.UpdateCustomerCommand;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CustomerApiMapper {

    AddressInput toInput(AddressRequest request);

    CreateCustomerCommand toCommand(CreateCustomerRequest request);

    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "email", source = "request.email")
    @Mapping(target = "phone", source = "request.phone")
    @Mapping(target = "identificationNumber", source = "request.identificationNumber")
    UpdateCustomerCommand toCommand(UUID customerId, UpdateCustomerRequest request);

    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "address", source = "request")
    AddAddressCommand toAddAddressCommand(UUID customerId, AddressRequest request);

    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "addressId", source = "addressId")
    @Mapping(target = "address", source = "request")
    UpdateAddressCommand toUpdateAddressCommand(
            UUID customerId, UUID addressId, AddressRequest request);

    RemoveAddressCommand toRemoveAddressCommand(UUID customerId, UUID addressId);
}

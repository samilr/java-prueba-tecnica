package com.oriontek.clients.customer.api.dto;

import com.oriontek.clients.shared.validation.ValidIdentification;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateCustomerRequest(
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 30) String phone,
        @NotBlank @ValidIdentification @Size(max = 20) String identificationNumber,
        @NotEmpty @Valid List<AddressRequest> addresses) {}

package com.oriontek.clients.customer.api.dto;

import com.oriontek.clients.shared.validation.ValidIdentification;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 30) String phone,
        @NotBlank @ValidIdentification @Size(max = 20) String identificationNumber) {}

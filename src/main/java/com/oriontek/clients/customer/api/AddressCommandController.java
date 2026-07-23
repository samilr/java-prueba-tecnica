package com.oriontek.clients.customer.api;

import com.oriontek.clients.customer.api.dto.AddressRequest;
import com.oriontek.clients.customer.api.dto.IdResponse;
import com.oriontek.clients.customer.application.command.AddAddressHandler;
import com.oriontek.clients.customer.application.command.RemoveAddressHandler;
import com.oriontek.clients.customer.application.command.UpdateAddressHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/addresses")
@Tag(name = "Addresses", description = "Gestión de direcciones de un cliente")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AddressCommandController {

    private final AddAddressHandler addAddressHandler;
    private final UpdateAddressHandler updateAddressHandler;
    private final RemoveAddressHandler removeAddressHandler;
    private final CustomerApiMapper customerApiMapper;

    @PostMapping
    @Operation(summary = "Agregar una dirección al cliente")
    public ResponseEntity<IdResponse> add(
            @PathVariable UUID customerId, @Valid @RequestBody AddressRequest request) {
        UUID id =
                addAddressHandler.handle(
                        customerApiMapper.toAddAddressCommand(customerId, request));
        return ResponseEntity.created(
                        URI.create("/api/v1/customers/" + customerId + "/addresses/" + id))
                .body(new IdResponse(id));
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Actualizar una dirección del cliente")
    public ResponseEntity<Void> update(
            @PathVariable UUID customerId,
            @PathVariable UUID addressId,
            @Valid @RequestBody AddressRequest request) {
        updateAddressHandler.handle(
                customerApiMapper.toUpdateAddressCommand(customerId, addressId, request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Eliminar una dirección del cliente")
    public ResponseEntity<Void> remove(
            @PathVariable UUID customerId, @PathVariable UUID addressId) {
        removeAddressHandler.handle(
                customerApiMapper.toRemoveAddressCommand(customerId, addressId));
        return ResponseEntity.noContent().build();
    }
}

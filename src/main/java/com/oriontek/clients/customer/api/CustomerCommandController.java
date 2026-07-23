package com.oriontek.clients.customer.api;

import com.oriontek.clients.customer.api.dto.CreateCustomerRequest;
import com.oriontek.clients.customer.api.dto.IdResponse;
import com.oriontek.clients.customer.api.dto.UpdateCustomerRequest;
import com.oriontek.clients.customer.application.command.CreateCustomerCommand;
import com.oriontek.clients.customer.application.command.CreateCustomerHandler;
import com.oriontek.clients.customer.application.command.DeleteCustomerCommand;
import com.oriontek.clients.customer.application.command.DeleteCustomerHandler;
import com.oriontek.clients.customer.application.command.UpdateCustomerCommand;
import com.oriontek.clients.customer.application.command.UpdateCustomerHandler;
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
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers - Commands", description = "Operaciones de escritura sobre clientes")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class CustomerCommandController {

    private final CreateCustomerHandler createCustomerHandler;
    private final UpdateCustomerHandler updateCustomerHandler;
    private final DeleteCustomerHandler deleteCustomerHandler;

    @PostMapping
    @Operation(summary = "Crear un cliente con sus direcciones")
    public ResponseEntity<IdResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
        UUID id =
                createCustomerHandler.handle(
                        new CreateCustomerCommand(
                                request.name(),
                                request.email(),
                                request.phone(),
                                request.identificationNumber(),
                                request.addresses().stream()
                                        .map(
                                                com.oriontek.clients.customer.api.dto.AddressRequest
                                                        ::toInput)
                                        .toList()));
        return ResponseEntity.created(URI.create("/api/v1/customers/" + id))
                .body(new IdResponse(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar los datos de un cliente")
    public ResponseEntity<Void> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateCustomerRequest request) {
        updateCustomerHandler.handle(
                new UpdateCustomerCommand(
                        id,
                        request.name(),
                        request.email(),
                        request.phone(),
                        request.identificationNumber()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un cliente (soft delete)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteCustomerHandler.handle(new DeleteCustomerCommand(id));
        return ResponseEntity.noContent().build();
    }
}

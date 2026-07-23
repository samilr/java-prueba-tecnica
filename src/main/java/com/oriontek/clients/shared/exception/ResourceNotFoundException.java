package com.oriontek.clients.shared.exception;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, Object id) {
        return new ResourceNotFoundException("%s no encontrado: %s".formatted(resource, id));
    }
}

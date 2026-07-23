package com.oriontek.clients.shared.cqrs;

public interface CommandHandler<C, R> {

    R handle(C command);
}

package com.oriontek.clients.shared.cqrs;

public interface QueryHandler<Q, R> {

    R handle(Q query);
}

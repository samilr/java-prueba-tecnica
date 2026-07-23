package com.oriontek.clients.auth.api;

import com.oriontek.clients.auth.api.dto.AuthResponse;
import com.oriontek.clients.auth.application.AuthTokens;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AuthApiMapper {

    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "expiresIn", source = "expiresInSeconds")
    AuthResponse toResponse(AuthTokens tokens);
}

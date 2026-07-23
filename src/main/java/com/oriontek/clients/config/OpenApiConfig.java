package com.oriontek.clients.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info =
                @Info(
                        title = "OrionTek Clients API",
                        version = "1.0.0",
                        description =
                                "API REST para la gestión de clientes y sus direcciones (1:N)",
                        contact = @Contact(name = "OrionTek", email = "soporte@oriontek.com")),
        servers = @Server(url = "/", description = "Servidor por defecto"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Introduce el access token JWT obtenido en /api/v1/auth/login")
public class OpenApiConfig {}

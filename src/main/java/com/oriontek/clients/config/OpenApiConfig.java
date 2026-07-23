package com.oriontek.clients.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Comparator;
import java.util.List;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info =
                @Info(
                        title = "OrionTek Clients API",
                        version = "1.0.0",
                        description =
                                "API REST para la gestión de clientes y sus direcciones (1:N)"),
        servers = @Server(url = "/", description = "Servidor por defecto"),
        tags = {
            @Tag(name = "Authentication", description = "Registro, login y renovación de tokens"),
            @Tag(name = "Customers", description = "Gestión de clientes y sus direcciones"),
            @Tag(name = "Addresses", description = "Gestión de direcciones de un cliente")
        })
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Introduce el access token JWT obtenido en /api/v1/auth/login")
public class OpenApiConfig {

    private static final List<String> TAG_ORDER =
            List.of("Authentication", "Customers", "Addresses");

    @Bean
    public OpenApiCustomizer tagOrderCustomizer() {
        return openApi -> {
            if (openApi.getTags() == null) {
                return;
            }
            openApi.getTags()
                    .sort(
                            Comparator.comparingInt(
                                    tag -> {
                                        int position = TAG_ORDER.indexOf(tag.getName());
                                        return position < 0 ? Integer.MAX_VALUE : position;
                                    }));
        };
    }
}

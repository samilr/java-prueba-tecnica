package com.oriontek.clients.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.List;
import java.util.Map;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiResponsesCustomizer {

    private static final String ERROR_SCHEMA = "#/components/schemas/ApiResponseVoid";
    private static final String CUSTOMERS_PREFIX = "/api/v1/customers";

    private static final List<String> CREATED_OPERATIONS =
            List.of(
                    "POST /api/v1/auth/register",
                    "POST /api/v1/customers",
                    "POST /api/v1/customers/{customerId}/addresses");

    @Bean
    public OpenApiCustomizer standardResponsesCustomizer() {
        return openApi ->
                openApi.getPaths()
                        .forEach(
                                (path, pathItem) ->
                                        describeOperations(path, pathItem, errorContent()));
    }

    private void describeOperations(String path, PathItem pathItem, Content errorContent) {
        for (Map.Entry<PathItem.HttpMethod, Operation> entry :
                pathItem.readOperationsMap().entrySet()) {
            String method = entry.getKey().name();
            Operation operation = entry.getValue();
            ApiResponses responses = operation.getResponses();

            promoteToCreated(method + " " + path, responses);

            add(
                    responses,
                    "400",
                    "La solicitud no es válida o no supera las validaciones",
                    errorContent);
            add(responses, "429", "Se ha excedido el límite de solicitudes", errorContent);

            if (path.startsWith(CUSTOMERS_PREFIX)) {
                add(responses, "401", "Falta el token de acceso o no es válido", errorContent);
                add(responses, "403", "El rol autenticado no tiene permisos", errorContent);
            }
            if (path.contains("{")) {
                add(responses, "404", "El recurso solicitado no existe", errorContent);
            }
            if (!"GET".equals(method) && path.startsWith(CUSTOMERS_PREFIX)) {
                add(
                        responses,
                        "409",
                        "Conflicto con una regla de negocio o un duplicado",
                        errorContent);
            }
        }
    }

    private void promoteToCreated(String operationKey, ApiResponses responses) {
        if (!CREATED_OPERATIONS.contains(operationKey)) {
            return;
        }
        ApiResponse created = responses.remove("200");
        if (created != null) {
            responses.addApiResponse("201", created.description("Recurso creado"));
        }
    }

    private void add(ApiResponses responses, String code, String description, Content content) {
        if (responses.containsKey(code)) {
            return;
        }
        responses.addApiResponse(code, new ApiResponse().description(description).content(content));
    }

    private Content errorContent() {
        return new Content()
                .addMediaType(
                        org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                        new MediaType().schema(new Schema<>().$ref(ERROR_SCHEMA)));
    }
}

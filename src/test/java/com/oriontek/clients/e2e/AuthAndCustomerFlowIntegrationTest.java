package com.oriontek.clients.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import com.oriontek.clients.auth.api.dto.AuthResponse;
import com.oriontek.clients.auth.api.dto.LoginRequest;
import com.oriontek.clients.customer.api.dto.AddressRequest;
import com.oriontek.clients.customer.api.dto.CreateCustomerRequest;
import com.oriontek.clients.customer.api.dto.IdResponse;
import com.oriontek.clients.customer.application.query.CustomerDetailView;
import com.oriontek.clients.customer.application.query.CustomerSummaryView;
import com.oriontek.clients.customer.domain.AddressType;
import com.oriontek.clients.shared.web.ApiResponse;
import com.oriontek.clients.support.AbstractIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthAndCustomerFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired private TestRestTemplate restTemplate;

    private String loginAsAdmin() {
        ResponseEntity<ApiResponse<AuthResponse>> response =
                restTemplate.exchange(
                        "/api/v1/auth/login",
                        HttpMethod.POST,
                        new HttpEntity<>(new LoginRequest("admin", "Admin123!")),
                        new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().successful()).isTrue();
        assertThat(response.getBody().error()).isNull();
        assertThat(response.getBody().data().accessToken()).isNotBlank();
        return response.getBody().data().accessToken();
    }

    private HttpHeaders bearer(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    void protectedEndpointRejectsRequestWithoutToken() {
        ResponseEntity<ApiResponse<Void>> response =
                restTemplate.exchange(
                        "/api/v1/customers",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().successful()).isFalse();
        assertThat(response.getBody().data()).isNull();
        assertThat(response.getBody().error().status()).isEqualTo(401);
    }

    @Test
    void loginThenAccessProtectedListing() {
        String token = loginAsAdmin();

        ResponseEntity<ApiResponse<List<CustomerSummaryView>>> response =
                restTemplate.exchange(
                        "/api/v1/customers?size=5",
                        HttpMethod.GET,
                        new HttpEntity<>(bearer(token)),
                        new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().successful()).isTrue();
        assertThat(response.getBody().data()).isNotEmpty();
        assertThat(response.getBody().pagination()).isNotNull();
        assertThat(response.getBody().pagination().size()).isEqualTo(5);
        assertThat(response.getBody().pagination().totalElements()).isPositive();
    }

    @Test
    void createCustomerWithAddressesThenFetchDetail() {
        String token = loginAsAdmin();
        CreateCustomerRequest request =
                new CreateCustomerRequest(
                        "Nuevo Cliente E2E",
                        "e2e@test.com",
                        "809-555-1234",
                        "40200123459",
                        List.of(
                                new AddressRequest(
                                        "Av. Principal 100",
                                        "Santo Domingo",
                                        "DN",
                                        "República Dominicana",
                                        "10101",
                                        AddressType.HOME,
                                        true),
                                new AddressRequest(
                                        "Calle Secundaria 5",
                                        "Santiago",
                                        "Santiago",
                                        null,
                                        "51000",
                                        AddressType.WORK,
                                        false)));

        ResponseEntity<ApiResponse<IdResponse>> created =
                restTemplate.exchange(
                        "/api/v1/customers",
                        HttpMethod.POST,
                        new HttpEntity<>(request, bearer(token)),
                        new ParameterizedTypeReference<>() {});

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        assertThat(created.getBody().successful()).isTrue();
        assertThat(created.getBody().data().id()).isNotNull();

        ResponseEntity<ApiResponse<CustomerDetailView>> detail =
                restTemplate.exchange(
                        "/api/v1/customers/" + created.getBody().data().id(),
                        HttpMethod.GET,
                        new HttpEntity<>(bearer(token)),
                        new ParameterizedTypeReference<>() {});

        assertThat(detail.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(detail.getBody()).isNotNull();
        assertThat(detail.getBody().successful()).isTrue();
        assertThat(detail.getBody().pagination()).isNull();
        assertThat(detail.getBody().data().addresses()).hasSize(2);
        assertThat(detail.getBody().data().email()).isEqualTo("e2e@test.com");
    }
}

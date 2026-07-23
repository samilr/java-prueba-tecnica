package com.oriontek.clients;

import static org.assertj.core.api.Assertions.assertThat;

import com.oriontek.clients.auth.api.dto.AuthResponse;
import com.oriontek.clients.auth.api.dto.LoginRequest;
import com.oriontek.clients.customer.api.dto.AddressRequest;
import com.oriontek.clients.customer.api.dto.CreateCustomerRequest;
import com.oriontek.clients.customer.api.dto.IdResponse;
import com.oriontek.clients.customer.application.query.CustomerDetailView;
import com.oriontek.clients.customer.domain.AddressType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthAndCustomerFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired private TestRestTemplate restTemplate;

    private String loginAsAdmin() {
        ResponseEntity<AuthResponse> response =
                restTemplate.postForEntity(
                        "/api/v1/auth/login",
                        new LoginRequest("admin", "Admin123!"),
                        AuthResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotBlank();
        return response.getBody().accessToken();
    }

    private HttpHeaders bearer(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    void protectedEndpointRejectsRequestWithoutToken() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/api/v1/customers", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void loginThenAccessProtectedListing() {
        String token = loginAsAdmin();

        ResponseEntity<String> response =
                restTemplate.exchange(
                        "/api/v1/customers?size=5",
                        HttpMethod.GET,
                        new HttpEntity<>(bearer(token)),
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("totalElements");
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

        ResponseEntity<IdResponse> created =
                restTemplate.postForEntity(
                        "/api/v1/customers",
                        new HttpEntity<>(request, bearer(token)),
                        IdResponse.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();

        ResponseEntity<CustomerDetailView> detail =
                restTemplate.exchange(
                        "/api/v1/customers/" + created.getBody().id(),
                        HttpMethod.GET,
                        new HttpEntity<>(bearer(token)),
                        CustomerDetailView.class);

        assertThat(detail.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(detail.getBody()).isNotNull();
        assertThat(detail.getBody().addresses()).hasSize(2);
        assertThat(detail.getBody().email()).isEqualTo("e2e@test.com");
    }
}

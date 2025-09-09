package co.com.crediya.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;

class UserRestConsumerTest {

    private static UserRestConsumer userRestConsumer;

    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        var webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();
        var objectMapper = new ObjectMapper();
        var registry = CircuitBreakerRegistry.ofDefaults();

        userRestConsumer = new UserRestConsumer(webClient, objectMapper, registry);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("findByIdentityDocument should return User on success")
    void testFindByIdentityDocumentSuccess() {
        String responseBody = """
                    {
                        "id": "1b9200f7-5d1b-47fe-b691-5437a972b947",
                        "name": "Jenner",
                        "lastName": "Durand",
                        "email": "jennerjose3619@gmail.com",
                        "identityDocument": "12345678",
                        "baseSalary": 1222222
                    }
                """;

        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody(responseBody));

        StepVerifier.create(userRestConsumer.findByIdentityDocument("12345678"))
                .expectNextMatches(user -> "12345678".equals(user.getIdentityDocument())
                        && "jennerjose3619@gmail.com".equals(user.getEmail()))
                .verifyComplete();
    }

    @Test
    @DisplayName("findByIdentityDocument should return empty on 404")
    void testFindByIdentityDocumentNotFound() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value()));

        StepVerifier.create(userRestConsumer.findByIdentityDocument("00000000"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("findByIdentityDocument should return empty on server error")
    void testFindByIdentityDocumentServerError() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        StepVerifier.create(userRestConsumer.findByIdentityDocument("error"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("findByIdentityDocument should return ErrorResponseDTO")
    void testFindByIdentityDocumentErrorResponse() {

        String responseBody = """
                    {
                        "timestamp": "2024-10-10T10:00:00Z",
                        "status": 500,
                        "error": "Internal Server Error",
                        "message": "An unexpected error occurred",
                        "path": "/api/users/identityDocument/error",
                        "requestId": "req-123456"
                    }
                """;
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody(responseBody));

        StepVerifier.create(userRestConsumer.findByIdentityDocument("error"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("findByIdentityDocument should trigger fallback when circuit breaker is open")
    void testFindByIdentityDocumentFallback() {
        mockBackEnd.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        StepVerifier.create(userRestConsumer.findByIdentityDocument("any"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("findByIdentityDocument should return empty when error response body cannot be parsed")
    void testFindByIdentityDocumentErrorResponseInvalidJson() {
        String invalidJson = "{ invalid-json-response }";

        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody(invalidJson));

        StepVerifier.create(userRestConsumer.findByIdentityDocument("parse-error"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("findAllByIdentityDocuments should return empty on 500 with valid ErrorResponseDTO")
    void testFindAllByIdentityDocumentsServerErrorWithValidErrorResponseDTO() {
        String responseBody = """
            {
                "timestamp": "2024-10-10T10:00:00Z",
                "status": 500,
                "error": "Internal Server Error",
                "message": "Unexpected error in AuthService",
                "path": "/v1/usuarios/identity-documents",
                "requestId": "req-98765"
            }
        """;

        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody(responseBody));

        StepVerifier.create(userRestConsumer.findAllByIdentityDocuments(List.of("12345678", "87654321")))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("findAllByIdentityDocuments should return empty on 500 with invalid error body")
    void testFindAllByIdentityDocumentsServerErrorWithInvalidErrorResponseDTO() {
        String invalidJson = "{ invalid-json-response }";

        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody(invalidJson));

        StepVerifier.create(userRestConsumer.findAllByIdentityDocuments(List.of("11111111", "22222222")))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("findAllByIdentityDocuments should trigger fallback when circuit breaker is open")
    void testFindAllByIdentityDocumentsFallback() {
        mockBackEnd.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        StepVerifier.create(userRestConsumer.findAllByIdentityDocuments(List.of("12345678", "87654321")))
                .expectNextCount(0)
                .verifyComplete();
    }
}
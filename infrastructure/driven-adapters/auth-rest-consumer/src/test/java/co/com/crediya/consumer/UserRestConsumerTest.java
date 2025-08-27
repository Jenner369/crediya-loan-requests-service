package co.com.crediya.consumer;


import co.com.crediya.consumer.consumer.UserRestConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import static org.mockito.Mockito.*;

class UserRestConsumerTest {

    private static UserRestConsumer userRestConsumer;

    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        var webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();
        var objectMapper = mock(ObjectMapper.class);

        userRestConsumer = new UserRestConsumer(webClient, objectMapper);
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
}
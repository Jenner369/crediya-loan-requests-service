package co.com.crediya.authentication.filter;

import co.com.crediya.api.authentication.filter.SecurityContextRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class SecurityContextRepositoryTest {

    private ReactiveAuthenticationManager authenticationManager;
    private SecurityContextRepository repository;

    @BeforeEach
    void setUp() {
        authenticationManager = Mockito.mock(ReactiveAuthenticationManager.class);
        repository = new SecurityContextRepository(authenticationManager);
    }

    @Test
    void shouldReturnSecurityContextWhenAuthorizationHeaderIsPresent() {
        var token = "valid-token";
        var auth = new UsernamePasswordAuthenticationToken("principal", "credentials");

        when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(Mono.just(auth));

        var request = MockServerHttpRequest.get("/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        var exchange = MockServerWebExchange.from(request);

        StepVerifier.create(repository.load(exchange))
                .assertNext(context -> {
                    assertThat(context).isInstanceOf(SecurityContext.class);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenAuthorizationHeaderIsMissing() {
        var request = MockServerHttpRequest.get("/test").build();
        var exchange = MockServerWebExchange.from(request);

        StepVerifier.create(repository.load(exchange))
                .verifyComplete();
    }
}

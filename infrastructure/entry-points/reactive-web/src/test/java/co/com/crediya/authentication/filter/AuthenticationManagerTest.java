package co.com.crediya.authentication.filter;

import co.com.crediya.api.authentication.filter.AuthenticationManager;
import co.com.crediya.api.authentication.filter.AuthUserDetails;
import co.com.crediya.api.contract.TokenValidator;
import co.com.crediya.api.dto.common.ClaimsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationManagerTest {

    private TokenValidator tokenValidator;
    private AuthenticationManager authManager;

    @BeforeEach
    void setup() {
        tokenValidator = mock(TokenValidator.class);
        authManager = new AuthenticationManager(tokenValidator);
    }

    @Test
    void authenticateShouldReturnAuthenticationWhenTokenIsValid() {
        var claims = new ClaimsDTO("123", "user@example.com", "role-1");
        when(tokenValidator.validateTokenAndGetClaims(anyString())).thenReturn(Mono.just(claims));

        Authentication auth = new UsernamePasswordAuthenticationToken("token123", "token123");

        Mono<Authentication> result = authManager.authenticate(auth);

        StepVerifier.create(result)
                .assertNext(authentication -> {
                    AuthUserDetails userDetails = (AuthUserDetails) authentication.getPrincipal();

                    assertThat(userDetails.getId()).isEqualTo("123");
                    assertThat(userDetails.getUsername()).isEqualTo("user@example.com");
                    assertThat(userDetails.getAuthorities())
                            .hasSize(1)
                            .extracting("authority")
                            .containsExactly("role-1");
                })
                .verifyComplete();

        verify(tokenValidator, times(1)).validateTokenAndGetClaims("token123");
    }

    @Test
    void authenticateShouldErrorWhenTokenIsInvalid() {
        when(tokenValidator.validateTokenAndGetClaims(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Token invalid")));

        Authentication auth = new UsernamePasswordAuthenticationToken("badToken", "badToken");

        StepVerifier.create(authManager.authenticate(auth))
                .expectErrorMessage("Token invalid")
                .verify();

        verify(tokenValidator, times(1)).validateTokenAndGetClaims("badToken");
    }
}

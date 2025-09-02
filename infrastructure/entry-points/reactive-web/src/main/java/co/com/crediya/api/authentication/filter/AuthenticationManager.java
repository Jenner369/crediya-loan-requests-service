package co.com.crediya.api.authentication.filter;

import co.com.crediya.api.contract.TokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final TokenValidator tokenValidator;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        return tokenValidator.validateTokenAndGetClaims(authToken)
                .map(claimsDTO ->
                        new AuthUserDetails(
                                claimsDTO.userId(),
                                claimsDTO.username(),
                                authToken,
                                List.of(claimsDTO::roleId)
                        )
                ).map(userDetails ->
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                userDetails.getPassword(),
                                userDetails.getAuthorities()
                        )
                );
    }
}

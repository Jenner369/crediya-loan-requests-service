package co.com.crediya.api.contract;

import co.com.crediya.api.dto.common.ClaimsDTO;
import reactor.core.publisher.Mono;

public interface TokenValidator {
    Mono<ClaimsDTO> validateTokenAndGetClaims(String token);
}

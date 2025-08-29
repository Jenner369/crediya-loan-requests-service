package co.com.crediya.consumer.consumer;

import co.com.crediya.consumer.dto.common.ErrorResponseDTO;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserRestConsumer implements UserRepository {

    private final ObjectMapper mapper;
    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;

    private static final String BASE_PATH = "/v1/usuarios";

    public UserRestConsumer(
            @Qualifier("authWebClient")
            WebClient webClient,
            ObjectMapper mapper,
            CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        this.webClient = webClient;
        this.mapper = mapper;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("userRestConsumer");
    }

    @Override
    public Mono<User> findByIdentityDocument(String identityDocument) {
        var path = BASE_PATH + "/identity-document/";

        return webClient
                .get()
                .uri(path + "{identityDocument}", identityDocument)
                .retrieve()
                .bodyToMono(User.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(ex -> findUserFallback(identityDocument, ex));
    }

    public Mono<User> findUserFallback(String identityDocument, Throwable ex) {
        var path = BASE_PATH + "/identity-document/";

        if (ex instanceof WebClientResponseException wcre) {
            if (wcre.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("[{}] ExternalUserNotFound - status {} {} {}: Usuario no encontrado en AuthService",
                        getClass().getSimpleName(),
                        wcre.getStatusCode().value(),
                        "GET",
                        path + identityDocument
                );
                return Mono.empty();
            }

            String externalMessage = null;
            try {
                var externalError = mapper.readValue(wcre.getResponseBodyAsString(), ErrorResponseDTO.class);
                if (externalError != null) {
                    externalMessage = externalError.message();
                }
            } catch (Exception parseEx) {
                log.warn("[{}] No se pudo parsear body de WebClientResponseException: {} {}: {}",
                        getClass().getSimpleName(),
                        wcre.getStatusCode().value(),
                        path + identityDocument,
                        parseEx.getMessage()
                );
            }

            log.warn("[{}] Error externo desde AuthService: {} {}: {}",
                    getClass().getSimpleName(),
                    wcre.getStatusCode().value(),
                    path + identityDocument,
                    externalMessage != null ? externalMessage : wcre.getMessage()
            );

        } else {
            log.error("[{}] Error inesperado al consumir AuthService: {} {}: {}",
                    getClass().getSimpleName(),
                    "GET",
                    path + identityDocument,
                    ex.getMessage(),
                    ex
            );

            return Mono.empty();
        }

        return Mono.empty();
    }
}

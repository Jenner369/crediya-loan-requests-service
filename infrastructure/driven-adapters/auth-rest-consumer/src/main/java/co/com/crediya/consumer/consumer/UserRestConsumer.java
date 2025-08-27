package co.com.crediya.consumer.consumer;

import co.com.crediya.consumer.dto.common.ErrorResponseDTO;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserRestConsumer implements UserRepository {

    private final ObjectMapper mapper;
    private final WebClient webClient;

    private static final String BASE_PATH = "/v1/usuarios";

    public UserRestConsumer(@Qualifier("authWebClient") WebClient webClient, ObjectMapper mapper) {
        this.webClient = webClient;
        this.mapper = mapper;
    }

    @Override
    @CircuitBreaker(name = "findByIdentityDocument", fallbackMethod = "findUserFallback")
    public Mono<User> findByIdentityDocument(String identityDocument) {
        var path = BASE_PATH + "/identity-document/";

        return webClient
                .get()
                .uri(path + "{identityDocument}", identityDocument)
                .retrieve()
                .bodyToMono(User.class)
                .onErrorResume(ex -> handleWebClientError(ex, path, identityDocument));
    }

    public Mono<User> findUserFallback(String identityDocument, Throwable throwable) {
        log.error("[{}] Fallback activado para findByIdentityDocument con identityDocument {}: {}",
                getClass().getSimpleName(),
                identityDocument,
                throwable.getMessage(),
                throwable
        );

        return Mono.empty();
    }

    private Mono<User> handleWebClientError(Throwable ex, String path, String identityDocument) {
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

            try {
                var externalError = mapper.readValue(wcre.getResponseBodyAsString(), ErrorResponseDTO.class);
                log.warn("[{}] Error externo desde AuthService: {} {}: {}",
                        getClass().getSimpleName(),
                        wcre.getStatusCode().value(),
                        path + identityDocument,
                        externalError.message()
                );
            } catch (Exception parseEx) {
                log.error("[{}] No se pudo parsear WebClientResponseException: {} {}: {}",
                        getClass().getSimpleName(),
                        wcre.getStatusCode().value(),
                        path + identityDocument,
                        wcre.getMessage(),
                        parseEx
                );
            }
        } else {
            log.error("[{}] Error inesperado al consumir AuthService: {} {}: {}",
                    getClass().getSimpleName(),
                    "GET",
                    path + identityDocument,
                    ex.getMessage(),
                    ex
            );
        }

        return Mono.empty();
    }
}

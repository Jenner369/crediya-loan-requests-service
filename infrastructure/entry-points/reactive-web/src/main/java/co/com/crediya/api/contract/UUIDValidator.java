package co.com.crediya.api.contract;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UUIDValidator {
    Mono<UUID> validate(String id);
    Mono<Void> validateExists(String id);
}

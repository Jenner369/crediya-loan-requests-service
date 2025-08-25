package co.com.crediya.api.presentation.contract;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UUIDValidator {
    Mono<UUID> validate(String id);
}

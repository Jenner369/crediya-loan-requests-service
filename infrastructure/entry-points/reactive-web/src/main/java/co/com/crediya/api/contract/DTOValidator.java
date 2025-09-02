package co.com.crediya.api.contract;

import reactor.core.publisher.Mono;

public interface DTOValidator {
    <T> Mono<T> validate(T dto);
}

package co.com.crediya.model.common.gateways;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

public interface TransactionalGateway {
    <T> Mono<T> execute(Supplier<Mono<T>> action);
    <T> Flux<T> executeMany(Supplier<Flux<T>> action);
}


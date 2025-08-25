package co.com.crediya.r2dbc.transaction;

import co.com.crediya.model.common.gateways.TransactionalGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class TransactionalGatewayImpl implements TransactionalGateway {

    private final TransactionalOperator transactionalOperator;

    @Override
    public <T> Mono<T> execute(Supplier<Mono<T>> action) {
        return transactionalOperator.transactional(action.get());
    }

    @Override
    public <T> Flux<T> executeMany(Supplier<Flux<T>> action) {
        return transactionalOperator.transactional(action.get());
    }
}

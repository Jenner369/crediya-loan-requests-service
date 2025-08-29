package co.com.crediya.r2dbc.transaction;

import co.com.crediya.model.common.gateways.TransactionalGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TransactionalGatewayImplTest {

    private TransactionalOperator transactionalOperator;
    private TransactionalGateway gateway;

    @BeforeEach
    void setUp() {
        transactionalOperator = Mockito.mock(TransactionalOperator.class);
        gateway = new TransactionalGatewayImpl(transactionalOperator);
    }

    @Test
    void shouldExecuteMonoInsideTransaction() {
        Mono<String> original = Mono.just("ok");
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mono<String> result = gateway.execute(() -> original);

        StepVerifier.create(result)
                .expectNext("ok")
                .verifyComplete();
    }

    @Test
    void shouldExecuteFluxInsideTransaction() {
        Flux<Integer> original = Flux.just(1, 2, 3);
        when(transactionalOperator.transactional(any(Flux.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Flux<Integer> result = gateway.executeMany(() -> original);

        StepVerifier.create(result)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }
}

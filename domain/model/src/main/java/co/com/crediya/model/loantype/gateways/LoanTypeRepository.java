package co.com.crediya.model.loantype.gateways;

import co.com.crediya.model.loantype.LoanType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LoanTypeRepository {
    Mono<LoanType> findById(UUID id);
    Flux<LoanType> findAll();
}

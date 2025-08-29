package co.com.crediya.model.loanapplication.gateways;

import co.com.crediya.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LoanApplicationRepository {
    Mono<LoanApplication> findById(UUID id);

    Mono<LoanApplication> save(LoanApplication loanApplication);
}

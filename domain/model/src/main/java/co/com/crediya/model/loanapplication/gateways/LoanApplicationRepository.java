package co.com.crediya.model.loanapplication.gateways;

import co.com.crediya.model.loanapplication.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LoanApplicationRepository {
    Flux<LoanApplication> findAllWithDetails(
            String identityDocument,
            String statusCode,
            String loanTypeCode,
            Boolean autoApproval,
            Integer pageNumber,
            Integer pageSize
    );
    Mono<LoanApplication> findById(UUID id);

    Mono<LoanApplication> save(LoanApplication loanApplication);
    Mono<Long> countByFilters(
            String identityDocument,
            String statusCode,
            String loanTypeCode,
            Boolean autoApproval);
}

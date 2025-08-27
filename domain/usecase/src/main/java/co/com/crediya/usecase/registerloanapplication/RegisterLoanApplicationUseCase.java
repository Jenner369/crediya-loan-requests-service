package co.com.crediya.usecase.registerloanapplication;

import co.com.crediya.contract.ReactiveUseCase;
import co.com.crediya.exception.LoanTypeNotFoundException;
import co.com.crediya.model.common.gateways.TransactionalGateway;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.status.enums.Statuses;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterLoanApplicationUseCase implements ReactiveUseCase<LoanApplication, Mono<LoanApplication>> {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final TransactionalGateway transactionalGateway;

    @Override
    public Mono<LoanApplication> execute(LoanApplication loanApplication) {
        return transactionalGateway.execute(
                () -> setDefaultStatus(loanApplication)
                        .flatMap(this::validateLoanTypeExists)
                        .flatMap(loanApplicationRepository::save)
        );
    }

    private Mono<LoanApplication> validateLoanTypeExists(LoanApplication loanApplication) {
        return loanTypeRepository.existsById(loanApplication.getLoanTypeId())
                .flatMap(exists -> {
                    if (Boolean.FALSE.equals(exists)) {
                        return Mono.error(new LoanTypeNotFoundException());
                    }

                    return Mono.just(loanApplication);
                });
    }

    private Mono<LoanApplication> setDefaultStatus(LoanApplication loanApplication) {
        loanApplication.setStatusId(Statuses.PENDING.getId());

        return Mono.just(loanApplication);
    }
}

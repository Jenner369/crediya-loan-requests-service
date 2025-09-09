package co.com.crediya.usecase.applyloanapplicationdecision;

import co.com.crediya.contract.ReactiveUseCase;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ApplyLoanApplicationDecisionUseCase implements ReactiveUseCase<ApplyLoanApplicationDecisionUseCaseInput, Mono<LoanApplication>> {
    private final LoanApplicationRepository loanApplicationRepository;

    @Override
    public Mono<LoanApplication> execute(ApplyLoanApplicationDecisionUseCaseInput input) {
        return loanApplicationRepository.findById(input.id())
                .flatMap(loanApplication
                        -> changeStatusFromDecision(loanApplication, input.decisionStatusCode()))
                .flatMap(loanApplicationRepository::save);
    }

    private Mono<LoanApplication> changeStatusFromDecision(
            LoanApplication loanApplication,
            String decisionStatusCode
    ) {
        return Mono.fromCallable(() -> {
            loanApplication.validateCanChangeStatusFromDecision(decisionStatusCode);
            loanApplication.setStatusCode(decisionStatusCode);

            return loanApplication;
        });
    }
}

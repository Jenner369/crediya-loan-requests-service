package co.com.crediya.usecase.applyloanapplicationdecision;

import co.com.crediya.contract.ReactiveUseCase;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.events.LoanApplicationApprovedEvent;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationEventPublisher;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
public class ApplyLoanApplicationDecisionUseCase implements ReactiveUseCase<ApplyLoanApplicationDecisionUseCaseInput, Mono<LoanApplication>> {
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanApplicationEventPublisher publisher;

    @Override
    public Mono<LoanApplication> execute(ApplyLoanApplicationDecisionUseCaseInput input) {
        return loanApplicationRepository.findById(input.id())
                .flatMap(loanApplication
                        -> changeStatusFromDecision(loanApplication, input.decisionStatusCode()))
                .flatMap(loanApplicationRepository::save)
                .flatMap(this::publishIfApproved);
    }

    private Mono<LoanApplication> publishIfApproved(LoanApplication loanApplication) {
        if (Boolean.FALSE.equals(loanApplication.isApproved())) {
            return Mono.just(loanApplication);
        }

        return publisher.publishApproved(
                LoanApplicationApprovedEvent.fromApprovedLoanApplication(
                        loanApplication,
                        new Date()
                )).thenReturn(loanApplication);
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

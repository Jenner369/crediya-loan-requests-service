package co.com.crediya.usecase.changeloanapplicationstatus;

import co.com.crediya.contract.ReactiveUseCase;
import co.com.crediya.exception.LoanApplicationNotFoundException;
import co.com.crediya.exception.UserNotFoundException;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.events.LoanApplicationApprovedEvent;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationEventPublisher;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loantype.enums.LoanTypes;
import co.com.crediya.model.status.enums.Statuses;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.changeloanapplicationstatus.wrappers.LoanApplicationWithEvent;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
public class ChangeLoanApplicationStatusUseCase
        implements ReactiveUseCase<ChangeLoanApplicationStatusUseCaseInput, Mono<LoanApplication>> {

    private final LoanApplicationRepository repository;
    private final UserRepository userRepository;
    private final LoanApplicationEventPublisher publisher;

    @Override
    public Mono<LoanApplication> execute(ChangeLoanApplicationStatusUseCaseInput input) {
        return getLoanApplicationById(input.loanApplicationId())
                .flatMap(loanApplication
                        -> validateCanChangeStatus(loanApplication, input.newStatusCode()))
                .map(this::setLoanTypeAndStatus)
                .flatMap(this::setUser)
                .flatMap(loanApplication
                        -> changeStatus(loanApplication, input.newStatusCode()))
                .flatMap(this::persistAndPublishChange)
                .flatMap(this::publishIfApproved);
    }

    private Mono<LoanApplication> getLoanApplicationById(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new LoanApplicationNotFoundException()));
    }

    private Mono<LoanApplication> validateCanChangeStatus(LoanApplication loanApplication, String newStatusCode) {
        return Mono.fromCallable(() ->{
            loanApplication.validateCanChangeStatus(newStatusCode);

            return loanApplication;
        });
    }

    private LoanApplication setLoanTypeAndStatus(LoanApplication loanApplication) {
        loanApplication.setLoanType(LoanTypes.fromCode(loanApplication.getLoanTypeCode()));
        loanApplication.setStatus(Statuses.fromCode(loanApplication.getStatusCode()));

        return loanApplication;
    }

    private Mono<LoanApplication> setUser(LoanApplication loanApplication) {
        return userRepository.findByIdentityDocument(loanApplication.getIdentityDocument())
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(user -> {
                    loanApplication.setUser(user);
                    return loanApplication;
                });
    }

    private Mono<LoanApplicationWithEvent> changeStatus(LoanApplication loanApplication, String newStatusCode) {
        return Mono.fromCallable(() -> {
            loanApplication.setStatus(Statuses.fromCode(newStatusCode));
            var event = loanApplication.changeStatus(newStatusCode);

            return new LoanApplicationWithEvent(loanApplication, event);
        });
    }

    private Mono<LoanApplication> persistAndPublishChange(LoanApplicationWithEvent wrapper) {
        return repository.save(wrapper.loanApplication())
                .flatMap(saved -> publisher.publishChange(wrapper.event())
                        .thenReturn(saved));
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
}

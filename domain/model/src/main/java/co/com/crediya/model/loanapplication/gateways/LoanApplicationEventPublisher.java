package co.com.crediya.model.loanapplication.gateways;

import co.com.crediya.model.loanapplication.events.LoanApplicationApprovedEvent;
import co.com.crediya.model.loanapplication.events.LoanApplicationChangedEvent;
import co.com.crediya.model.loanapplication.events.LoanApplicationValidationEvent;
import reactor.core.publisher.Mono;

public interface LoanApplicationEventPublisher {
    Mono<Void> publishChange(LoanApplicationChangedEvent event);
    Mono<Void> publishValidation(LoanApplicationValidationEvent event);
    Mono<Void> publishApproved(LoanApplicationApprovedEvent event);
}

package co.com.crediya.model.loanapplication.gateways;

import co.com.crediya.model.loanapplication.events.LoanApplicationChangedEvent;
import reactor.core.publisher.Mono;

public interface LoanApplicationEventPublisher {
    Mono<Void> publishChange(LoanApplicationChangedEvent event);
}

package co.com.crediya.sqs.listener;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.exceptions.CannotChangeLoanApplicationStatusException;
import co.com.crediya.usecase.applyloanapplicationdecision.ApplyLoanApplicationDecisionUseCase;
import co.com.crediya.usecase.applyloanapplicationdecision.ApplyLoanApplicationDecisionUseCaseInput;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final ApplyLoanApplicationDecisionUseCase applyLoanApplicationDecisionUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> apply(Message message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message.body(), ApplyLoanApplicationDecisionUseCaseInput.class))
                .doOnError(e -> log.error("Error converting message to input: {}", e.getMessage()))
                .flatMap(applyLoanApplicationDecisionUseCase::execute)
                .doOnSuccess(result -> log.info("Message processed successfully: {}", result))
                .onErrorResume(e -> handleError(e, message))
                .then();
    }

    private Mono<LoanApplication> handleError(Throwable e, Message message) {
        if (e instanceof CannotChangeLoanApplicationStatusException) {
            log.warn("Controlled business exception: {}. Skipping message {}", e.getMessage(), message.messageId());

            return Mono.empty();
        }
        log.error("Unexpected error processing message {}: {}", message.messageId(), e.getMessage(), e);

        return Mono.error(e);
    }
}

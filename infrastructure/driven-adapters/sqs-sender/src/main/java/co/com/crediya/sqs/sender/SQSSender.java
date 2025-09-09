package co.com.crediya.sqs.sender;

import co.com.crediya.model.loanapplication.events.LoanApplicationChangedEvent;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationEventPublisher;
import co.com.crediya.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements LoanApplicationEventPublisher {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> publishChange(LoanApplicationChangedEvent event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .doOnNext(json -> log.info("Mapped JSON message to send: {}", json))
                .flatMap(this::send)
                .then()
                .onErrorMap(e -> new RuntimeException("Error serializing event to JSON", e));
    }
}

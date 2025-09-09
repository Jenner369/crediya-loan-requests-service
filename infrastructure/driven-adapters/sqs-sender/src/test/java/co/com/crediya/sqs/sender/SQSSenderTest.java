package co.com.crediya.sqs.sender;

import co.com.crediya.model.loanapplication.events.LoanApplicationChangedEvent;
import co.com.crediya.model.loanapplication.events.LoanApplicationValidationEvent;
import co.com.crediya.model.loantype.enums.LoanTypes;
import co.com.crediya.model.status.enums.Statuses;
import co.com.crediya.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SQSSenderTest {

    @Mock
    private SQSSenderProperties properties;

    @Mock
    private SqsAsyncClient client;

    private ObjectMapper objectMapper;

    private SQSSender sqsSender;

    private LoanApplicationChangedEvent changedEvent;
    private LoanApplicationValidationEvent validationEvent;

    @BeforeEach
    void setUp() {
        objectMapper = spy(new ObjectMapper());
        sqsSender = new SQSSender(properties, client, objectMapper);

        changedEvent = new LoanApplicationChangedEvent(
                UUID.randomUUID().toString(),
                new BigDecimal("500000"),
                12,
                LoanTypes.BUSINESS_LOAN.getName(),
                Statuses.APPROVED.getName(),
                "jennerjose369@gmail.com",
                "Jenner",
                "Durand"
        );

        validationEvent = new LoanApplicationValidationEvent(
                UUID.randomUUID().toString(),
                new BigDecimal("500000"),
                12,
                LoanTypes.BUSINESS_LOAN.getName(),
                new BigDecimal("0.24"),
                new BigDecimal("0"),
                "Jenner",
                "Durand",
                "jennerjose369@gmail.com",
                "12345678",
                new BigDecimal("5000000")
        );
    }

    @Test
    void publishChangeShouldSendEventSuccessfully() throws Exception {
        var response = SendMessageResponse.builder().messageId("msg-123").build();
        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        StepVerifier.create(sqsSender.publishChange(changedEvent))
                .verifyComplete();

        verify(objectMapper).writeValueAsString(changedEvent);
        verify(client).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    void publishValidationShouldSendEventSuccessfully() throws Exception {
        var response = SendMessageResponse.builder().messageId("msg-456").build();
        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        StepVerifier.create(sqsSender.publishValidation(validationEvent))
                .verifyComplete();

        verify(objectMapper).writeValueAsString(validationEvent);
        verify(client).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    void publishChangeShouldReturnErrorOnJsonProcessingException() throws Exception {
        doThrow(new RuntimeException("JSON error")).when(objectMapper).writeValueAsString(changedEvent);

        StepVerifier.create(sqsSender.publishChange(changedEvent))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("JSON error"))
                .verify();
    }

    @Test
    void publishValidationShouldReturnErrorOnJsonProcessingException() throws Exception {
        doThrow(new RuntimeException("JSON error")).when(objectMapper).writeValueAsString(validationEvent);

        StepVerifier.create(sqsSender.publishValidation(validationEvent))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("JSON error"))
                .verify();
    }
}

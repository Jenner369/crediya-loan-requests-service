package co.com.crediya.usecase.changeloanapplicationstatus;

import co.com.crediya.exception.LoanApplicationNotFoundException;
import co.com.crediya.exception.UserNotFoundException;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationEventPublisher;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loantype.enums.LoanTypes;
import co.com.crediya.model.status.enums.Statuses;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeLoanApplicationStatusUseCaseTest {

    @Mock
    private LoanApplicationRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanApplicationEventPublisher publisher;

    @InjectMocks
    private ChangeLoanApplicationStatusUseCase useCase;

    private LoanApplication loanApplication;
    private UUID loanApplicationId;

    @BeforeEach
    void setUp() {
        loanApplicationId = UUID.randomUUID();
        loanApplication = LoanApplication.builder()
                .id(loanApplicationId)
                .amount(new BigDecimal("10000"))
                .term(12)
                .identityDocument("12345678")
                .email("jennerjose369@gmail.com")
                .statusCode(Statuses.PENDING.getCode())
                .loanTypeCode(LoanTypes.PERSONAL_LOAN.getCode())
                .build();
    }

    @Test
    void shouldChangeLoanApplicationStatusSuccessfully() {
        var user = User.builder()
                .id(UUID.randomUUID())
                .name("Jenner")
                .lastName("Durand")
                .email("jennerjose369@gmail.com")
                .identityDocument("12345678")
                .baseSalary(new BigDecimal("5000000"))
                .build();

        when(repository.findById(loanApplicationId)).thenReturn(Mono.just(loanApplication));
        when(userRepository.findByIdentityDocument("12345678")).thenReturn(Mono.just(user));
        when(repository.save(any(LoanApplication.class))).thenReturn(Mono.just(loanApplication));
        when(publisher.publishChange(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(new ChangeLoanApplicationStatusUseCaseInput(
                        loanApplicationId,
                        Statuses.APPROVED.getCode()
                )))
                .expectNextMatches(app -> app.getStatusCode().equals(Statuses.APPROVED.getCode()))
                .verifyComplete();

        verify(repository).save(any(LoanApplication.class));
        verify(publisher).publishChange(any());
    }

    @Test
    void shouldThrowLoanApplicationNotFoundException() {
        when(repository.findById(loanApplicationId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(new ChangeLoanApplicationStatusUseCaseInput(
                        loanApplicationId,
                        Statuses.APPROVED.getCode()
                )))
                .expectErrorMatches(LoanApplicationNotFoundException.class::isInstance)
                .verify();
    }

    @Test
    void shouldThrowUserNotFoundException() {
        when(repository.findById(loanApplicationId)).thenReturn(Mono.just(loanApplication));
        when(userRepository.findByIdentityDocument("12345678")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(new ChangeLoanApplicationStatusUseCaseInput(
                        loanApplicationId,
                        Statuses.APPROVED.getCode()
                )))
                .expectErrorMatches(UserNotFoundException.class::isInstance)
                .verify();
    }
}

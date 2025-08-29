package co.com.crediya.usecase.registerloanapplication;

import co.com.crediya.exception.LoanTypeNotFoundException;
import co.com.crediya.model.common.gateways.TransactionalGateway;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loantype.enums.LoanTypes;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.status.enums.Statuses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
class RegisterLoanApplicationUseCaseTest {
    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private TransactionalGateway transactionalGateway;

    @InjectMocks
    private RegisterLoanApplicationUseCase registerLoanApplicationUseCase;

    private LoanApplication loanApplication;

    @BeforeEach
    void setUp() {
        loanApplication = LoanApplication.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal("10000"))
                .term(12)
                .identityDocument("12345678")
                .email("jennerjose369@gmail.com")
                .statusCode(Statuses.PENDING.getCode())
                .loanTypeCode(LoanTypes.PERSONAL_LOAN.getCode())
                .build();
    }

    @Test
    void shouldRegisterLoanApplicationSuccessfully() {
        when(loanApplicationRepository.save(loanApplication)).thenReturn(Mono.just(loanApplication));
        when(loanTypeRepository.existsByCode(LoanTypes.PERSONAL_LOAN.getCode())).thenReturn(Mono.just(true));
        when(transactionalGateway.execute(any()))
                .thenAnswer(invocation -> {
                    Supplier<Mono<?>> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        StepVerifier.create(registerLoanApplicationUseCase.execute(loanApplication))
                .expectNextMatches(u -> u.getIdentityDocument().equals("12345678"))
                .verifyComplete();

        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    void shouldThrowExceptionWhenLoanTypeNotFound() {
        when(loanTypeRepository.existsByCode(LoanTypes.PERSONAL_LOAN.getCode())).thenReturn(Mono.just(false));
        when(transactionalGateway.execute(any()))
                .thenAnswer(invocation -> {
                    Supplier<Mono<?>> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        StepVerifier.create(registerLoanApplicationUseCase.execute(loanApplication))
                .expectErrorMatches(LoanTypeNotFoundException.class::isInstance)
                .verify();
    }
}

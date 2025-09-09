package co.com.crediya.usecase.registerloanapplication;

import co.com.crediya.exception.LoanApplicationOwnerMismatchException;
import co.com.crediya.exception.LoanTypeNotFoundException;
import co.com.crediya.model.common.gateways.TransactionalGateway;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationEventPublisher;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loantype.enums.LoanTypes;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.status.enums.Statuses;
import co.com.crediya.model.user.User;
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

    @Mock
    private LoanApplicationEventPublisher loanApplicationEventPublisher;

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
                .loanTypeCode(LoanTypes.STUDENT_LOAN.getCode())
                .build();
    }

    @Test
    void shouldRegisterLoanApplicationSuccessfully() {
        var user = User.builder()
                .id(UUID.randomUUID())
                .name("Jenner")
                .lastName("Durand")
                .email("jennerjose369@gmail.com")
                .identityDocument("12345678")
                .baseSalary(new BigDecimal("5000000"))
                .build();

        when(loanApplicationRepository.save(loanApplication)).thenReturn(Mono.just(loanApplication));
        when(loanTypeRepository.existsByCode(LoanTypes.STUDENT_LOAN.getCode())).thenReturn(Mono.just(true));
        when(transactionalGateway.execute(any()))
                .thenAnswer(invocation -> {
                    Supplier<Mono<?>> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        StepVerifier.create(registerLoanApplicationUseCase.execute(new RegisterLoanApplicationUseCaseInput(
                        loanApplication,
                        user,
                        user
                )))
                .expectNextMatches(u -> u.getIdentityDocument().equals("12345678"))
                .verifyComplete();

        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    void shouldThrowExceptionWhenLoanTypeNotFound() {
        var user = User.builder()
                .id(UUID.randomUUID())
                .name("Jenner")
                .lastName("Durand")
                .email("jennerjose369@gmail.com")
                .identityDocument("12345678")
                .baseSalary(new BigDecimal("5000000"))
                .build();

        when(loanTypeRepository.existsByCode(LoanTypes.STUDENT_LOAN.getCode())).thenReturn(Mono.just(false));
        when(transactionalGateway.execute(any()))
                .thenAnswer(invocation -> {
                    Supplier<Mono<?>> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        StepVerifier.create(registerLoanApplicationUseCase.execute(new RegisterLoanApplicationUseCaseInput(
                        loanApplication,
                        user,
                        user
                )))
                .expectErrorMatches(LoanTypeNotFoundException.class::isInstance)
                .verify();
    }

    @Test
    void shouldThrowExceptionWhenOwnerMismatch() {
        var user = User.builder().id(UUID.randomUUID()).build();
        var authUser = User.builder().id(UUID.randomUUID()).build();

        when(transactionalGateway.execute(any())).thenAnswer(invocation -> {
            Supplier<Mono<?>> supplier = invocation.getArgument(0);
            return supplier.get();
        });

        StepVerifier.create(registerLoanApplicationUseCase.execute(
                        new RegisterLoanApplicationUseCaseInput(loanApplication, user, authUser)
                ))
                .expectErrorMatches(LoanApplicationOwnerMismatchException.class::isInstance)
                .verify();
    }

    @Test
    void shouldHandleLoanWithoutAutoApproval() {
        var user = User.builder().id(UUID.randomUUID()).build();

        when(loanApplicationRepository.save(any())).thenReturn(Mono.just(loanApplication));
        when(loanTypeRepository.existsByCode(any())).thenReturn(Mono.just(true));
        when(transactionalGateway.execute(any())).thenAnswer(invocation -> {
            Supplier<Mono<?>> supplier = invocation.getArgument(0);
            return supplier.get();
        });

        StepVerifier.create(registerLoanApplicationUseCase.execute(
                        new RegisterLoanApplicationUseCaseInput(loanApplication, user, user)
                ))
                .expectNextMatches(la -> la.getId().equals(loanApplication.getId()))
                .verifyComplete();

        verify(loanApplicationRepository).save(any());
    }

    @Test
    void shouldHandleLoanWithAutoApproval() {
        var user = User.builder().id(UUID.randomUUID()).build();

        loanApplication.setLoanTypeCode(LoanTypes.PERSONAL_LOAN.getCode());

        when(loanApplicationRepository.save(any())).thenReturn(Mono.just(loanApplication));
        when(loanTypeRepository.existsByCode(any())).thenReturn(Mono.just(true));
        when(loanApplicationRepository.getTotalMonthlyDebtApprovedFromLoanApplicationById(any(), any()))
                .thenReturn(Mono.just(new BigDecimal("5000")));
        when(loanApplicationEventPublisher.publishValidation(any()))
                .thenReturn(Mono.empty());
        when(transactionalGateway.execute(any())).thenAnswer(invocation -> {
            Supplier<Mono<?>> supplier = invocation.getArgument(0);
            return supplier.get();
        });

        StepVerifier.create(registerLoanApplicationUseCase.execute(
                        new RegisterLoanApplicationUseCaseInput(loanApplication, user, user)
                ))
                .expectNextMatches(la -> la.getTotalMonthlyDebtApproved().compareTo(new BigDecimal("5000")) == 0)
                .verifyComplete();

        verify(loanApplicationRepository).getTotalMonthlyDebtApprovedFromLoanApplicationById(any(), any());
        verify(loanApplicationEventPublisher).publishValidation(any());
    }
}

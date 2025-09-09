package co.com.crediya.usecase.applyloanapplicationdecision;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.exceptions.CannotChangeLoanApplicationStatusException;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.status.enums.Statuses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplyLoanApplicationDecisionUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @InjectMocks
    private ApplyLoanApplicationDecisionUseCase useCase;

    private LoanApplication loanApplication;
    private UUID loanId;

    @BeforeEach
    void setUp() {
        loanId = UUID.randomUUID();
        loanApplication = LoanApplication.builder()
                .id(loanId)
                .statusCode(Statuses.PENDING.getCode())
                .build();
    }

    @Test
    void shouldApplyDecisionSuccessfully() {
        when(loanApplicationRepository.findById(loanId)).thenReturn(Mono.just(loanApplication));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.execute(new ApplyLoanApplicationDecisionUseCaseInput(loanId, Statuses.APPROVED.getCode())))
                .expectNextMatches(loan -> loan.getStatusCode().equals(Statuses.APPROVED.getCode()))
                .verifyComplete();

        verify(loanApplicationRepository).findById(loanId);
        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    void shouldFailIfCannotChangeStatus() {
        loanApplication.setStatusCode(Statuses.APPROVED.getCode());
        when(loanApplicationRepository.findById(loanId)).thenReturn(Mono.just(loanApplication));

        StepVerifier.create(useCase.execute(new ApplyLoanApplicationDecisionUseCaseInput(loanId, Statuses.REJECTED.getCode())))
                .expectError(CannotChangeLoanApplicationStatusException.class)
                .verify();

        verify(loanApplicationRepository).findById(loanId);
        verify(loanApplicationRepository, never()).save(any());
    }
}

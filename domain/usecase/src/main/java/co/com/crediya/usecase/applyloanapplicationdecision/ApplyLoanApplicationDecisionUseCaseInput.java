package co.com.crediya.usecase.applyloanapplicationdecision;

import java.util.UUID;

public record ApplyLoanApplicationDecisionUseCaseInput(
        UUID id,
        String decisionStatusCode
) {
}

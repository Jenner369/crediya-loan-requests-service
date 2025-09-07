package co.com.crediya.usecase.changeloanapplicationstatus;

import java.util.UUID;

public record ChangeLoanApplicationStatusUseCaseInput(
        UUID loanApplicationId,
        String newStatusCode
) {
}

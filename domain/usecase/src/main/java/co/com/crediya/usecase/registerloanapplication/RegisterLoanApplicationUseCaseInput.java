package co.com.crediya.usecase.registerloanapplication;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.user.User;

public record RegisterLoanApplicationUseCaseInput(
        LoanApplication loanApplication,
        User user,
        User authUser
) {
}

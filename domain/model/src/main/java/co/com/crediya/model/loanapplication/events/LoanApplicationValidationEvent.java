package co.com.crediya.model.loanapplication.events;

import co.com.crediya.model.loanapplication.LoanApplication;

import java.math.BigDecimal;

public record LoanApplicationValidationEvent(
        String id,
        BigDecimal amount,
        Integer term,
        String loanTypeName,
        BigDecimal loanTypeInterestRate,
        BigDecimal totalMonthlyDebtApproved,
        String userName,
        String userLastName,
        String userEmail,
        String userIdentityDocument,
        BigDecimal userBaseSalary
){
    public static LoanApplicationValidationEvent fromPendingLoanApplication(LoanApplication loanApplication) {
        return new LoanApplicationValidationEvent(
                loanApplication.getId().toString(),
                loanApplication.getAmount(),
                loanApplication.getTerm(),
                loanApplication.getLoanType().getName(),
                loanApplication.getLoanType().getInterestRate(),
                loanApplication.getTotalMonthlyDebtApproved(),
                loanApplication.getUser().getName(),
                loanApplication.getUser().getLastName(),
                loanApplication.getUser().getEmail(),
                loanApplication.getUser().getIdentityDocument(),
                loanApplication.getUser().getBaseSalary()
        );
    }
}

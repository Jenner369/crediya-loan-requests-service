package co.com.crediya.model.loanapplication.events;

import co.com.crediya.model.loanapplication.LoanApplication;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

public record LoanApplicationApprovedEvent(
        String id,
        BigDecimal amount,
        Date approvedAt
) {
    public static LoanApplicationApprovedEvent fromApprovedLoanApplication(LoanApplication loanApplication, Date approvedAt) {
        return new LoanApplicationApprovedEvent(
                loanApplication.getId().toString(),
                loanApplication.getAmount(),
                new Date(approvedAt.getTime())
        );
    }
}

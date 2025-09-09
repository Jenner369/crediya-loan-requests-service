package co.com.crediya.model.loanapplication.events;

import java.math.BigDecimal;

public record LoanApplicationChangedEvent(
        String loanRequestId,
        BigDecimal loanAmount,
        Integer loanTerm,
        String loanTypeName,
        String statusName,
        String userEmail,
        String userName,
        String userLastName) {
}

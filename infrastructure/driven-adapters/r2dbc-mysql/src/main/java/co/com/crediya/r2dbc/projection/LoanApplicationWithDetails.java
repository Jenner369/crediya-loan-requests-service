package co.com.crediya.r2dbc.projection;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.status.Status;

import java.math.BigDecimal;
import java.util.UUID;

public record LoanApplicationWithDetails(
        UUID id,
        BigDecimal amount,
        Integer term,
        String identityDocument,
        String email,
        String statusCode,
        String statusName,
        String statusDescription,
        String loanTypeCode,
        String loanTypeName,
        BigDecimal loanTypeInterestRate,
        BigDecimal totalMonthlyDebtApproved
) {
    public LoanApplication toModel() {
        return LoanApplication.builder()
                .id(id)
                .amount(amount)
                .term(term)
                .identityDocument(identityDocument)
                .email(email)
                .totalMonthlyDebtApproved(totalMonthlyDebtApproved)
                .statusCode(statusCode)
                .loanTypeCode(loanTypeCode)
                .status(toStatus())
                .loanType(toLoanType())
                .build();
    }

    public Status toStatus() {
        return Status.builder()
                .code(statusCode)
                .name(statusName)
                .description(statusDescription)
                .build();
    }

    public LoanType toLoanType() {
        return LoanType.builder()
                .code(loanTypeCode)
                .name(loanTypeName)
                .interestRate(loanTypeInterestRate)
                .build();
    }
}

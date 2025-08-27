package co.com.crediya.model.loantype.enums;

import co.com.crediya.model.loantype.LoanType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
public enum LoanTypes {
    PERSONAL_LOAN(
            UUID.fromString("95e99151-61dd-442b-abb3-6abca850b7aa"),
            "Préstamo personal",
            new BigDecimal("1000.00"),
            new BigDecimal("20000.00"),
            new BigDecimal("12.5"),
            true
    ),
    MORTGAGE_LOAN(
            UUID.fromString("9fc995e0-aa1c-4a93-ab42-52428f023aec"),
            "Préstamo hipotecario",
            new BigDecimal("20000.00"),
            new BigDecimal("300000.00"),
            new BigDecimal("7.0"),
            false
    ),
    CAR_LOAN(
            UUID.fromString("197a7bad-4c42-458e-909c-fbae6d6c6eb1"),
            "Préstamo vehicular",
            new BigDecimal("5000.00"),
            new BigDecimal("100000.00"),
            new BigDecimal("9.8"),
            true
    ),
    STUDENT_LOAN(
            UUID.fromString("f6516fd5-eca2-4db6-94a7-1f78c2ba040d"),
            "Préstamo estudiantil",
            new BigDecimal("1000.00"),
            new BigDecimal("50000.00"),
            new BigDecimal("5.5"),
            false
    ),
    BUSINESS_LOAN(
            UUID.fromString("3c9e078a-cddb-49e5-b4bb-300b7b664f9c"),
            "Préstamo empresarial",
            new BigDecimal("10000.00"),
            new BigDecimal("500000.00"),
            new BigDecimal("10.2"),
            false
    );

    private final UUID id;
    private final String name;
    private final BigDecimal minAmount;
    private final BigDecimal maxAmount;
    private final BigDecimal interestRate;
    private final Boolean autoApproval;

    public LoanType toModel() {
        return new LoanType(id, name, minAmount, maxAmount, interestRate, autoApproval);
    }
}

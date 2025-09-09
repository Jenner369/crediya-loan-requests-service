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
            "personal_loan",
            "Préstamo personal",
            new BigDecimal("1000.00"),
            new BigDecimal("20000.00"),
            new BigDecimal("0.13"),
            true
    ),
    MORTGAGE_LOAN(
            "mortgage_loan",
            "Préstamo hipotecario",
            new BigDecimal("20000.00"),
            new BigDecimal("300000.00"),
            new BigDecimal("0.7"),
            false
    ),
    CAR_LOAN(
            "car_loan",
            "Préstamo vehicular",
            new BigDecimal("5000.00"),
            new BigDecimal("100000.00"),
            new BigDecimal("0.98"),
            true
    ),
    STUDENT_LOAN(
            "student_loan",
            "Préstamo estudiantil",
            new BigDecimal("1000.00"),
            new BigDecimal("50000.00"),
            new BigDecimal("0.55"),
            false
    ),
    BUSINESS_LOAN(
            "business_loan",
            "Préstamo empresarial",
            new BigDecimal("10000.00"),
            new BigDecimal("500000.00"),
            new BigDecimal("0.15"),
            false
    );

    private final String code;
    private final String name;
    private final BigDecimal minAmount;
    private final BigDecimal maxAmount;
    private final BigDecimal interestRate;
    private final Boolean autoApproval;

    public LoanType toModel() {
        return new LoanType(code, name, minAmount, maxAmount, interestRate, autoApproval);
    }

    public static LoanType fromCode(String code) {
        for (LoanTypes loanType : values()) {
            if (loanType.getCode().equals(code)) {
                return loanType.toModel();
            }
        }

        return null;
    }
}

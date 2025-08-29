package co.com.crediya.api.dto.loanapplication;

import java.math.BigDecimal;

public record ShortLoanApplicationDTO(
        String id,
        BigDecimal amount,
        Integer term,
        String identityDocument
) {
}

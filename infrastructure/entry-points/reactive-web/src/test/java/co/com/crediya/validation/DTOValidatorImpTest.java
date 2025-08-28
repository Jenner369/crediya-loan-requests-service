package co.com.crediya.validation;

import co.com.crediya.api.dto.loanapplication.RegisterLoanApplicationDTO;
import co.com.crediya.api.validation.DTOValidatorImp;
import co.com.crediya.model.loantype.enums.LoanTypes;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

class DTOValidatorImpTest {

    private DTOValidatorImp dtoValidator;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        dtoValidator = new DTOValidatorImp(validator);
    }

    @Test
    void shouldPassValidationWhenValidDTO() {
        var dto = new RegisterLoanApplicationDTO(
                new BigDecimal("2000000"),
                12,
                LoanTypes.PERSONAL_LOAN.getCode(),
                "99999999"
        );

        StepVerifier.create(dtoValidator.validate(dto))
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void shouldFailValidationWhenInvalidDTO() {
        var dto = new RegisterLoanApplicationDTO(
                new BigDecimal("0"),
                10,
                LoanTypes.PERSONAL_LOAN.getCode(),
                "99999999"
        );

        StepVerifier.create(dtoValidator.validate(dto))
                .expectError(ConstraintViolationException.class)
                .verify();
    }
}


package co.com.crediya.model.loanapplication.exceptions;

import co.com.crediya.model.common.exceptions.DomainException;

public class InvalidStatusForLoanApplicationException extends DomainException {
    public InvalidStatusForLoanApplicationException() {
        super(
                "INVALID_STATUS_FOR_LOAN_APPLICATION",
                "El estado proporcionado no es válido para la solicitud de préstamo."
        );
    }
}

package co.com.crediya.model.loanapplication.exceptions;

import co.com.crediya.model.common.exceptions.DomainException;

public class CannotChangeLoanApplicationStatusException extends DomainException {
    public CannotChangeLoanApplicationStatusException() {
        super(
                "CANNOT_CHANGE_LOAN_APPLICATION_STATUS",
                "No se puede cambiar el estado de la solicitud de préstamo."
        );
    }
}

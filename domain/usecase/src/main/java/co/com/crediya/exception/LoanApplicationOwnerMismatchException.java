package co.com.crediya.exception;

import co.com.crediya.model.common.exceptions.DomainException;

public class LoanApplicationOwnerMismatchException extends DomainException {
    public LoanApplicationOwnerMismatchException() {
        super("LOAN_APPLICATION_OWNER_MISMATCH", "Solo puedes gestionar tus propias solicitudes de préstamo");
    }
}

package co.com.crediya.exception;

public class LoanApplicationNotFoundException extends NotFoundException {
    public LoanApplicationNotFoundException() {
        super("LOAN_APPLICATION_NOT_FOUND", "Solicitud de préstamo no encontrada");
    }
}

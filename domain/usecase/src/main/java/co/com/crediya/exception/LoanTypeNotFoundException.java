package co.com.crediya.exception;

public class LoanTypeNotFoundException extends NotFoundException {
    public LoanTypeNotFoundException() {
        super("LOAN_TYPE_NOT_FOUND", "Tipo de préstamo no encontrado");
    }
}
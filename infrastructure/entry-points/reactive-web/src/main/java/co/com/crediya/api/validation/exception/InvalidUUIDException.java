package co.com.crediya.api.validation.exception;

public class InvalidUUIDException extends ValidationException {
    public InvalidUUIDException(String id) {
        super("id", String.format("El id '%s' no es un UUID válido", id));
    }
}

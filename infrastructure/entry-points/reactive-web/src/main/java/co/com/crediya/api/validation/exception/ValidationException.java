package co.com.crediya.api.validation.exception;

import lombok.Getter;

@Getter
public abstract class ValidationException extends RuntimeException {
    private final String field;

    protected ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

}
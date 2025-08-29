package co.com.crediya.model.common.exceptions;

import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException {

    private final String code;

    protected DomainException(String code, String message) {
        super(message);
        this.code = code;
    }
}

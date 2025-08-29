package co.com.crediya.api.validation;

import co.com.crediya.api.presentation.contract.DTOValidator;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DTOValidatorImp implements DTOValidator {
    private final Validator validator;

    public <T> Mono<T> validate(T dto) {
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            return Mono.error(new ConstraintViolationException(violations));
        }

        return Mono.just(dto);
    }
}
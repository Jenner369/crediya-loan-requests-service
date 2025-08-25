package co.com.crediya.api.validation;

import co.com.crediya.api.presentation.contract.UUIDValidator;
import co.com.crediya.api.validation.exception.InvalidUUIDException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class UUIDValidatorImp implements UUIDValidator {
    @Override
    public Mono<UUID> validate(String id) {
        try {
            return Mono.just(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            return Mono.error(new InvalidUUIDException(id));
        }
    }
}

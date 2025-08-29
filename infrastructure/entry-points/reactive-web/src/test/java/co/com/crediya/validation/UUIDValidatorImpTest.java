package co.com.crediya.validation;
import co.com.crediya.api.validation.UUIDValidatorImp;
import co.com.crediya.api.validation.exception.InvalidUUIDException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.UUID;

class UUIDValidatorImpTest {

    private UUIDValidatorImp uuidValidator;

    @BeforeEach
    void setUp() {
        uuidValidator = new UUIDValidatorImp();
    }

    @Test
    void shouldValidateValidUUID() {
        String id = UUID.randomUUID().toString();

        StepVerifier.create(uuidValidator.validate(id))
                .expectNext(UUID.fromString(id))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenInvalidUUID() {
        String invalidId = "not-a-uuid";

        StepVerifier.create(uuidValidator.validate(invalidId))
                .expectError(InvalidUUIDException.class)
                .verify();
    }

    @Test
    void shouldPassValidateExistsWithValidUUID() {
        String id = UUID.randomUUID().toString();

        StepVerifier.create(uuidValidator.validateExists(id))
                .verifyComplete();
    }

    @Test
    void shouldFailValidateExistsWithInvalidUUID() {
        String invalidId = "12345";

        StepVerifier.create(uuidValidator.validateExists(invalidId))
                .expectError(InvalidUUIDException.class)
                .verify();
    }
}

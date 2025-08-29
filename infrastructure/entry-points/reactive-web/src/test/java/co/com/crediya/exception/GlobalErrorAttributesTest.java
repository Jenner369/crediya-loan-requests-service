package co.com.crediya.exception;

import co.com.crediya.api.exception.GlobalErrorAttributes;
import co.com.crediya.api.validation.exception.InvalidUUIDException;
import co.com.crediya.model.common.exceptions.DomainException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalErrorAttributesTest {

    private GlobalErrorAttributes globalErrorAttributes;

    @BeforeEach
    void setUp() {
        globalErrorAttributes = new GlobalErrorAttributes();
    }

    private ServerRequest buildRequestWithError(Throwable ex) {
        ServerHttpRequest httpRequest = mock(ServerHttpRequest.class);
        when(httpRequest.getId()).thenReturn("test-id");
        when(httpRequest.getPath()).thenReturn(null);

        ServerRequest request = mock(ServerRequest.class);
        when(request.exchange()).thenReturn(mock(org.springframework.web.server.ServerWebExchange.class));
        when(request.exchange().getRequest()).thenReturn(httpRequest);
        when(request.method()).thenReturn(HttpMethod.GET);
        when(request.path()).thenReturn("/test");

        when(request.attribute("org.springframework.boot.web.reactive.error.DefaultErrorAttributes.ERROR"))
                .thenReturn(Optional.of(ex));
        return request;
    }

    @Test
    void shouldReturnNotFoundError() {
        var request = buildRequestWithError(new UserNotFoundException());
        Map<String, Object> attrs = globalErrorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());

        assertThat(attrs).containsEntry("status", 404);
    }

    @Test
    void shouldReturnDomainExceptionError() {
        var domainEx = new TestDomainException("Algo salió mal");
        var request = buildRequestWithError(domainEx);

        var attrs = globalErrorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());

        assertThat(attrs).containsEntry("status", 400);
    }

    @Test
    void shouldReturnValidationExceptionError() {
        var request = buildRequestWithError(new InvalidUUIDException("ID inválido"));
        var attrs = globalErrorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());

        assertThat(attrs).containsEntry("status", 422);
    }

    @Test
    void shouldReturnConstraintViolationError() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Debe ser mayor a 0");
        var ex = new ConstraintViolationException(Set.of(violation));

        var request = buildRequestWithError(ex);
        var attrs = globalErrorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());

        assertThat(attrs).containsEntry("status", 422);
    }

    @Test
    void shouldReturnInternalServerErrorForUnknownException() {
        var request = buildRequestWithError(new RuntimeException("Falló algo"));
        var attrs = globalErrorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());

        assertThat(attrs).containsEntry("status", 500);
    }

    static class TestDomainException extends DomainException {
        public TestDomainException(String message) {
            super("TEST_CODE", message);
        }
    }
}

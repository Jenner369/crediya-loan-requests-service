package co.com.crediya.api.exception;

import co.com.crediya.api.dto.common.ErrorResponseDTO;
import co.com.crediya.api.validation.exception.ValidationException;
import co.com.crediya.exception.NotFoundException;
import co.com.crediya.model.common.exceptions.DomainException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        var status = getHttpStatusByException(error);

        var errorDTO = new ErrorResponseDTO(
                new Date(),
                request.path(),
                status.value(),
                status.getReasonPhrase(),
                request.exchange().getRequest().getId(),
                getErrorMessageByException(error, request)
        );

        return errorDTO.toMap();
    }

    private HttpStatus getHttpStatusByException(Throwable error) {
        if (error instanceof NotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (error instanceof DomainException) {
            return HttpStatus.BAD_REQUEST;
        } else if (error instanceof ConstraintViolationException) {
            return HttpStatus.UNPROCESSABLE_ENTITY;
        } else if (error instanceof ValidationException) {
            return HttpStatus.UNPROCESSABLE_ENTITY;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private String getErrorMessageByException(Throwable error, ServerRequest request) {

        String errorMessage = "Un error inesperado ha ocurrido";

        if (error instanceof ConstraintViolationException cve) {
            errorMessage = cve.getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .findFirst()
                    .orElse("Validación fallida");

            log.warn("[{}] ConstraintViolationException {} {}: {}",
                    request.exchange().getRequest().getId(),
                    request.method().name(),
                    request.path(),
                    errorMessage
            );
        } else if (error instanceof DomainException de) {
            errorMessage = de.getMessage();

            log.warn("[{}] {} with code {} {} {}: {}",
                    request.exchange().getRequest().getId(),
                    de.getClass().getSimpleName(),
                    de.getCode(),
                    request.method().name(),
                    request.path(),
                    de.getMessage()
            );
        } else if (error instanceof ValidationException ve) {
            errorMessage = ve.getMessage();

            log.warn("[{}] {} on field '{}' {} {}: {}",
                    request.exchange().getRequest().getId(),
                    ve.getClass().getSimpleName(),
                    ve.getField(),
                    request.method().name(),
                    request.path(),
                    ve.getMessage()
            );
        } else {
            log.error("[{}] Excepción no controlada {} {}: {}",
                    request.exchange().getRequest().getId(),
                    request.method().name(),
                    request.path(),
                    error.getMessage(),
                    error
            );
        }

        return errorMessage;
    }
}

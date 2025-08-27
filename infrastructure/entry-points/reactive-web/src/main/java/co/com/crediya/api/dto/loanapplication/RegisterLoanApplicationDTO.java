package co.com.crediya.api.dto.loanapplication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Datos necesarios para registrar una solicitud de préstamo")
public record RegisterLoanApplicationDTO(
        @Schema(description = "Monto solicitado para el préstamo", example = "5000.00")
        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor a 0")
        BigDecimal amount,

        @Schema(description = "Plazo del préstamo en meses", example = "12")
        @NotNull(message = "El plazo es obligatorio")
        @Positive(message = "El plazo debe ser mayor a 0")
        Integer term,

        @Schema(description = "ID del tipo de préstamo", example = "a3f1c9e2-5d6b-4e8f-9c3a-2b1e4d5f6a7b")
        @NotNull(message = "El ID del tipo de préstamo es obligatorio")
        String loanTypeId,

        @Schema(description = "Documento de identidad del solicitante", example = "123456789")
        @NotNull(message = "El documento de identidad es obligatorio")
        String identityDocument
) {
}

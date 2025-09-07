package co.com.crediya.api.dto.loanapplication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos necesarios para cambiar el estado de una solicitud de préstamo")
public record ChangeLoanApplicationStatusDTO(
        @Schema(description = "ID de la solicitud de préstamo", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "El ID de la solicitud de préstamo es obligatorio")
        String loanApplicationId,

        @Schema(description = "Nuevo código de estado para la solicitud de préstamo", example = "approved")
        @NotNull(message = "El nuevo código de estado es obligatorio")
        String newStatusCode
) {
}

package co.com.crediya.api.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Schema(description = "Estructura estándar para las respuestas de error")
public record ErrorResponseDTO(
        @Schema(description = "Momento en que ocurrió el error", example = "2025-08-24T15:57:19.192Z")
        Date timestamp,

        @Schema(description = "Path del request", example = "/api/v1/usuarios")
        String path,

        @Schema(description = "Código HTTP", example = "400")
        int status,

        @Schema(description = "Nombre del error HTTP", example = "Bad Request")
        String error,

        @Schema(description = "Id del request para trazabilidad", example = "4f38808d-1")
        String requestId,

        @Schema(description = "Mensaje legible del error", example = "El salario debe estar entre 0 y 15000000")
        String message
) {
    public Map<String, Object> toMap() {
        return Map.of(
                "timestamp", timestamp,
                "path", path,
                "status", status,
                "error", error,
                "requestId", requestId,
                "message", message
        );
    }

    public static ErrorResponseDTO fromMap(Map<String, Object> map) {
        return new ErrorResponseDTO(
                Date.from(Instant.parse((String) map.get("timestamp"))),
                (String) map.get("path"),
                (Integer) map.get("status"),
                (String) map.get("error"),
                (String) map.get("requestId"),
                (String) map.get("message")
        );
    }
}

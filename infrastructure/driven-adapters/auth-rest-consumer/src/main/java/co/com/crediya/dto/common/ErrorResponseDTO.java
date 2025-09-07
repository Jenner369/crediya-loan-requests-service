package co.com.crediya.dto.common;

import java.util.Date;

public record ErrorResponseDTO(
        Date timestamp,
        String path,
        int status,
        String error,
        String requestId,
        String message
) {
}

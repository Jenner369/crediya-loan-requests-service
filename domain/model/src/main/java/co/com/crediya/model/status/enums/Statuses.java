package co.com.crediya.model.status.enums;

import co.com.crediya.model.status.Status;
import lombok.Getter;

@Getter
public enum Statuses {
    PENDING(
            "pending",
            "Pendiente de Revisión",
            "Solicitud en espera de ser revisada por un asesor"
    ),
    APPROVED(
            "approved",
            "Aprobado",
            "Cliente aprobado para el préstamo"
    ),
    REJECTED(
            "rejected",
            "Rechazado",
            "Cliente no aprobado para el préstamo"
    ),
    MANUAL_REVIEW(
            "manual_review",
            "Revisión Manual",
            "Solicitud que requiere revisión manual adicional"
    );

    private final String code;
    private final String name;
    private final String description;

    Statuses(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public Status toModel() {
        return Status.builder()
                .code(this.code)
                .name(this.name)
                .description(this.description)
                .build();
    }

    public static Status fromCode(String code) {
        for (Statuses status : Statuses.values()) {
            if (status.getCode().equals(code)) {
                return status.toModel();
            }
        }

        return null;
    }

    public static Boolean isApproved(String statusCode) {
        return APPROVED.getCode().equals(statusCode);
    }

    public static Boolean isRejected(String statusCode) {
        return REJECTED.getCode().equals(statusCode);
    }

    public static Boolean isManualReview(String statusCode) {
        return MANUAL_REVIEW.getCode().equals(statusCode);
    }
}

package co.com.crediya.model.status.enums;

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
    );

    private final String code;
    private final String name;
    private final String description;

    Statuses(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}

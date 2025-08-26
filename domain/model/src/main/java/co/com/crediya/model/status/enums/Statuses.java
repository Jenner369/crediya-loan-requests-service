package co.com.crediya.model.status.enums;

import lombok.Getter;

import java.util.UUID;

@Getter
public enum Statuses {
    PENDING(
            UUID.fromString("828ca174-c4a7-4850-b759-996219edef6c"),
            "Pendiente de Revisión",
            "Solicitud en espera de ser revisada por un asesor"
    ),
    APPROVED(
            UUID.fromString("0a00524c-b32e-47e3-94d1-615461984428"),
            "Aprobado",
            "Cliente aprobado para el préstamo"
    ),
    REJECTED(
            UUID.fromString("44468889-fac0-424f-abf6-f52424aaad09"),
            "Rechazado",
            "Cliente no aprobado para el préstamo"
    );

    private final UUID id;
    private final String name;
    private final String description;

    Statuses(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}

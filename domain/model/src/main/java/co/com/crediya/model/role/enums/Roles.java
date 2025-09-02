package co.com.crediya.model.role.enums;

import lombok.Getter;

import java.util.UUID;

@Getter
public enum Roles {
    ADMIN(UUID.fromString("d4e5f6a7-8901-23bc-def4-5678901234cd"), "Administrador", "Acceso total"),
    ADVISOR(UUID.fromString("c1a2f3e4-5678-90ab-cdef-1234567890ab"), "Asesor", "Gestiona usuarios"),
    CLIENT(UUID.fromString("e7f8a9b0-1234-56cd-ef78-9012345678ef"),"Cliente", "Acceso limitado");

    private final UUID id;
    private final String name;
    private final String description;

    Roles(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}

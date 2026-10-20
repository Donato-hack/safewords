package com.safewords.api.dto;

import java.time.LocalDateTime;

public record ModeracionPublicacionResponse(
        Long id,
        String titulo,
        String contenido,
        String categoria,
        String autorVisible,
        Long usuarioId,
        String nombreUsuario,
        String nombreCompleto,
        String correo,
        Boolean anonima,
        String estado,
        LocalDateTime fechaCreacion
) {
}

package com.safewords.api.dto;

import java.time.LocalDateTime;

public record ModeracionComentarioResponse(
        Long id,
        Long publicacionId,
        String publicacionTitulo,
        String contenido,
        String autorVisible,
        Long usuarioId,
        String nombreUsuario,
        String nombreCompleto,
        String correo,
        Boolean anonimo,
        String estado,
        LocalDateTime fechaCreacion
) {
}

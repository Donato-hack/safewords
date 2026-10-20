package com.safewords.api.dto;

import java.time.LocalDateTime;

public record ModeracionReporteResponse(
        Long id,
        String tipoObjetivo,
        Long publicacionId,
        Long comentarioId,
        String publicacionTitulo,
        String contenido,
        String autorVisible,
        Long usuarioId,
        String nombreUsuario,
        String nombreCompleto,
        String correo,
        String motivo,
        String descripcion,
        String estado,
        LocalDateTime fechaReporte
) {
}

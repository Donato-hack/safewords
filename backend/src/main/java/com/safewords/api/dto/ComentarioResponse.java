package com.safewords.api.dto;

import java.time.LocalDateTime;

public record ComentarioResponse(
        Long id,
        String contenido,
        String autor,
        Boolean anonimo,
        String estado,
        LocalDateTime fechaCreacion
) {
}
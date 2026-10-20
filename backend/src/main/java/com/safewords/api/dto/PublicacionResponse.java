package com.safewords.api.dto;

import java.time.LocalDateTime;

public record PublicacionResponse(
        Long id,
        String titulo,
        String contenido,
        String categoria,
        String autor,
        Boolean anonima,
        LocalDateTime fechaCreacion
) {
}
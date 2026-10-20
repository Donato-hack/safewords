package com.safewords.api.modelo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.safewords.api.modelo.Comentario;

public interface ComentarioRepository
        extends JpaRepository<Comentario, Long> {

    List<Comentario>
    findByPublicacionIdAndEstadoOrderByFechaCreacionAsc(
            Long publicacionId,
            String estado
    );

    List<Comentario>
    findByEstadoOrderByFechaCreacionAsc(
            String estado
    );
}

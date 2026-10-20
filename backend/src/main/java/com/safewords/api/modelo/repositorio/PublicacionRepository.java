package com.safewords.api.modelo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.safewords.api.modelo.Publicacion;

public interface PublicacionRepository
        extends JpaRepository<Publicacion, Long> {

    List<Publicacion> findByEstadoOrderByFechaCreacionDesc(
            String estado
    );

    List<Publicacion> findByCategoriaIdAndEstadoOrderByFechaCreacionDesc(
            Long categoriaId,
            String estado
    );

    List<Publicacion> findByTituloContainingIgnoreCaseOrContenidoContainingIgnoreCase(
            String titulo,
            String contenido
    );
}

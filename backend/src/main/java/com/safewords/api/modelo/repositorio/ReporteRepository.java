package com.safewords.api.modelo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.safewords.api.modelo.Reporte;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByEstadoOrderByFechaReporteDesc(String estado);
}

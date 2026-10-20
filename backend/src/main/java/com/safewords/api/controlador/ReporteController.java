package com.safewords.api.controlador;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.safewords.api.dto.ReporteRequest;
import com.safewords.api.modelo.Reporte;
import com.safewords.api.servicios.ReporteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "http://localhost:4200")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @PostMapping("/publicaciones/{id}")
    public ResponseEntity<?> reportarPublicacion(
            @PathVariable Long id,
            @Valid @RequestBody ReporteRequest request,
            Authentication authentication) {
        try {
            Reporte reporte = reporteService.reportarPublicacion(id, request, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "mensaje", "Reporte enviado para revisión",
                    "id", reporte.getId(),
                    "estado", reporte.getEstado()
            ));
        } catch (RuntimeException error) {
            return ResponseEntity.badRequest().body(Map.of("error", error.getMessage()));
        }
    }

    @PostMapping("/comentarios/{id}")
    public ResponseEntity<?> reportarComentario(
            @PathVariable Long id,
            @Valid @RequestBody ReporteRequest request,
            Authentication authentication) {
        try {
            Reporte reporte = reporteService.reportarComentario(id, request, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "mensaje", "Reporte enviado para revisión",
                    "id", reporte.getId(),
                    "estado", reporte.getEstado()
            ));
        } catch (RuntimeException error) {
            return ResponseEntity.badRequest().body(Map.of("error", error.getMessage()));
        }
    }
}

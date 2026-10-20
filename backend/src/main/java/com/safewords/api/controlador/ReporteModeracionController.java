package com.safewords.api.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.safewords.api.dto.ModeracionReporteResponse;
import com.safewords.api.dto.ReporteModeracionRequest;
import com.safewords.api.modelo.Reporte;
import com.safewords.api.servicios.ReporteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/reportes")
@CrossOrigin(origins = "http://localhost:4200")
public class ReporteModeracionController {

    private final ReporteService reporteService;

    public ReporteModeracionController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<ModeracionReporteResponse>> listarPendientes() {
        return ResponseEntity.ok(reporteService.listarPendientes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> moderar(
            @PathVariable Long id,
            @Valid @RequestBody ReporteModeracionRequest request,
            Authentication authentication) {
        try {
            Reporte reporte = reporteService.moderar(id, request, authentication.getName());
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Reporte actualizado correctamente",
                    "id", reporte.getId(),
                    "estado", reporte.getEstado()
            ));
        } catch (RuntimeException error) {
            return ResponseEntity.badRequest().body(Map.of("error", error.getMessage()));
        }
    }
}

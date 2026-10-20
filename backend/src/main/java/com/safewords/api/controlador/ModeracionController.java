package com.safewords.api.controlador;

import java.util.Map;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.safewords.api.dto.ModeracionRequest;
import com.safewords.api.dto.ModeracionComentarioResponse;
import com.safewords.api.dto.ModeracionPublicacionResponse;
import com.safewords.api.modelo.Comentario;
import com.safewords.api.modelo.Publicacion;
import com.safewords.api.servicios.ModeracionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class ModeracionController {

    private final ModeracionService moderacionService;

    public ModeracionController(
            ModeracionService moderacionService) {
        this.moderacionService = moderacionService;
    }

    @PutMapping("/publicaciones/{id}/moderacion")
    public ResponseEntity<?> moderarPublicacion(
            @PathVariable Long id,
            @Valid @RequestBody ModeracionRequest request,
            Authentication authentication) {

        try {
            Publicacion publicacion =
                    moderacionService.moderarPublicacion(
                            id,
                            request,
                            authentication.getName()
                    );

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "Publicación moderada correctamente",
                            "id",
                            publicacion.getId(),
                            "estado",
                            publicacion.getEstado()
                    )
            );

        } catch (RuntimeException error) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", error.getMessage()));
        }
    }

    @GetMapping("/publicaciones/pendientes")
    public ResponseEntity<List<ModeracionPublicacionResponse>> listarPublicacionesPendientes() {
        return ResponseEntity.ok(moderacionService.listarPublicacionesPendientes());
    }

    @GetMapping("/comentarios/pendientes")
    public ResponseEntity<List<ModeracionComentarioResponse>> listarComentariosPendientes() {
        return ResponseEntity.ok(moderacionService.listarComentariosPendientes());
    }

    @PutMapping("/comentarios/{id}/moderacion")
    public ResponseEntity<?> moderarComentario(
            @PathVariable Long id,
            @Valid @RequestBody ModeracionRequest request,
            Authentication authentication) {

        try {
            Comentario comentario =
                    moderacionService.moderarComentario(
                            id,
                            request,
                            authentication.getName()
                    );

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "Comentario moderado correctamente",
                            "id",
                            comentario.getId(),
                            "estado",
                            comentario.getEstado()
                    )
            );

        } catch (RuntimeException error) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", error.getMessage()));
        }
    }
}

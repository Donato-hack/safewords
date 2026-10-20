package com.safewords.api.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.safewords.api.dto.ComentarioRequest;
import com.safewords.api.dto.ComentarioResponse;
import com.safewords.api.modelo.Comentario;
import com.safewords.api.servicios.ComentarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/publicaciones/{publicacionId}/comentarios")
@CrossOrigin(origins = "http://localhost:4200")
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(
            ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @GetMapping
    public ResponseEntity<List<ComentarioResponse>>
            listarComentarios(
                    @PathVariable Long publicacionId) {

        return ResponseEntity.ok(
                comentarioService.listarComentarios(publicacionId)
        );
    }

    @PostMapping
    public ResponseEntity<?> crearComentario(
            @PathVariable Long publicacionId,
            @Valid @RequestBody ComentarioRequest request,
            Authentication authentication) {

        try {
            String correoUsuario = authentication.getName();

            Comentario comentario =
                    comentarioService.crearComentario(
                            publicacionId,
                            request,
                            correoUsuario
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensaje",
                            "Comentario enviado para moderación",
                            "id",
                            comentario.getId(),
                            "estado",
                            comentario.getEstado()
                    ));

        } catch (RuntimeException error) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            error.getMessage()
                    ));
        }
    }
}

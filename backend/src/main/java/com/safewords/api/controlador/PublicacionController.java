package com.safewords.api.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.safewords.api.dto.PublicacionRequest;
import com.safewords.api.dto.PublicacionResponse;
import com.safewords.api.modelo.Publicacion;
import com.safewords.api.servicios.PublicacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/publicaciones")
@CrossOrigin(origins = "http://localhost:4200")
public class PublicacionController {

    private final PublicacionService publicacionService;

    public PublicacionController(
            PublicacionService publicacionService) {
        this.publicacionService = publicacionService;
    }

    @GetMapping
    public ResponseEntity<List<PublicacionResponse>>
            listarPublicaciones() {

        return ResponseEntity.ok(
                publicacionService.listarPublicadas()
        );
    }

    @PostMapping
    public ResponseEntity<?> crearPublicacion(
            @Valid @RequestBody PublicacionRequest request,
            Authentication authentication) {

        try {
            String correoUsuario = authentication.getName();

            Publicacion publicacion =
                    publicacionService.crearPublicacion(
                            request,
                            correoUsuario
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensaje",
                            "Publicación enviada para moderación",
                            "id",
                            publicacion.getId(),
                            "estado",
                            publicacion.getEstado()
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

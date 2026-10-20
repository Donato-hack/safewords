package com.safewords.api.controlador;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safewords.api.dto.LoginRequest;
import com.safewords.api.dto.RegistroRequest;
import com.safewords.api.modelo.Usuario;
import com.safewords.api.servicios.JwtService;
import com.safewords.api.servicios.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthController(
            UsuarioService usuarioService,
            JwtService jwtService) {

        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(
            @Valid @RequestBody RegistroRequest request) {

        try {
            UsuarioService.RegistroResultado resultado = usuarioService.registrarUsuario(
                    request.getNombreCompleto(),
                    request.getNombreUsuario(),
                    request.getCorreo(),
                    request.getPassword()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensaje",
                            resultado.correoEnviado()
                                    ? "Usuario registrado correctamente. Revisa tu correo."
                                    : "Usuario registrado correctamente. El correo de bienvenida no está configurado.",
                            "correoEnviado",
                            resultado.correoEnviado()
                    ));

        } catch (RuntimeException error) {
            HttpStatus estado = error.getMessage() != null
                    && (error.getMessage().contains("ya está registrado")
                        || error.getMessage().contains("ya existe"))
                    ? HttpStatus.CONFLICT
                    : HttpStatus.BAD_REQUEST;

            return ResponseEntity
                    .status(estado)
                    .body(Map.of(
                            "error",
                            error.getMessage()
                    ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(
            @Valid @RequestBody LoginRequest request) {

        try {
            Usuario usuario = usuarioService.iniciarSesion(
                    request.getCorreo(),
                    request.getPassword()
            );

            String token = jwtService.generarToken(usuario);

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje", "Inicio de sesión correcto",
                            "token", token,
                            "usuario", usuario.getNombreUsuario(),
                            "rol", usuario.getRol()
                    )
            );

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

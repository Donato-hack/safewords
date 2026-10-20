package com.safewords.api.servicios;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.safewords.api.modelo.Usuario;
import com.safewords.api.modelo.repositorio.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CorreoService correoService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            CorreoService correoService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.correoService = correoService;
    }

    public record RegistroResultado(Usuario usuario, boolean correoEnviado) {
    }

    public RegistroResultado registrarUsuario(
            String nombreCompleto,
            String nombreUsuario,
            String correo,
            String password) {

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new RuntimeException("El correo ya está registrado");
        }

        if (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        Usuario usuario = new Usuario();

        usuario.setNombreCompleto(nombreCompleto);
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setCorreo(correo);

        // La contraseña queda cifrada antes de guardarse
        usuario.setPasswordHash(passwordEncoder.encode(password));

        usuario.setRol("USER");
        usuario.setEstado("ACTIVE");

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        boolean correoEnviado = correoService.enviarBienvenida(usuarioGuardado);

        return new RegistroResultado(usuarioGuardado, correoEnviado);
    }
    
    public Usuario iniciarSesion(String correo, String password) {

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() ->
                    new RuntimeException("Correo o contraseña incorrectos")
                );

        boolean passwordCorrecta = passwordEncoder.matches(
                password,
                usuario.getPasswordHash()
        );

        if (!passwordCorrecta) {
            throw new RuntimeException("Correo o contraseña incorrectos");
        }

        if (!usuario.getEstado().equals("ACTIVE")) {
            throw new RuntimeException("El usuario no está activo");
        }

        return usuario;
    }
}

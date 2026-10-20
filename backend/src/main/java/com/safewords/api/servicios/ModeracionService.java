package com.safewords.api.servicios;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safewords.api.dto.ModeracionRequest;
import com.safewords.api.dto.ModeracionComentarioResponse;
import com.safewords.api.dto.ModeracionPublicacionResponse;
import com.safewords.api.modelo.Comentario;
import com.safewords.api.modelo.Publicacion;
import com.safewords.api.modelo.Usuario;
import com.safewords.api.modelo.repositorio.ComentarioRepository;
import com.safewords.api.modelo.repositorio.PublicacionRepository;
import com.safewords.api.modelo.repositorio.UsuarioRepository;

@Service
public class ModeracionService {

    private final PublicacionRepository publicacionRepository;
    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;

    public ModeracionService(
            PublicacionRepository publicacionRepository,
            ComentarioRepository comentarioRepository,
            UsuarioRepository usuarioRepository) {

        this.publicacionRepository = publicacionRepository;
        this.comentarioRepository = comentarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Publicacion moderarPublicacion(
            Long id,
            ModeracionRequest request,
            String correoModerador) {

        validarEstado(request.getEstado());

        Publicacion publicacion = publicacionRepository
                .findById(id)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Publicación no encontrada"
                    )
                );

        Usuario moderador = buscarModerador(correoModerador);

        publicacion.setEstado(request.getEstado());
        publicacion.setModeradoPor(moderador);
        publicacion.setFechaModeracion(LocalDateTime.now());
        publicacion.setNotaModeracion(request.getNota());

        return publicacionRepository.save(publicacion);
    }

    @Transactional
    public Comentario moderarComentario(
            Long id,
            ModeracionRequest request,
            String correoModerador) {

        validarEstado(request.getEstado());

        Comentario comentario = comentarioRepository
                .findById(id)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Comentario no encontrado"
                    )
                );

        Usuario moderador = buscarModerador(correoModerador);

        comentario.setEstado(request.getEstado());
        comentario.setModeradoPor(moderador);
        comentario.setFechaModeracion(LocalDateTime.now());
        comentario.setNotaModeracion(request.getNota());

        return comentarioRepository.save(comentario);
    }

    @Transactional(readOnly = true)
    public java.util.List<ModeracionPublicacionResponse> listarPublicacionesPendientes() {
        return publicacionRepository
                .findByEstadoOrderByFechaCreacionDesc("PENDING")
                .stream()
                .map(publicacion -> new ModeracionPublicacionResponse(
                        publicacion.getId(),
                        publicacion.getTitulo(),
                        publicacion.getContenido(),
                        publicacion.getCategoria().getNombre(),
                        publicacion.getEsAnonima() ? "Usuario anónimo" : publicacion.getUsuario().getNombreUsuario(),
                        publicacion.getUsuario().getId(),
                        publicacion.getUsuario().getNombreUsuario(),
                        publicacion.getUsuario().getNombreCompleto(),
                        publicacion.getUsuario().getCorreo(),
                        publicacion.getEsAnonima(),
                        publicacion.getEstado(),
                        publicacion.getFechaCreacion()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public java.util.List<ModeracionComentarioResponse> listarComentariosPendientes() {
        return comentarioRepository
                .findByEstadoOrderByFechaCreacionAsc("PENDING")
                .stream()
                .map(comentario -> new ModeracionComentarioResponse(
                        comentario.getId(),
                        comentario.getPublicacion().getId(),
                        comentario.getPublicacion().getTitulo(),
                        comentario.getContenido(),
                        comentario.getEsAnonimo() ? "Usuario anónimo" : comentario.getUsuario().getNombreUsuario(),
                        comentario.getUsuario().getId(),
                        comentario.getUsuario().getNombreUsuario(),
                        comentario.getUsuario().getNombreCompleto(),
                        comentario.getUsuario().getCorreo(),
                        comentario.getEsAnonimo(),
                        comentario.getEstado(),
                        comentario.getFechaCreacion()
                ))
                .toList();
    }

    private Usuario buscarModerador(String correo) {
        return usuarioRepository
                .findByCorreo(correo)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Moderador no encontrado"
                    )
                );
    }

    private void validarEstado(String estado) {
        if (!estado.equals("PUBLISHED")
                && !estado.equals("HIDDEN")) {

            throw new RuntimeException(
                    "El estado debe ser PUBLISHED o HIDDEN"
            );
        }
    }
}

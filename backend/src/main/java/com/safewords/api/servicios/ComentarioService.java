package com.safewords.api.servicios;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safewords.api.dto.ComentarioRequest;
import com.safewords.api.dto.ComentarioResponse;
import com.safewords.api.modelo.Comentario;
import com.safewords.api.modelo.Publicacion;
import com.safewords.api.modelo.Usuario;
import com.safewords.api.modelo.repositorio.ComentarioRepository;
import com.safewords.api.modelo.repositorio.PublicacionRepository;
import com.safewords.api.modelo.repositorio.UsuarioRepository;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;

    public ComentarioService(
            ComentarioRepository comentarioRepository,
            PublicacionRepository publicacionRepository,
            UsuarioRepository usuarioRepository) {

        this.comentarioRepository = comentarioRepository;
        this.publicacionRepository = publicacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Comentario crearComentario(
            Long publicacionId,
            ComentarioRequest request,
            String correoUsuario) {

        Publicacion publicacion = publicacionRepository
                .findById(publicacionId)
                .orElseThrow(() ->
                    new RuntimeException("Publicación no encontrada")
                );

        if (!publicacion.getEstado().equals("PUBLISHED")) {
            throw new RuntimeException(
                    "La publicación todavía no está disponible"
            );
        }

        Usuario usuario = usuarioRepository
                .findByCorreo(correoUsuario)
                .orElseThrow(() ->
                    new RuntimeException("Usuario no encontrado")
                );

        Comentario comentario = new Comentario();

        comentario.setPublicacion(publicacion);
        comentario.setUsuario(usuario);
        comentario.setContenido(request.getContenido());

        Boolean anonimo = request.getEsAnonimo() == null
                ? true
                : request.getEsAnonimo();

        comentario.setEsAnonimo(anonimo);
        comentario.setEstado("PENDING");

        if (request.getComentarioPadreId() != null) {
            Comentario padre = comentarioRepository
                    .findById(request.getComentarioPadreId())
                    .orElseThrow(() ->
                        new RuntimeException(
                            "Comentario padre no encontrado"
                        )
                    );

            comentario.setComentarioPadre(padre);
        }

        return comentarioRepository.save(comentario);
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponse> listarComentarios(
            Long publicacionId) {

        return comentarioRepository
                .findByPublicacionIdAndEstadoOrderByFechaCreacionAsc(
                        publicacionId,
                        "PUBLISHED"
                )
                .stream()
                .map(comentario -> {

                    String autor = comentario.getEsAnonimo()
                            ? "Usuario anónimo"
                            : comentario.getUsuario()
                                .getNombreUsuario();

                    return new ComentarioResponse(
                            comentario.getId(),
                            comentario.getContenido(),
                            autor,
                            comentario.getEsAnonimo(),
                            comentario.getEstado(),
                            comentario.getFechaCreacion()
                    );
                })
                .toList();
    }
}

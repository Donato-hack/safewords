package com.safewords.api.servicios;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safewords.api.dto.PublicacionRequest;
import com.safewords.api.dto.PublicacionResponse;
import com.safewords.api.modelo.Categoria;
import com.safewords.api.modelo.Publicacion;
import com.safewords.api.modelo.Usuario;
import com.safewords.api.modelo.repositorio.CategoriaRepository;
import com.safewords.api.modelo.repositorio.PublicacionRepository;
import com.safewords.api.modelo.repositorio.UsuarioRepository;

@Service
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public PublicacionService(
            PublicacionRepository publicacionRepository,
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository) {

        this.publicacionRepository = publicacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Publicacion crearPublicacion(
            PublicacionRequest request,
            String correoUsuario) {

        Usuario usuario = usuarioRepository
                .findByCorreo(correoUsuario)
                .orElseThrow(() ->
                    new RuntimeException("Usuario no encontrado")
                );

        Categoria categoria = categoriaRepository
                .findById(request.getCategoriaId())
                .orElseThrow(() ->
                    new RuntimeException("Categoría no encontrada")
                );

        Publicacion publicacion = new Publicacion();

        publicacion.setUsuario(usuario);
        publicacion.setCategoria(categoria);
        publicacion.setTitulo(request.getTitulo());
        publicacion.setContenido(request.getContenido());

        Boolean anonima = request.getEsAnonima() == null
                ? true
                : request.getEsAnonima();

        publicacion.setEsAnonima(anonima);
        publicacion.setEstado("PENDING");

        return publicacionRepository.save(publicacion);
    }

    @Transactional(readOnly = true)
    public List<PublicacionResponse> listarPublicadas() {

        return publicacionRepository
                .findByEstadoOrderByFechaCreacionDesc("PUBLISHED")
                .stream()
                .map(publicacion -> {

                    String autor = publicacion.getEsAnonima()
                            ? "Usuario anónimo"
                            : publicacion.getUsuario()
                                .getNombreUsuario();

                    return new PublicacionResponse(
                            publicacion.getId(),
                            publicacion.getTitulo(),
                            publicacion.getContenido(),
                            publicacion.getCategoria()
                                .getNombre(),
                            autor,
                            publicacion.getEsAnonima(),
                            publicacion.getFechaCreacion()
                    );
                })
                .toList();
    }
}

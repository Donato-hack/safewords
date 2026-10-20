package com.safewords.api.servicios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safewords.api.dto.ModeracionReporteResponse;
import com.safewords.api.dto.ReporteModeracionRequest;
import com.safewords.api.dto.ReporteRequest;
import com.safewords.api.modelo.Comentario;
import com.safewords.api.modelo.Publicacion;
import com.safewords.api.modelo.Reporte;
import com.safewords.api.modelo.Usuario;
import com.safewords.api.modelo.repositorio.ComentarioRepository;
import com.safewords.api.modelo.repositorio.PublicacionRepository;
import com.safewords.api.modelo.repositorio.ReporteRepository;
import com.safewords.api.modelo.repositorio.UsuarioRepository;

@Service
public class ReporteService {

    private static final Set<String> MOTIVOS_VALIDOS = Set.of(
            "ACOSO",
            "AMENAZA",
            "LENGUAJE_OFENSIVO",
            "DATOS_PERSONALES",
            "CONTENIDO_INAPROPIADO",
            "OTRO"
    );

    private final ReporteRepository reporteRepository;
    private final PublicacionRepository publicacionRepository;
    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteService(
            ReporteRepository reporteRepository,
            PublicacionRepository publicacionRepository,
            ComentarioRepository comentarioRepository,
            UsuarioRepository usuarioRepository) {
        this.reporteRepository = reporteRepository;
        this.publicacionRepository = publicacionRepository;
        this.comentarioRepository = comentarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Reporte reportarPublicacion(Long id, ReporteRequest request, String correo) {
        validarMotivo(request.getMotivo());
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));
        validarVisible(publicacion.getEstado());

        Reporte reporte = new Reporte();
        reporte.setReportadoPor(buscarUsuario(correo));
        reporte.setPublicacion(publicacion);
        reporte.setMotivo(request.getMotivo().trim().toUpperCase());
        reporte.setDescripcion(limpiarDescripcion(request.getDescripcion()));
        return reporteRepository.save(reporte);
    }

    @Transactional
    public Reporte reportarComentario(Long id, ReporteRequest request, String correo) {
        validarMotivo(request.getMotivo());
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        validarVisible(comentario.getEstado());

        Reporte reporte = new Reporte();
        reporte.setReportadoPor(buscarUsuario(correo));
        reporte.setComentario(comentario);
        reporte.setMotivo(request.getMotivo().trim().toUpperCase());
        reporte.setDescripcion(limpiarDescripcion(request.getDescripcion()));
        return reporteRepository.save(reporte);
    }

    @Transactional(readOnly = true)
    public List<ModeracionReporteResponse> listarPendientes() {
        return reporteRepository.findByEstadoOrderByFechaReporteDesc("PENDING")
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Transactional
    public Reporte moderar(Long id, ReporteModeracionRequest request, String correoModerador) {
        validarEstado(request.getEstado());
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        reporte.setEstado(request.getEstado().trim().toUpperCase());
        reporte.setRespuestaModerador(limpiarDescripcion(request.getRespuesta()));
        reporte.setRevisadoPor(buscarUsuario(correoModerador));
        reporte.setFechaResolucion(LocalDateTime.now());
        return reporteRepository.save(reporte);
    }

    private ModeracionReporteResponse convertirRespuesta(Reporte reporte) {
        Publicacion publicacion = reporte.getPublicacion();
        Comentario comentario = reporte.getComentario();
        Usuario autor = comentario != null ? comentario.getUsuario() : publicacion.getUsuario();
        boolean anonimo = comentario != null
                ? Boolean.TRUE.equals(comentario.getEsAnonimo())
                : Boolean.TRUE.equals(publicacion.getEsAnonima());
        String contenido = comentario != null ? comentario.getContenido() : publicacion.getContenido();
        Long publicacionId = publicacion != null ? publicacion.getId() : comentario.getPublicacion().getId();
        String publicacionTitulo = publicacion != null
                ? publicacion.getTitulo()
                : comentario.getPublicacion().getTitulo();

        return new ModeracionReporteResponse(
                reporte.getId(),
                comentario != null ? "COMENTARIO" : "PUBLICACION",
                publicacionId,
                comentario != null ? comentario.getId() : null,
                publicacionTitulo,
                contenido,
                anonimo ? "Usuario anónimo" : autor.getNombreUsuario(),
                autor.getId(),
                autor.getNombreUsuario(),
                autor.getNombreCompleto(),
                autor.getCorreo(),
                reporte.getMotivo(),
                reporte.getDescripcion(),
                reporte.getEstado(),
                reporte.getFechaReporte()
        );
    }

    private Usuario buscarUsuario(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private void validarMotivo(String motivo) {
        if (motivo == null || !MOTIVOS_VALIDOS.contains(motivo.trim().toUpperCase())) {
            throw new RuntimeException("El motivo del reporte no es válido");
        }
    }

    private void validarEstado(String estado) {
        if (estado == null || (!estado.equals("IN_REVIEW")
                && !estado.equals("RESOLVED")
                && !estado.equals("REJECTED"))) {
            throw new RuntimeException("El estado del reporte no es válido");
        }
    }

    private void validarVisible(String estado) {
        if (!"PUBLISHED".equals(estado)) {
            throw new RuntimeException("Solo puedes reportar contenido publicado");
        }
    }

    private String limpiarDescripcion(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}

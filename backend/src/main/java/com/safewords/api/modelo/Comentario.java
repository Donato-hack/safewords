package com.safewords.api.modelo;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publicacion_id", nullable = false)
    private Publicacion publicacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comentario_padre_id")
    private Comentario comentarioPadre;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "es_anonimo", nullable = false)
    private Boolean esAnonimo = true;

    @Column(nullable = false, length = 20)
    private String estado = "PENDING";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderado_por")
    private Usuario moderadoPor;

    @Column(name = "fecha_moderacion")
    private LocalDateTime fechaModeracion;

    @Column(name = "nota_moderacion", length = 500)
    private String notaModeracion;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public Comentario() {
    }

    public Long getId() {
        return id;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Comentario getComentarioPadre() {
        return comentarioPadre;
    }

    public void setComentarioPadre(Comentario comentarioPadre) {
        this.comentarioPadre = comentarioPadre;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Boolean getEsAnonimo() {
        return esAnonimo;
    }

    public void setEsAnonimo(Boolean esAnonimo) {
        this.esAnonimo = esAnonimo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario getModeradoPor() {
        return moderadoPor;
    }

    public void setModeradoPor(Usuario moderadoPor) {
        this.moderadoPor = moderadoPor;
    }

    public LocalDateTime getFechaModeracion() {
        return fechaModeracion;
    }

    public void setFechaModeracion(LocalDateTime fechaModeracion) {
        this.fechaModeracion = fechaModeracion;
    }

    public String getNotaModeracion() {
        return notaModeracion;
    }

    public void setNotaModeracion(String notaModeracion) {
        this.notaModeracion = notaModeracion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}

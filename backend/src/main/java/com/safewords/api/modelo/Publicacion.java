package com.safewords.api.modelo;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "publicaciones")
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false, length = 180)
    private String titulo;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "es_anonima", nullable = false)
    private Boolean esAnonima = true;

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

    @Column(name = "fecha_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion;

    public Publicacion() {
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Boolean getEsAnonima() {
        return esAnonima;
    }

    public void setEsAnonima(Boolean esAnonima) {
        this.esAnonima = esAnonima;
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

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}

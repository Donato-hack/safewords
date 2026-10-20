package com.safewords.api.dto;

import jakarta.validation.constraints.NotBlank;

public class ComentarioRequest {

    @NotBlank
    private String contenido;

    private Boolean esAnonimo = true;

    private Long comentarioPadreId;

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

    public Long getComentarioPadreId() {
        return comentarioPadreId;
    }

    public void setComentarioPadreId(Long comentarioPadreId) {
        this.comentarioPadreId = comentarioPadreId;
    }
}
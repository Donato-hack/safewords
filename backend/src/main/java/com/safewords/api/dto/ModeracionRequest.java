package com.safewords.api.dto;

import jakarta.validation.constraints.NotBlank;

public class ModeracionRequest {

    @NotBlank
    private String estado;

    private String nota;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }
}
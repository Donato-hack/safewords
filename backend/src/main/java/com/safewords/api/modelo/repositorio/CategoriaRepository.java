package com.safewords.api.modelo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.safewords.api.modelo.Categoria;

public interface CategoriaRepository
        extends JpaRepository<Categoria, Long> {
}

package com.proyectogrado.auth_backend.repository;

import com.proyectogrado.auth_backend.model.Acompanante;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a la tabla acompanante.
 */
public interface AcompananteRepository
        extends JpaRepository<Acompanante, Integer> {
}
package com.proyectogrado.backend.repository;

import com.proyectogrado.backend.model.Acompanante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcompananteRepository
        extends JpaRepository<Acompanante, Integer> {
}
package com.proyectogrado.backend.repository;

import com.proyectogrado.backend.model.Gusto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GustoRepository extends JpaRepository<Gusto, Integer> {

    boolean existsByNombre(String nombre);
}

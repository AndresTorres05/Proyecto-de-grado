package com.proyectogrado.personas_backend.repository;

import com.proyectogrado.personas_backend.model.Gusto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GustoRepository extends JpaRepository<Gusto, Integer> {

    List<Gusto> findByCategoria(String categoria);

    boolean existsByNombre(String nombre);
}

package com.proyectogrado.persona_mayor_service.repository;

import com.proyectogrado.persona_mayor_service.model.Gusto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GustoRepository extends JpaRepository<Gusto, Integer> {

    List<Gusto> findByCategoria(String categoria);

    boolean existsByNombre(String nombre);
}

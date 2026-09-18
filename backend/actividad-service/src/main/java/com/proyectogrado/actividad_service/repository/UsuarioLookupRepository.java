package com.proyectogrado.actividad_service.repository;

import com.proyectogrado.actividad_service.model.UsuarioLookup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioLookupRepository extends JpaRepository<UsuarioLookup, Integer> {
}

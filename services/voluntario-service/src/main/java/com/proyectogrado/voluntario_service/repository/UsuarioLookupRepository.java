package com.proyectogrado.voluntario_service.repository;

import com.proyectogrado.voluntario_service.model.UsuarioLookup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioLookupRepository extends JpaRepository<UsuarioLookup, Integer> {
}

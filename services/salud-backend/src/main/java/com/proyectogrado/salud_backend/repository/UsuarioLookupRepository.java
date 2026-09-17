package com.proyectogrado.salud_backend.repository;

import com.proyectogrado.salud_backend.model.UsuarioLookup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioLookupRepository extends JpaRepository<UsuarioLookup, Integer> {
}

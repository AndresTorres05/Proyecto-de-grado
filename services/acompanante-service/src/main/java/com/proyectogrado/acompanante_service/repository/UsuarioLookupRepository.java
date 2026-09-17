package com.proyectogrado.acompanante_service.repository;

import com.proyectogrado.acompanante_service.model.UsuarioLookup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioLookupRepository extends JpaRepository<UsuarioLookup, Integer> {
}

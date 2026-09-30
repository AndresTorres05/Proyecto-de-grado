package com.proyectogrado.messaging_backend.repository;

import com.proyectogrado.messaging_backend.model.UsuarioLookup;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioLookupRepository extends JpaRepository<UsuarioLookup, Integer> {
}

package com.proyectogrado.auth_backend.repository;

import com.proyectogrado.auth_backend.model.Voluntario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoluntarioRepository extends JpaRepository<Voluntario, Integer> {
}
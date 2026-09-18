package com.proyectogrado.auth_backend.repository;

import com.proyectogrado.auth_backend.model.Organizacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizacionRepository extends JpaRepository<Organizacion, Integer> {

}
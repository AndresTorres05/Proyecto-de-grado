package com.proyectogrado.auth_backend.repository;

import com.proyectogrado.auth_backend.model.PersonaMayor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaMayorRepository extends JpaRepository<PersonaMayor, Integer> {
}

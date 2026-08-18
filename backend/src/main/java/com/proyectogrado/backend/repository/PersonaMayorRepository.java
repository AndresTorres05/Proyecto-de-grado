package com.proyectogrado.backend.repository;

import com.proyectogrado.backend.model.PersonaMayor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaMayorRepository extends JpaRepository<PersonaMayor, Integer> {
}

package com.proyectogrado.backend.repository;

import com.proyectogrado.backend.model.PersonaMayorAcompanante;
import com.proyectogrado.backend.model.PersonaMayorAcompananteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonaMayorAcompananteRepository
        extends JpaRepository<
                PersonaMayorAcompanante,
                PersonaMayorAcompananteId> {

        List<PersonaMayorAcompanante>
        findById_IdPersonaMayor(Integer idPersonaMayor);
}
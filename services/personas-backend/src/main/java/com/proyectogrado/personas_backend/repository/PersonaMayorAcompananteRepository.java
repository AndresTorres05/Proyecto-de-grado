package com.proyectogrado.personas_backend.repository;

import com.proyectogrado.personas_backend.model.PersonaMayorAcompanante;
import com.proyectogrado.personas_backend.model.PersonaMayorAcompananteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonaMayorAcompananteRepository
        extends JpaRepository<PersonaMayorAcompanante, PersonaMayorAcompananteId> {

    List<PersonaMayorAcompanante> findById_IdPersonaMayorAndEstado(Integer idPersonaMayor, String estado);

    List<PersonaMayorAcompanante> findById_IdAcompananteAndEstado(Integer idAcompanante, String estado);

    List<PersonaMayorAcompanante> findById_IdAcompanante(Integer idAcompanante);
}

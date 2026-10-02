package com.proyectogrado.acompanante_service.repository;

import com.proyectogrado.acompanante_service.model.PersonaMayorAcompanante;
import com.proyectogrado.acompanante_service.model.PersonaMayorAcompananteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a los vínculos entre personas mayores y acompañantes.
 */
public interface PersonaMayorAcompananteRepository
        extends JpaRepository<PersonaMayorAcompanante, PersonaMayorAcompananteId> {

    List<PersonaMayorAcompanante> findById_IdAcompananteAndEstado(Integer idAcompanante, String estado);

    List<PersonaMayorAcompanante> findById_IdAcompanante(Integer idAcompanante);

    List<PersonaMayorAcompanante> findById_IdPersonaMayorAndEstado(Integer idPersonaMayor, String estado);
}

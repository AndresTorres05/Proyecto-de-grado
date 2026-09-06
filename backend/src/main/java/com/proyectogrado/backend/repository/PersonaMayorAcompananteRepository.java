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

        List<PersonaMayorAcompanante>
        findById_IdPersonaMayorAndEstado(
                Integer idPersonaMayor,
                String estado
        );

        List<PersonaMayorAcompanante>
        findById_IdAcompanante(Integer idAcompanante);

        List<PersonaMayorAcompanante>
        findById_IdAcompananteAndEstado(
                Integer idAcompanante,
                String estado
        );
}
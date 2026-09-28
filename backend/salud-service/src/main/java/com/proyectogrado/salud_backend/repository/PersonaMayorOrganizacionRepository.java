package com.proyectogrado.salud_backend.repository;

import com.proyectogrado.salud_backend.model.PersonaMayorOrganizacion;
import com.proyectogrado.salud_backend.model.PersonaMayorOrganizacionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonaMayorOrganizacionRepository
        extends JpaRepository<
                PersonaMayorOrganizacion,
                PersonaMayorOrganizacionId
        > {

    Optional<PersonaMayorOrganizacion>
    findById_IdPersonaMayorAndId_IdOrganizacionAndEstado(
            Integer idPersonaMayor,
            Integer idOrganizacion,
            String estado
    );
}
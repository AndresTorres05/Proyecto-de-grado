package com.proyectogrado.backend.repository;

import com.proyectogrado.backend.model.PersonaMayorOrganizacion;
import com.proyectogrado.backend.model.PersonaMayorOrganizacionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonaMayorOrganizacionRepository
        extends JpaRepository<
                PersonaMayorOrganizacion,
                PersonaMayorOrganizacionId> {

    List<PersonaMayorOrganizacion>
    findById_IdPersonaMayor(Integer idPersonaMayor);

    List<PersonaMayorOrganizacion>
    findById_IdOrganizacion(Integer idOrganizacion);

    List<PersonaMayorOrganizacion>
    findById_IdPersonaMayorAndEstado(
            Integer idPersonaMayor,
            String estado
    );

    List<PersonaMayorOrganizacion>
    findById_IdOrganizacionAndEstado(
            Integer idOrganizacion,
            String estado
    );
}
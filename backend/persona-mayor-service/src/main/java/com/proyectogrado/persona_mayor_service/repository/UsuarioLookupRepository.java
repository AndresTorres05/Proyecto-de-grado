package com.proyectogrado.persona_mayor_service.repository;

import com.proyectogrado.persona_mayor_service.model.UsuarioLookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UsuarioLookupRepository extends JpaRepository<UsuarioLookup, Integer> {

    Optional<UsuarioLookup> findByCelular(String celular);

    List<UsuarioLookup> findByIdOrganizacion(Integer idOrganizacion);

    /**
     * Acompañantes y voluntarios que nacieron en ese mes y alguno de esos
     * dias. Su fecha de nacimiento solo vive en "usuario" (las personas
     * mayores se buscan aparte, con PersonaMayorLookupRepository).
     */
    @Query(value = """
            select distinct u.* from usuario u
            join usuario_rol ur on ur.id_usuario = u.id_usuario
            join rol r on r.id_rol = ur.id_rol
            where r.nombre in ('ACOMPANANTE', 'VOLUNTARIO')
              and u.fecha_nacimiento is not null
              and extract(month from u.fecha_nacimiento) = :mes
              and extract(day from u.fecha_nacimiento) in (:dias)
            """, nativeQuery = true)
    List<UsuarioLookup> findCumpleanosAcompanantesYVoluntarios(
            @Param("mes") int mes, @Param("dias") Collection<Integer> dias);
}

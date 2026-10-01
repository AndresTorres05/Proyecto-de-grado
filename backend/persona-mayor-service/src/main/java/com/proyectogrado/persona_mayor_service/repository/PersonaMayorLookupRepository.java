package com.proyectogrado.persona_mayor_service.repository;

import com.proyectogrado.persona_mayor_service.model.PersonaMayorLookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PersonaMayorLookupRepository extends JpaRepository<PersonaMayorLookup, Integer> {

    /**
     * Personas mayores que nacieron en ese mes y alguno de esos dias. La
     * fecha del registro queda en "usuario"; la de "persona_mayor" solo
     * existe si la persona edito su perfil, y en ese caso manda.
     */
    @Query(value = """
            select pm.* from persona_mayor pm
            join usuario u on u.id_usuario = pm.id_usuario
            where coalesce(pm.fecha_nacimiento, u.fecha_nacimiento) is not null
              and extract(month from coalesce(pm.fecha_nacimiento, u.fecha_nacimiento)) = :mes
              and extract(day from coalesce(pm.fecha_nacimiento, u.fecha_nacimiento)) in (:dias)
            """, nativeQuery = true)
    List<PersonaMayorLookup> findCumpleanos(@Param("mes") int mes, @Param("dias") Collection<Integer> dias);
}

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
     * Usuarios activos de cualquier rol que nacieron en ese mes y alguno
     * de esos dias. Igual que en PersonaMayorLookupRepository, la fecha de
     * "persona_mayor" (si la persona edito su perfil) manda sobre la del
     * registro.
     */
    @Query(value = """
            select u.* from usuario u
            left join persona_mayor pm on pm.id_usuario = u.id_usuario
            where u.activo is not false
              and coalesce(pm.fecha_nacimiento, u.fecha_nacimiento) is not null
              and extract(month from coalesce(pm.fecha_nacimiento, u.fecha_nacimiento)) = :mes
              and extract(day from coalesce(pm.fecha_nacimiento, u.fecha_nacimiento)) in (:dias)
            """, nativeQuery = true)
    List<UsuarioLookup> findCumpleanos(@Param("mes") int mes, @Param("dias") Collection<Integer> dias);
}

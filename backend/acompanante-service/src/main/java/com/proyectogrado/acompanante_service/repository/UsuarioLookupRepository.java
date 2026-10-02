package com.proyectogrado.acompanante_service.repository;

import com.proyectogrado.acompanante_service.model.UsuarioLookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Lectura de la tabla usuario (ver UsuarioLookup).
 */
public interface UsuarioLookupRepository extends JpaRepository<UsuarioLookup, Integer> {

    Optional<UsuarioLookup> findByCelular(String celular);

    /** true si el usuario tiene el rol indicado (PERSONA_MAYOR, ACOMPANANTE...). */
    @Query(value = """
            select count(*) > 0 from usuario_rol ur
            join rol r on r.id_rol = ur.id_rol
            where ur.id_usuario = :idUsuario and r.nombre = :rol
            """, nativeQuery = true)
    boolean tieneRol(@Param("idUsuario") Integer idUsuario, @Param("rol") String rol);
}

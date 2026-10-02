package com.proyectogrado.analitica_service.controller;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Resuelve la organización del usuario que hace la petición (el gateway
 * envía su id en X-User-Id). Solo una organización puede ver la analítica.
 */
@Component
public class OrganizacionActual {

    /** Organización del usuario: id y nombre (el nombre va en el PDF). */
    public record Organizacion(Integer id, String nombre) {
    }

    private final NamedParameterJdbcTemplate jdbc;

    public OrganizacionActual(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** La organización del usuario, o null si el usuario no es una organización. */
    public Organizacion de(Integer idUsuario) {
        List<Organizacion> resultado = jdbc.query("""
                SELECT u.id_organizacion, u.nombre_usuario
                  FROM usuario u
                 WHERE u.id_usuario = :id
                   AND u.id_organizacion IS NOT NULL
                """, new MapSqlParameterSource("id", idUsuario),
                (rs, i) -> new Organizacion(rs.getInt("id_organizacion"), rs.getString("nombre_usuario")));

        return resultado.isEmpty() ? null : resultado.get(0);
    }
}

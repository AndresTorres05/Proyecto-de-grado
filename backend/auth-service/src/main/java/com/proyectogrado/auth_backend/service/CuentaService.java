package com.proyectogrado.auth_backend.service;

import com.proyectogrado.auth_backend.model.Usuario;
import com.proyectogrado.auth_backend.repository.UsuarioRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Eliminacion de cuenta, valida para cualquier rol. No mira el rol del
 * usuario: borra todo lo que pueda referenciar su id_usuario en cada
 * tabla (si no aplica a su rol, el DELETE simplemente no afecta filas).
 *
 * Se hace con SQL nativo porque varias de estas tablas pertenecen a
 * otros servicios y auth-backend no tiene entidades para ellas; como
 * todos comparten la misma base, todo corre en una sola transaccion:
 * o se borra todo, o no se borra nada.
 */
@Service
public class CuentaService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UsuarioRepository usuarioRepository;

    public CuentaService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void eliminarCuenta(Integer idUsuario) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Integer idOrganizacion = usuario.getIdOrganizacion();

        // Datos como persona mayor
        ejecutar("DELETE FROM participacion WHERE id_persona_mayor = :id", idUsuario);
        ejecutar("DELETE FROM medicamento WHERE id_persona_mayor = :id", idUsuario);
        ejecutar("DELETE FROM persona_mayor_gusto WHERE id_persona_mayor = :id", idUsuario);
        ejecutar("DELETE FROM persona_mayor_organizacion WHERE id_persona_mayor = :id", idUsuario);

        // Relaciones persona mayor <-> acompanante (en cualquier sentido)
        ejecutar("DELETE FROM persona_mayor_acompanante"
                + " WHERE id_persona_mayor = :id OR id_acompanante = :id", idUsuario);

        // Perfiles de rol
        ejecutar("DELETE FROM persona_mayor WHERE id_usuario = :id", idUsuario);
        ejecutar("DELETE FROM acompanante WHERE id_usuario = :id", idUsuario);
        ejecutar("DELETE FROM voluntario WHERE id_usuario = :id", idUsuario);

        ejecutar("DELETE FROM usuario_rol WHERE id_usuario = :id", idUsuario);
        ejecutar("DELETE FROM usuario WHERE id_usuario = :id", idUsuario);

        // Si era el ultimo usuario de una organizacion, la organizacion
        // queda huerfana: se borra junto con sus actividades.
        if (idOrganizacion != null) {
            eliminarOrganizacionSinUsuarios(idOrganizacion);
        }
    }

    private void eliminarOrganizacionSinUsuarios(Integer idOrganizacion) {

        Number usuariosRestantes = (Number) entityManager
                .createNativeQuery("SELECT COUNT(*) FROM usuario WHERE id_organizacion = :id")
                .setParameter("id", idOrganizacion)
                .getSingleResult();

        if (usuariosRestantes.longValue() > 0) {
            return;
        }

        ejecutar("DELETE FROM participacion WHERE id_actividad IN"
                + " (SELECT id_actividad FROM actividad WHERE id_organizacion = :id)", idOrganizacion);
        ejecutar("DELETE FROM actividad WHERE id_organizacion = :id", idOrganizacion);
        ejecutar("DELETE FROM persona_mayor_organizacion WHERE id_organizacion = :id", idOrganizacion);
        ejecutar("DELETE FROM organizacion WHERE id_organizacion = :id", idOrganizacion);
    }

    private void ejecutar(String sql, Integer id) {
        entityManager.createNativeQuery(sql)
                .setParameter("id", id)
                .executeUpdate();
    }
}

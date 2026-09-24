package com.proyectogrado.acompanante_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Vista de SOLO LECTURA sobre la tabla "usuario", que es dueña de
 * auth-backend. acompanante-service NUNCA crea, edita ni borra usuarios;
 * esto existe solo para poder mostrar nombre/celular/correo de la
 * persona mayor en las respuestas de relaciones, sin tener que llamar
 * por HTTP a auth-backend en cada consulta (válido en arquitectura de
 * servicios con BD compartida, NO sería válido en microservicios
 * estrictos).
 */
@Entity
@Table(name = "usuario")
public class UsuarioLookup {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    @Column(name = "telefono")
    private String celular;

    @Column(name = "correo")
    private String correo;

    protected UsuarioLookup() {
        // JPA
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getCelular() {
        return celular;
    }

    public String getCorreo() {
        return correo;
    }
}

package com.proyectogrado.actividad_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Vista de SOLO LECTURA sobre la tabla "usuario", que es dueña de
 * auth-backend. actividad-service NUNCA crea, edita ni borra usuarios;
 * esto existe solo para:
 *  - saber a que organizacion pertenece el usuario autenticado
 *    (columna id_organizacion, solo tiene valor para el rol ORGANIZACION),
 *  - mostrar nombre/celular de un participante en las respuestas.
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

    @Column(name = "id_organizacion")
    private Integer idOrganizacion;

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

    public Integer getIdOrganizacion() {
        return idOrganizacion;
    }
}

package com.proyectogrado.organizacion_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Vista de SOLO LECTURA sobre la tabla "usuario", que es dueña de
 * auth-backend. organizacion-service NUNCA crea, edita ni borra
 * usuarios; esto existe solo para:
 *  - buscar una persona mayor por teléfono al asociarla, y
 *  - saber a qué organización pertenece el usuario autenticado
 *    (columna id_organizacion, presente en TODO usuario pero solo
 *    con valor para el rol ORGANIZACION).
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
    private String telefono;

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

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public Integer getIdOrganizacion() {
        return idOrganizacion;
    }
}

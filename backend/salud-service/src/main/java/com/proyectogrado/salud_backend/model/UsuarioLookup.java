package com.proyectogrado.salud_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Solo lectura sobre "usuario" (dueno de auth-backend). Mismo patron que
 * ya usamos en personas-backend: necesitamos el nombre/celular de la
 * persona mayor y del acompanante para redactar y enviar el SMS.
 */
@Entity
@Table(name = "usuario")
public class UsuarioLookup {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    @Column(name = "Celular")
    private String celular;

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
}

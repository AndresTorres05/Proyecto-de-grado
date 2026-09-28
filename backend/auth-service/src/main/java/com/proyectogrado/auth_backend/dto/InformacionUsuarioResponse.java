package com.proyectogrado.auth_backend.dto;

import java.time.LocalDate;

public class InformacionUsuarioResponse {

    private Integer idUsuario;
    private String nombre;
    private String celular;
    private String correo;
    private LocalDate fechaNacimiento;
    private String genero;
    private String direccion;
    private boolean tieneContrasena;

    public InformacionUsuarioResponse(
            Integer idUsuario,
            String nombre,
            String celular,
            String correo,
            LocalDate fechaNacimiento,
            String genero,
            String direccion,
            boolean tieneContrasena
    ) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.celular = celular;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.direccion = direccion;
        this.tieneContrasena = tieneContrasena;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCelular() {
        return celular;
    }

    public String getCorreo() {
        return correo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public String getDireccion() {
        return direccion;
    }

    public boolean isTieneContrasena() {
        return tieneContrasena;
    }
}
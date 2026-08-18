package com.proyectogrado.backend.dto;

import java.time.LocalDate;

public class ActividadResponse {

    private Integer idActividad;
    private Integer idOrganizacion;
    private String nombre;
    private LocalDate fecha;
    private String lugar;
    private String tipo;

    public ActividadResponse() {
    }

    public ActividadResponse(Integer idActividad, Integer idOrganizacion, String nombre,
                              LocalDate fecha, String lugar, String tipo) {
        this.idActividad = idActividad;
        this.idOrganizacion = idOrganizacion;
        this.nombre = nombre;
        this.fecha = fecha;
        this.lugar = lugar;
        this.tipo = tipo;
    }

    public Integer getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(Integer idActividad) {
        this.idActividad = idActividad;
    }

    public Integer getIdOrganizacion() {
        return idOrganizacion;
    }

    public void setIdOrganizacion(Integer idOrganizacion) {
        this.idOrganizacion = idOrganizacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}

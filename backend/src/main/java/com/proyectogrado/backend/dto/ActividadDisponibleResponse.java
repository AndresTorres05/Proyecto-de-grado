package com.proyectogrado.backend.dto;

import java.time.LocalDate;

public class ActividadDisponibleResponse {

    private Integer idActividad;
    private String nombre;
    private LocalDate fecha;
    private String lugar;
    private String tipo;
    private boolean inscrito;

    public ActividadDisponibleResponse() {
    }

    public ActividadDisponibleResponse(Integer idActividad, String nombre, LocalDate fecha,
                                        String lugar, String tipo, boolean inscrito) {
        this.idActividad = idActividad;
        this.nombre = nombre;
        this.fecha = fecha;
        this.lugar = lugar;
        this.tipo = tipo;
        this.inscrito = inscrito;
    }

    public Integer getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(Integer idActividad) {
        this.idActividad = idActividad;
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

    public boolean isInscrito() {
        return inscrito;
    }

    public void setInscrito(boolean inscrito) {
        this.inscrito = inscrito;
    }
}
package com.proyectogrado.backend.dto;

import java.time.LocalDate;

public class ActividadResponse {

    private Integer idActividad;
    private Integer idOrganizacion;
    private String nombre;
    private LocalDate fecha;
    private String lugar;
    private String tipo;
    private String descripcion;
private String hora;
private Integer cupos;

    public ActividadResponse() {
    }

public ActividadResponse(Integer idActividad, Integer idOrganizacion, String nombre,
                         String descripcion, LocalDate fecha, String hora,
                         String lugar, String tipo, Integer cupos) {
this.idActividad = idActividad;
this.idOrganizacion = idOrganizacion;
this.nombre = nombre;
this.descripcion = descripcion;
this.fecha = fecha;
this.hora = hora;
this.lugar = lugar;
this.tipo = tipo;
this.cupos = cupos;
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
 public String getDescripcion() {
    return descripcion;
}

public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
}

public String getHora() {
    return hora;
}

public void setHora(String hora) {
    this.hora = hora;
}

public Integer getCupos() {
    return cupos;
}

public void setCupos(Integer cupos) {
    this.cupos = cupos;
}   
}

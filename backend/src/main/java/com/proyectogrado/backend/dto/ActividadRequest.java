package com.proyectogrado.backend.dto;

import java.time.LocalDate;

public class ActividadRequest {

private String nombre;
private String descripcion;
private LocalDate fecha;
private String hora;
private String lugar;
private String tipo;
private Integer cupos;
private String responsable;

    public ActividadRequest() {
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
public String getResponsable() {
    return responsable;
}

public void setResponsable(String responsable) {
    this.responsable = responsable;
}
}

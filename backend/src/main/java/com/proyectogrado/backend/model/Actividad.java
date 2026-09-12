package com.proyectogrado.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "actividad")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    private Integer idActividad;

    @Column(name = "id_organizacion", nullable = false)
    private Integer idOrganizacion;

@Column(name = "nombre", nullable = false)
private String nombre;

@Column(name = "descripcion", columnDefinition = "TEXT")
private String descripcion;

@Column(name = "fecha")
private LocalDate fecha;

@Column(name = "hora")
private String hora;

@Column(name = "lugar")
private String lugar;

@Column(name = "tipo")
private String tipo;

@Column(name = "cupos")
private Integer cupos;

@Column(name = "responsable")
private String responsable;

    public Actividad() {
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

    public String getDescripcion() {
    return descripcion;
}

public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
}

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
    return hora;
}

public void setHora(String hora) {
    this.hora = hora;
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

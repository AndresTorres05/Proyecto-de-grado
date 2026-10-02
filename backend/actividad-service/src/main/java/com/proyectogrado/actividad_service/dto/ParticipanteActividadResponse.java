package com.proyectogrado.actividad_service.dto;

/**
 * Persona inscrita en una actividad y si asistió.
 */
public class ParticipanteActividadResponse {

    private Integer idPersonaMayor;
    private String nombre;
    private String celular;
    private Boolean asistio;

    public ParticipanteActividadResponse() {
    }

    public ParticipanteActividadResponse(Integer idPersonaMayor, String nombre,
                                          String celular, Boolean asistio) {
        this.idPersonaMayor = idPersonaMayor;
        this.nombre = nombre;
        this.celular = celular;
        this.asistio = asistio;
    }

    public Integer getIdPersonaMayor() {
        return idPersonaMayor;
    }

    public void setIdPersonaMayor(Integer idPersonaMayor) {
        this.idPersonaMayor = idPersonaMayor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Boolean getAsistio() {
        return asistio;
    }

    public void setAsistio(Boolean asistio) {
        this.asistio = asistio;
    }
}

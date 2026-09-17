package com.proyectogrado.actividad_service.dto;

public class ParticipanteActividadResponse {

    private Integer idPersonaMayor;
    private String nombre;
    private String telefono;
    private Boolean asistio;

    public ParticipanteActividadResponse() {
    }

    public ParticipanteActividadResponse(Integer idPersonaMayor, String nombre,
                                          String telefono, Boolean asistio) {
        this.idPersonaMayor = idPersonaMayor;
        this.nombre = nombre;
        this.telefono = telefono;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getAsistio() {
        return asistio;
    }

    public void setAsistio(Boolean asistio) {
        this.asistio = asistio;
    }
}

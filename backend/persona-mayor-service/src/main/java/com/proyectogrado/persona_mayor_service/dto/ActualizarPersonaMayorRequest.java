package com.proyectogrado.persona_mayor_service.dto;

/**
 * Cambios del perfil de la persona mayor (ver PersonaMayorInformacionController).
 */
public class ActualizarPersonaMayorRequest {

    private String fechaNacimiento; // "yyyy-MM-dd", opcional
    private String genero;
    private String direccion;

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
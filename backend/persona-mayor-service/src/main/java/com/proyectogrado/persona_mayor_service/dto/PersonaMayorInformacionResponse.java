package com.proyectogrado.persona_mayor_service.dto;

public class PersonaMayorInformacionResponse {

    private Integer idUsuario;
    private String nombre;
    private String celular;
    private String correo;
    private String fechaNacimiento;
    private String genero;
    private String direccion;

    public PersonaMayorInformacionResponse(Integer idUsuario, String nombre, String celular, String correo,
                                            String fechaNacimiento, String genero, String direccion) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.celular = celular;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.direccion = direccion;
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

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public String getDireccion() {
        return direccion;
    }
}
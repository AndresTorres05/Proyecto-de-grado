package com.proyectogrado.organizacion_service.dto;

public class OrganizacionInformacionResponse {

    private Integer idOrganizacion;
    private String nombre;
    private String correo;
    private String telefono;
    private String direccion;

    public OrganizacionInformacionResponse(Integer idOrganizacion, String nombre, String correo,
                                            String telefono, String direccion) {
        this.idOrganizacion = idOrganizacion;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public Integer getIdOrganizacion() {
        return idOrganizacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }
}
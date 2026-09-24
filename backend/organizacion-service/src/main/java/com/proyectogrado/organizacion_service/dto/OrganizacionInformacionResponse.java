package com.proyectogrado.organizacion_service.dto;

public class OrganizacionInformacionResponse {

    private Integer idOrganizacion;
    private String nombre;
    private String correo;
    private String celular;
    private String direccion;

    public OrganizacionInformacionResponse(Integer idOrganizacion, String nombre, String correo,
                                            String celular, String direccion) {
        this.idOrganizacion = idOrganizacion;
        this.nombre = nombre;
        this.correo = correo;
        this.celular = celular;
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

    public String getCelular() {
        return celular;
    }

    public String getDireccion() {
        return direccion;
    }
}
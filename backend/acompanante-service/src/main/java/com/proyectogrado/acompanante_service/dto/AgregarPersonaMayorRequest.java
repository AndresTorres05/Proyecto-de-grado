package com.proyectogrado.acompanante_service.dto;

/**
 * Celular de la persona mayor a la que el acompañante quiere acompañar y la
 * relación que tiene con ella (por ejemplo, "Hijo").
 */
public record AgregarPersonaMayorRequest(String celular, String relacion) {
}

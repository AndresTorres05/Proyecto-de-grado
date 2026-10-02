package com.proyectogrado.persona_mayor_service.dto;

/**
 * Celular del acompañante que la persona mayor quiere agregar y su parentesco.
 */
public record AgregarAcompananteRequest(String celular, String relacion) {
}

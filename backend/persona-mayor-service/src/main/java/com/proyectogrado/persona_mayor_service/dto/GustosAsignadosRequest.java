package com.proyectogrado.persona_mayor_service.dto;

import java.util.List;

/**
 * Lista completa de los gustos que deja marcados la persona mayor;
 * reemplaza a los anteriores.
 */
public class GustosAsignadosRequest {

    private List<Integer> idsGustos;

    public List<Integer> getIdsGustos() {
        return idsGustos;
    }

    public void setIdsGustos(List<Integer> idsGustos) {
        this.idsGustos = idsGustos;
    }
}

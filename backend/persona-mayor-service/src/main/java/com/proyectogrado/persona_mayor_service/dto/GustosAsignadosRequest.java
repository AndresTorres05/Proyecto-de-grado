package com.proyectogrado.persona_mayor_service.dto;

import java.util.List;

public class GustosAsignadosRequest {

    private List<Integer> idsGustos;

    public List<Integer> getIdsGustos() {
        return idsGustos;
    }

    public void setIdsGustos(List<Integer> idsGustos) {
        this.idsGustos = idsGustos;
    }
}

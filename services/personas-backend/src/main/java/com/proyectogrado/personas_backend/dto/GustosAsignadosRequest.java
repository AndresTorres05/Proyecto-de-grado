package com.proyectogrado.personas_backend.dto;

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

package com.proyectogrado.backend.dto;

import java.util.List;

public class GustosAsignadosRequest {

    private List<Integer> idsGustos;

    public GustosAsignadosRequest() {
    }

    public List<Integer> getIdsGustos() {
        return idsGustos;
    }

    public void setIdsGustos(List<Integer> idsGustos) {
        this.idsGustos = idsGustos;
    }
}

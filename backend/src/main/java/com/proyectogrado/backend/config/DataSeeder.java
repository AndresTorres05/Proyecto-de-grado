package com.proyectogrado.backend.config;

import com.proyectogrado.backend.model.Rol;
import com.proyectogrado.backend.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final List<String> ROLES_POR_DEFECTO = List.of(
            "ORGANIZACION", "VOLUNTARIO", "ACOMPANANTE", "PERSONA_MAYOR"
    );

    private final RolRepository rolRepository;

    public DataSeeder(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public void run(String... args) {
        for (String nombre : ROLES_POR_DEFECTO) {
            if (!rolRepository.existsByNombre(nombre)) {
                rolRepository.save(new Rol(nombre));
            }
        }
    }
}

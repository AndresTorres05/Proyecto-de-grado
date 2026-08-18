package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.RolRequest;
import com.proyectogrado.backend.dto.RolResponse;
import com.proyectogrado.backend.model.Rol;
import com.proyectogrado.backend.repository.RolRepository;
import com.proyectogrado.backend.repository.UsuarioRolRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:4200")
public class RolController {

    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public RolController(RolRepository rolRepository, UsuarioRolRepository usuarioRolRepository) {
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    @GetMapping
    public List<RolResponse> listar() {
        return rolRepository.findAll().stream()
                .map(r -> new RolResponse(r.getIdRol(), r.getNombre()))
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody RolRequest request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio");
        }
        if (rolRepository.existsByNombre(request.getNombre())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un rol con ese nombre");
        }

        Rol rol = rolRepository.save(new Rol(request.getNombre()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new RolResponse(rol.getIdRol(), rol.getNombre()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody RolRequest request) {
        Rol rol = rolRepository.findById(id).orElse(null);
        if (rol == null) {
            return ResponseEntity.notFound().build();
        }
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio");
        }

        rolRepository.findByNombre(request.getNombre()).ifPresent(existente -> {
            if (!existente.getIdRol().equals(id)) {
                throw new IllegalStateException("Ya existe un rol con ese nombre");
            }
        });

        rol.setNombre(request.getNombre());
        rol = rolRepository.save(rol);
        return ResponseEntity.ok(new RolResponse(rol.getIdRol(), rol.getNombre()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        if (!rolRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (usuarioRolRepository.existsByRol_IdRol(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar: hay usuarios con este tipo de usuario asignado");
        }

        rolRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> manejarConflicto(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}

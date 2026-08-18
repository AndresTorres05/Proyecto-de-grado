package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.GustoResponse;
import com.proyectogrado.backend.dto.GustosAsignadosRequest;
import com.proyectogrado.backend.model.Gusto;
import com.proyectogrado.backend.model.PersonaMayor;
import com.proyectogrado.backend.model.PersonaMayorGusto;
import com.proyectogrado.backend.model.Usuario;
import com.proyectogrado.backend.repository.GustoRepository;
import com.proyectogrado.backend.repository.PersonaMayorGustoRepository;
import com.proyectogrado.backend.repository.PersonaMayorRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persona-mayor/{idPersonaMayor}/gustos")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonaMayorGustoController {

    private final PersonaMayorRepository personaMayorRepository;
    private final GustoRepository gustoRepository;
    private final PersonaMayorGustoRepository personaMayorGustoRepository;
    private final UsuarioRepository usuarioRepository;

    public PersonaMayorGustoController(PersonaMayorRepository personaMayorRepository,
                                        GustoRepository gustoRepository,
                                        PersonaMayorGustoRepository personaMayorGustoRepository,
                                        UsuarioRepository usuarioRepository) {
        this.personaMayorRepository = personaMayorRepository;
        this.gustoRepository = gustoRepository;
        this.personaMayorGustoRepository = personaMayorGustoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<GustoResponse> listar(@PathVariable Integer idPersonaMayor) {
        return personaMayorGustoRepository.findByPersonaMayor_IdUsuario(idPersonaMayor).stream()
                .map(pmg -> new GustoResponse(pmg.getGusto().getIdGusto(), pmg.getGusto().getNombre()))
                .toList();
    }

    @PutMapping
    public ResponseEntity<?> asignar(@PathVariable Integer idPersonaMayor,
                                      @RequestBody GustosAsignadosRequest request,
                                      Authentication authentication) {
        Usuario usuarioAutenticado = usuarioRepository.findByCorreo(authentication.getName()).orElse(null);
        if (usuarioAutenticado == null || !usuarioAutenticado.getIdUsuario().equals(idPersonaMayor)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo puedes gestionar tus propios gustos");
        }

        PersonaMayor personaMayor = personaMayorRepository.findById(idPersonaMayor).orElse(null);
        if (personaMayor == null) {
            return ResponseEntity.notFound().build();
        }

        List<Integer> idsGustos = request.getIdsGustos() == null ? List.of() : request.getIdsGustos();
        List<Gusto> gustos = gustoRepository.findAllById(idsGustos);
        if (gustos.size() != idsGustos.size()) {
            return ResponseEntity.badRequest().body("Alguno de los gustos indicados no existe");
        }

        personaMayorGustoRepository.deleteByPersonaMayor_IdUsuario(idPersonaMayor);
        for (Gusto gusto : gustos) {
            personaMayorGustoRepository.save(new PersonaMayorGusto(personaMayor, gusto));
        }

        return ResponseEntity.ok(gustos.stream()
                .map(g -> new GustoResponse(g.getIdGusto(), g.getNombre()))
                .toList());
    }
}

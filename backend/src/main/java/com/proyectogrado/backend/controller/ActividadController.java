package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.ActividadDisponibleResponse;
import com.proyectogrado.backend.dto.ActividadRequest;
import com.proyectogrado.backend.dto.ActividadResponse;
import com.proyectogrado.backend.model.Actividad;
import com.proyectogrado.backend.model.Participacion;
import com.proyectogrado.backend.model.PersonaMayor;
import com.proyectogrado.backend.model.Usuario;
import com.proyectogrado.backend.repository.ActividadRepository;
import com.proyectogrado.backend.repository.ParticipacionRepository;
import com.proyectogrado.backend.repository.PersonaMayorRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/actividades")
@CrossOrigin(origins = "http://localhost:4200")
public class ActividadController {

    private final ActividadRepository actividadRepository;
    private final UsuarioRepository usuarioRepository;
    private final PersonaMayorRepository personaMayorRepository;
    private final ParticipacionRepository participacionRepository;

    public ActividadController(ActividadRepository actividadRepository,
                                UsuarioRepository usuarioRepository,
                                PersonaMayorRepository personaMayorRepository,
                                ParticipacionRepository participacionRepository) {
        this.actividadRepository = actividadRepository;
        this.usuarioRepository = usuarioRepository;
        this.personaMayorRepository = personaMayorRepository;
        this.participacionRepository = participacionRepository;
    }

    @GetMapping
    public List<ActividadResponse> listar() {
        return actividadRepository.findAll().stream().map(this::aResponse).toList();
    }

    @GetMapping("/mias")
    public ResponseEntity<?> listarMias(Authentication authentication) {
        Usuario usuario = usuarioActual(authentication);
        if (usuario == null || usuario.getIdOrganizacion() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo una organización tiene actividades propias");
        }

        List<ActividadResponse> actividades = actividadRepository.findByIdOrganizacion(usuario.getIdOrganizacion())
                .stream().map(this::aResponse).toList();
        return ResponseEntity.ok(actividades);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<?> listarDisponibles(Authentication authentication) {
        PersonaMayor personaMayor = personaMayorActual(authentication);
        if (personaMayor == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo una persona mayor puede ver esto");
        }

        Set<Integer> idsInscritos = participacionRepository.findById_IdPersonaMayor(personaMayor.getIdUsuario())
                .stream()
                .map(p -> p.getId().getIdActividad())
                .collect(Collectors.toSet());

        List<ActividadDisponibleResponse> respuesta = actividadRepository.findAll().stream()
                .map(a -> new ActividadDisponibleResponse(
                        a.getIdActividad(), a.getNombre(), a.getFecha(), a.getLugar(), a.getTipo(),
                        idsInscritos.contains(a.getIdActividad())
                ))
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/{id}/inscribirse")
    public ResponseEntity<?> inscribirse(@PathVariable Integer id, Authentication authentication) {
        PersonaMayor personaMayor = personaMayorActual(authentication);
        if (personaMayor == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo una persona mayor puede inscribirse");
        }

        Actividad actividad = actividadRepository.findById(id).orElse(null);
        if (actividad == null) {
            return ResponseEntity.notFound().build();
        }

        boolean yaInscrito = participacionRepository
                .findById_IdPersonaMayorAndId_IdActividad(personaMayor.getIdUsuario(), id)
                .isPresent();

        if (yaInscrito) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya estás inscrito en esta actividad");
        }

        participacionRepository.save(new Participacion(personaMayor, actividad));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/inscribirse")
    public ResponseEntity<?> cancelarInscripcion(@PathVariable Integer id, Authentication authentication) {
        PersonaMayor personaMayor = personaMayorActual(authentication);
        if (personaMayor == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo una persona mayor puede cancelar su inscripción");
        }

        Participacion participacion = participacionRepository
                .findById_IdPersonaMayorAndId_IdActividad(personaMayor.getIdUsuario(), id)
                .orElse(null);

        if (participacion == null) {
            return ResponseEntity.notFound().build();
        }

        participacionRepository.delete(participacion);

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ActividadRequest request, Authentication authentication) {
        Usuario usuario = usuarioActual(authentication);
        if (usuario == null || usuario.getIdOrganizacion() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo una organización puede crear actividades");
        }
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio");
        }

        Actividad actividad = new Actividad();
        actividad.setIdOrganizacion(usuario.getIdOrganizacion());
        actividad.setNombre(request.getNombre());
        actividad.setFecha(request.getFecha());
        actividad.setLugar(request.getLugar());
        actividad.setTipo(request.getTipo());

        actividad = actividadRepository.save(actividad);
        return ResponseEntity.status(HttpStatus.CREATED).body(aResponse(actividad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody ActividadRequest request,
                                         Authentication authentication) {
        Usuario usuario = usuarioActual(authentication);
        Actividad actividad = actividadRepository.findById(id).orElse(null);
        if (actividad == null) {
            return ResponseEntity.notFound().build();
        }
        if (usuario == null || usuario.getIdOrganizacion() == null
                || !usuario.getIdOrganizacion().equals(actividad.getIdOrganizacion())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Esta actividad no pertenece a tu organización");
        }
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio");
        }

        actividad.setNombre(request.getNombre());
        actividad.setFecha(request.getFecha());
        actividad.setLugar(request.getLugar());
        actividad.setTipo(request.getTipo());

        actividad = actividadRepository.save(actividad);
        return ResponseEntity.ok(aResponse(actividad));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, Authentication authentication) {
        Usuario usuario = usuarioActual(authentication);
        Actividad actividad = actividadRepository.findById(id).orElse(null);
        if (actividad == null) {
            return ResponseEntity.notFound().build();
        }
        if (usuario == null || usuario.getIdOrganizacion() == null
                || !usuario.getIdOrganizacion().equals(actividad.getIdOrganizacion())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Esta actividad no pertenece a tu organización");
        }

        actividadRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Usuario usuarioActual(Authentication authentication) {
        return usuarioRepository.findByCorreo(authentication.getName()).orElse(null);
    }

    private PersonaMayor personaMayorActual(Authentication authentication) {
        String identificador = authentication.getName();

        Usuario usuario = usuarioRepository.findByCorreo(identificador)
                .or(() -> usuarioRepository.findByTelefono(identificador))
                .orElse(null);

        if (usuario == null) {
            return null;
        }

        return personaMayorRepository.findById(usuario.getIdUsuario()).orElse(null);
    }

    private ActividadResponse aResponse(Actividad a) {
        return new ActividadResponse(a.getIdActividad(), a.getIdOrganizacion(), a.getNombre(),
                a.getFecha(), a.getLugar(), a.getTipo());
    }
}
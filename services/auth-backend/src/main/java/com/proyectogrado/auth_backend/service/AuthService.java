package com.proyectogrado.auth_backend.service;

import com.proyectogrado.auth_backend.client.MessagingClient;
import com.proyectogrado.auth_backend.dto.LoginOtpRequest;
import com.proyectogrado.auth_backend.dto.LoginRequest;
import com.proyectogrado.auth_backend.dto.LoginResponse;
import com.proyectogrado.auth_backend.dto.RegistroRequest;
import com.proyectogrado.auth_backend.model.Acompanante;
import com.proyectogrado.auth_backend.model.Organizacion;
import com.proyectogrado.auth_backend.model.PersonaMayor;
import com.proyectogrado.auth_backend.model.Rol;
import com.proyectogrado.auth_backend.model.Usuario;
import com.proyectogrado.auth_backend.model.UsuarioRol;
import com.proyectogrado.auth_backend.model.Voluntario;
import com.proyectogrado.auth_backend.repository.AcompananteRepository;
import com.proyectogrado.auth_backend.repository.OrganizacionRepository;
import com.proyectogrado.auth_backend.repository.PersonaMayorRepository;
import com.proyectogrado.auth_backend.repository.RolRepository;
import com.proyectogrado.auth_backend.repository.UsuarioRepository;
import com.proyectogrado.auth_backend.repository.UsuarioRolRepository;
import com.proyectogrado.auth_backend.repository.VoluntarioRepository;
import com.proyectogrado.auth_backend.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PersonaMayorRepository personaMayorRepository;
    private final AcompananteRepository acompananteRepository;
    private final OrganizacionRepository organizacionRepository;
    private final VoluntarioRepository voluntarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MessagingClient messagingClient;

    public AuthService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            PersonaMayorRepository personaMayorRepository,
            AcompananteRepository acompananteRepository,
            OrganizacionRepository organizacionRepository,
            VoluntarioRepository voluntarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            MessagingClient messagingClient
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.personaMayorRepository = personaMayorRepository;
        this.acompananteRepository = acompananteRepository;
        this.organizacionRepository = organizacionRepository;
        this.voluntarioRepository = voluntarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.messagingClient = messagingClient;
    }

    // =========================================================
    // LOGIN CON CORREO + CONTRASENA
    // =========================================================

    public LoginResponse login(LoginRequest request) {

        if (request == null
                || request.getCorreo() == null
                || request.getCorreo().isBlank()
                || request.getContrasena() == null
                || request.getContrasena().isBlank()) {

            throw new RuntimeException(
                    "El correo y la contraseña son obligatorios"
            );
        }

        String correo = request.getCorreo().trim().toLowerCase();

        Usuario usuario = usuarioRepository
                .findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException(
                        "Correo o contraseña incorrectos"
                ));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new RuntimeException("El usuario se encuentra inactivo");
        }

        boolean contrasenaCorrecta = passwordEncoder.matches(
                request.getContrasena(),
                usuario.getContrasenaHash()
        );

        if (!contrasenaCorrecta) {
            throw new RuntimeException("Correo o contraseña incorrectos");
        }

        return generarRespuestaLogin(usuario, "Inicio de sesión exitoso");
    }

    // =========================================================
    // LOGIN CON TELEFONO + OTP
    // =========================================================

    public LoginResponse loginConOtp(LoginOtpRequest request) {

        if (request == null
                || request.getTelefono() == null
                || request.getTelefono().isBlank()
                || request.getCodigo() == null
                || request.getCodigo().isBlank()) {

            throw new RuntimeException(
                    "El teléfono y el código son obligatorios"
            );
        }

        String telefono = request.getTelefono().trim();

        boolean codigoValido = messagingClient.verificarOtp(
                telefono,
                request.getCodigo().trim()
        );

        if (!codigoValido) {
            throw new RuntimeException("Código incorrecto o expirado");
        }

        Usuario usuario = usuarioRepository
                .findByTelefono(telefono)
                .orElseThrow(() -> new RuntimeException(
                        "No existe una cuenta con ese teléfono"
                ));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new RuntimeException("El usuario se encuentra inactivo");
        }

        return generarRespuestaLogin(usuario, "Inicio de sesión exitoso");
    }

    private LoginResponse generarRespuestaLogin(Usuario usuario, String mensaje) {

        List<UsuarioRol> usuariosRoles =
                usuarioRolRepository.findByUsuario_IdUsuario(
                        usuario.getIdUsuario()
                );

        String rol = usuariosRoles.stream()
                .filter(usuarioRol -> usuarioRol.getRol() != null)
                .map(usuarioRol -> usuarioRol.getRol().getNombre())
                .findFirst()
                .orElse("SIN_ROL");

        String token = jwtService.generarToken(
                usuario.getIdUsuario(),
                rol
        );

        return new LoginResponse(
                token,
                rol,
                mensaje,
                usuario.getIdUsuario(),
                usuario.getNombreUsuario()
        );
    }

    // =========================================================
    // REGISTRO (telefono siempre obligatorio;
    // correo+contrasena juntos y opcionales)
    // =========================================================

    @Transactional
    public LoginResponse registrar(RegistroRequest request) {

        validarRegistro(request);

        String telefono = request.getTelefono().trim();

        String correo = null;
        if (request.getCorreo() != null && !request.getCorreo().isBlank()) {
            correo = request.getCorreo().trim().toLowerCase();
        }

        String contrasena = null;
        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            contrasena = request.getContrasena().trim();
        }

        boolean registroConContrasena = contrasena != null;

        String rolNombre = request.getRol().trim().toUpperCase();

        if (usuarioRepository.existsByTelefono(telefono)) {
            throw new RuntimeException(
                    "Ya existe un usuario registrado con ese teléfono"
            );
        }

        if (correo != null && usuarioRepository.existsByCorreo(correo)) {
            throw new RuntimeException(
                    "Ya existe un usuario registrado con ese correo"
            );
        }

        Rol rol = rolRepository
                .findByNombre(rolNombre)
                .orElseThrow(() -> new RuntimeException(
                        "El rol solicitado no existe: " + rolNombre
                ));

        // -----------------------------------------------------
        // Si NO hay correo/contrasena, el telefono se confirma
        // con OTP verificado contra messaging-backend.
        // -----------------------------------------------------
        if (!registroConContrasena) {

            if (request.getCodigo() == null || request.getCodigo().isBlank()) {
                throw new RuntimeException(
                        "El código de verificación es obligatorio"
                );
            }

            boolean codigoValido = messagingClient.verificarOtp(
                    telefono,
                    request.getCodigo().trim()
            );

            if (!codigoValido) {
                throw new RuntimeException("Código incorrecto o expirado");
            }
        }

        // -----------------------------------------------------
        // Crear usuario
        // -----------------------------------------------------

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.getNombreUsuario().trim());
        usuario.setTelefono(telefono);

        if (correo != null) {
            usuario.setCorreo(correo);
        }

        if (registroConContrasena) {
            usuario.setContrasenaHash(passwordEncoder.encode(contrasena));
        }

        usuario.setActivo(true);

        Usuario usuarioGuardado = usuarioRepository.saveAndFlush(usuario);

        usuarioRolRepository.saveAndFlush(
                new UsuarioRol(usuarioGuardado, rol)
        );

        // -----------------------------------------------------
        // Crear el registro especifico segun el rol
        // -----------------------------------------------------

        switch (rolNombre) {

            case "PERSONA_MAYOR" -> {
                PersonaMayor personaMayor = new PersonaMayor(usuarioGuardado);

                if (request.getFechaNacimiento() != null
                        && !request.getFechaNacimiento().isBlank()) {
                    personaMayor.setFechaNacimiento(
                            LocalDate.parse(request.getFechaNacimiento())
                    );
                }

                personaMayor.setGenero(request.getGenero());
                personaMayor.setDireccion(request.getDireccion());

                personaMayorRepository.save(personaMayor);
            }

            case "ACOMPANANTE" -> {
                Acompanante acompanante = new Acompanante(usuarioGuardado, null);
                acompananteRepository.save(acompanante);
            }

            case "ORGANIZACION" -> {
                Organizacion organizacion = new Organizacion();
                organizacion.setDireccion(request.getDireccion());
                organizacion = organizacionRepository.save(organizacion);

                usuarioGuardado.setIdOrganizacion(organizacion.getIdOrganizacion());
                usuarioRepository.save(usuarioGuardado);
            }

            case "VOLUNTARIO" -> voluntarioRepository.save(
                    new Voluntario(usuarioGuardado, request.getDisponibilidad())
            );

            default -> throw new RuntimeException(
                    "Rol no soportado para registro: " + rolNombre
            );
        }

        String token = jwtService.generarToken(
                usuarioGuardado.getIdUsuario(),
                rol.getNombre()
        );

        return new LoginResponse(
                token,
                rol.getNombre(),
                "Cuenta creada exitosamente",
                usuarioGuardado.getIdUsuario(),
                usuarioGuardado.getNombreUsuario()
        );
    }

    private void validarRegistro(RegistroRequest request) {

        if (request == null) {
            throw new RuntimeException("Los datos del registro son obligatorios");
        }

        if (request.getNombreUsuario() == null || request.getNombreUsuario().isBlank()) {
            throw new RuntimeException("El nombre de usuario es obligatorio");
        }

        if (request.getRol() == null || request.getRol().isBlank()) {
            throw new RuntimeException("El rol es obligatorio");
        }

        if (request.getTelefono() == null || request.getTelefono().isBlank()) {
            throw new RuntimeException("El teléfono es obligatorio");
        }

        String telefono = request.getTelefono().trim();

        if (!telefono.matches("\\d{10}")) {
            throw new RuntimeException(
                    "El teléfono debe tener exactamente 10 dígitos"
            );
        }

        boolean tieneCorreo =
                request.getCorreo() != null && !request.getCorreo().isBlank();

        boolean tieneContrasena =
                request.getContrasena() != null && !request.getContrasena().isBlank();

        if (tieneCorreo != tieneContrasena) {
            throw new RuntimeException(
                    "El correo y la contraseña deben registrarse juntos"
            );
        }

        if (tieneCorreo) {
            String correo = request.getCorreo().trim().toLowerCase();

            if (!correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new RuntimeException("El correo electrónico no es válido");
            }
        }

        if (tieneContrasena && request.getContrasena().trim().length() < 6) {
            throw new RuntimeException(
                    "La contraseña debe tener mínimo 6 caracteres"
            );
        }
    }
}

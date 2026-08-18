package com.proyectogrado.backend.security;

import com.proyectogrado.backend.model.Usuario;
import com.proyectogrado.backend.model.UsuarioRol;
import com.proyectogrado.backend.repository.UsuarioRepository;
import com.proyectogrado.backend.repository.UsuarioRolRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository, UsuarioRolRepository usuarioRolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        String[] authorities = usuarioRolRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                .stream()
                .map(usuarioRol -> "ROLE_" + usuarioRol.getRol().getNombre())
                .toArray(String[]::new);

        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getContrasenaHash())
                .authorities(authorities)
                .disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                .build();
    }
}
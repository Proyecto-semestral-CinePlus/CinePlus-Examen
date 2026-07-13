package com.example.cineplus.services;

import com.example.cineplus.model.Usuario;
import com.example.cineplus.repository.UsuarioRepository;
import com.example.cineplus.util.RecursoNoEncontradoException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        log.info("Autenticando usuario con correo: {}", correo);
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con correo: {}", correo);
                    return new UsernameNotFoundException("Usuario no encontrado con correo: " + correo);
                });

        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getPassword())
                .roles("USER")
                .build();
    }

    public List<Usuario> getUsuarios() {
        log.info("Obteniendo todos los usuarios");
        return usuarioRepository.findAll();
    }

    public Usuario getUsuarioId(int id) {
        log.info("Buscando usuario con id: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con id: {}", id);
                    return new RecursoNoEncontradoException("Usuario no encontrado con id: " + id);
                });
    }

    public Usuario saveUsuario(Usuario usuario) {
        log.info("Registrando nuevo usuario con correo: {}", usuario.getCorreo());
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Usuario updateUsuario(int id, Usuario usuarioNuevo) {
        log.info("Actualizando usuario con id: {}", id);
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con id: {}", id);
                    return new RecursoNoEncontradoException("Usuario no encontrado con id: " + id);
                });

        existente.setNombre(usuarioNuevo.getNombre());
        existente.setApellido(usuarioNuevo.getApellido());
        existente.setCorreo(usuarioNuevo.getCorreo());
        existente.setPassword(passwordEncoder.encode(usuarioNuevo.getPassword()));

        return usuarioRepository.save(existente);
    }

    public void deleteUsuario(int id) {
        log.info("Eliminando usuario con id: {}", id);
        if (!usuarioRepository.existsById(id)) {
            log.warn("Usuario no encontrado con id: {}", id);
            throw new RecursoNoEncontradoException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
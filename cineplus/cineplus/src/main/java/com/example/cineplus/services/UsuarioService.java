package com.example.cineplus.services;

import com.example.cineplus.model.Usuario;
import com.example.cineplus.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // metodo requerido por UserDetailsService para Spring Security
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getPassword())
                .roles("USER")
                .build();
    }

    // metodo para obtener todos los usuarios
    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    // metodo para obtener un usuario por id
    public Usuario getUsuarioId(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // metodo para crear un usuario (encriptando la password)
    public Usuario saveUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    // metodo para actualizar un usuario
    public Usuario updateUsuario(int id, Usuario usuarioNuevo) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioExistente.setNombre(usuarioNuevo.getNombre());
        usuarioExistente.setApellido(usuarioNuevo.getApellido());
        usuarioExistente.setCorreo(usuarioNuevo.getCorreo());
        usuarioExistente.setPassword(passwordEncoder.encode(usuarioNuevo.getPassword()));

        return usuarioRepository.save(usuarioExistente);
    }

    // metodo para eliminar un usuario
    public void deleteUsuario(int id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}
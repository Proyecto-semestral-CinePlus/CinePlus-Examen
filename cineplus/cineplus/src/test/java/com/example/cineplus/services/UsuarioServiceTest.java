package com.example.cineplus.services;

import com.example.cineplus.model.Usuario;
import com.example.cineplus.repository.UsuarioRepository;
import com.example.cineplus.util.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de la capa de servicio (UsuarioService).
 * Se mockean el repositorio y el PasswordEncoder, ambas dependencias del servicio.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1, "Carlos", "Salfate", "carlos@mail.com", "1234");
    }

    // ---------- loadUserByUsername (Spring Security) ----------

    @Test
    @DisplayName("loadUserByUsername: deberia retornar UserDetails cuando el correo existe")
    void deberiaCargarUsuarioPorCorreo() {
        when(usuarioRepository.findByCorreo("carlos@mail.com")).thenReturn(Optional.of(usuario));

        UserDetails resultado = usuarioService.loadUserByUsername("carlos@mail.com");

        assertNotNull(resultado);
        assertEquals("carlos@mail.com", resultado.getUsername());
        verify(usuarioRepository, times(1)).findByCorreo("carlos@mail.com");
    }

    @Test
    @DisplayName("loadUserByUsername: deberia lanzar UsernameNotFoundException si el correo no existe")
    void deberiaLanzarExcepcionSiCorreoNoExiste() {
        when(usuarioRepository.findByCorreo("noexiste@mail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> usuarioService.loadUserByUsername("noexiste@mail.com"));
    }

    // ---------- CRUD ----------

    @Test
    @DisplayName("getUsuarios: deberia retornar todos los usuarios")
    void deberiaRetornarTodos() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        assertEquals(1, usuarioService.getUsuarios().size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getUsuarios: deberia retornar lista vacia cuando no hay registros")
    void deberiaRetornarVacia() {
        when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());

        assertTrue(usuarioService.getUsuarios().isEmpty());
    }

    @Test
    @DisplayName("getUsuarioId: deberia retornar el usuario cuando existe")
    void deberiaRetornarPorId() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        assertEquals("Carlos", usuarioService.getUsuarioId(1).getNombre());
        verify(usuarioRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("getUsuarioId: deberia lanzar RecursoNoEncontradoException cuando no existe")
    void deberiaLanzarExcepcionPorId() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.getUsuarioId(999));
    }

    @Test
    @DisplayName("saveUsuario: deberia encriptar la password antes de guardar")
    void deberiaGuardarConPasswordEncriptada() {
        when(passwordEncoder.encode("1234")).thenReturn("$2a$10$hashficticio");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.saveUsuario(usuario);

        assertNotNull(resultado);
        verify(passwordEncoder, times(1)).encode("1234");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("updateUsuario: deberia actualizar los campos y reencriptar la password")
    void deberiaActualizar() {
        Usuario datosNuevos = new Usuario(0, "Carlos Andres", "Salfate", "carlos2@mail.com", "5678");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("5678")).thenReturn("$2a$10$otrohash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.updateUsuario(1, datosNuevos);

        assertEquals("Carlos Andres", resultado.getNombre());
        assertEquals("carlos2@mail.com", resultado.getCorreo());
        verify(passwordEncoder, times(1)).encode("5678");
    }

    @Test
    @DisplayName("updateUsuario: deberia lanzar excepcion si el usuario no existe")
    void deberiaLanzarExcepcionAlActualizar() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.updateUsuario(999, usuario));

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("deleteUsuario: deberia eliminar el usuario cuando existe")
    void deberiaEliminar() {
        when(usuarioRepository.existsById(1)).thenReturn(true);

        usuarioService.deleteUsuario(1);

        verify(usuarioRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("deleteUsuario: deberia lanzar excepcion si el usuario no existe")
    void deberiaLanzarExcepcionAlEliminar() {
        when(usuarioRepository.existsById(999)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.deleteUsuario(999));

        verify(usuarioRepository, never()).deleteById(anyInt());
    }
}
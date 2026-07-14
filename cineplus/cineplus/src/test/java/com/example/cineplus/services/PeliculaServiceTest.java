package com.example.cineplus.services;

import com.example.cineplus.model.Pelicula;
import com.example.cineplus.repository.PeliculaRepository;
import com.example.cineplus.util.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de la capa de servicio (PeliculaService).
 *
 * Usa Mockito puro (@ExtendWith(MockitoExtension.class)): no levanta el contexto
 * de Spring, por lo que no intervienen Spring Security, la base de datos ni los
 * filtros. Se prueba unicamente la logica de negocio, aislando el repositorio
 * con un mock.
 */
@ExtendWith(MockitoExtension.class)
class PeliculaServiceTest {

    @Mock
    private PeliculaRepository peliculaRepository;

    @InjectMocks
    private PeliculaService peliculaService;

    private Pelicula pelicula;

    @BeforeEach
    void setUp() {
        pelicula = new Pelicula(1, "Matrix", "Sci-Fi", "PG-13", 136,
                "Un hacker descubre la verdad sobre su realidad");
    }

    @Test
    @DisplayName("getPeliculas: deberia retornar todas las peliculas")
    void deberiaRetornarTodas() {
        Pelicula otra = new Pelicula(2, "Inception", "Sci-Fi", "PG-13", 148, "Suenos dentro de suenos");
        when(peliculaRepository.findAll()).thenReturn(List.of(pelicula, otra));

        List<Pelicula> resultado = peliculaService.getPeliculas();

        assertEquals(2, resultado.size());
        assertEquals("Matrix", resultado.get(0).getTitulo());
        verify(peliculaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getPeliculas: deberia retornar lista vacia cuando no hay registros")
    void deberiaRetornarVacia() {
        when(peliculaRepository.findAll()).thenReturn(Collections.emptyList());

        assertTrue(peliculaService.getPeliculas().isEmpty());
        verify(peliculaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getPeliculaId: deberia retornar la pelicula cuando existe")
    void deberiaRetornarPorId() {
        when(peliculaRepository.findById(1)).thenReturn(Optional.of(pelicula));

        Pelicula resultado = peliculaService.getPeliculaId(1);

        assertNotNull(resultado);
        assertEquals("Matrix", resultado.getTitulo());
        verify(peliculaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("getPeliculaId: deberia lanzar RecursoNoEncontradoException cuando no existe")
    void deberiaLanzarExcepcionPorId() {
        when(peliculaRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> peliculaService.getPeliculaId(999));

        verify(peliculaRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("savePelicula: deberia guardar y retornar la pelicula")
    void deberiaGuardar() {
        when(peliculaRepository.save(any(Pelicula.class))).thenReturn(pelicula);

        Pelicula resultado = peliculaService.savePelicula(pelicula);

        assertNotNull(resultado);
        assertEquals("Matrix", resultado.getTitulo());
        verify(peliculaRepository, times(1)).save(pelicula);
    }

    @Test
    @DisplayName("updatePelicula: deberia actualizar los campos de una pelicula existente")
    void deberiaActualizar() {
        Pelicula datosNuevos = new Pelicula(0, "Matrix Reloaded", "Accion", "R", 138, "Segunda parte");

        when(peliculaRepository.findById(1)).thenReturn(Optional.of(pelicula));
        when(peliculaRepository.save(any(Pelicula.class))).thenAnswer(inv -> inv.getArgument(0));

        Pelicula resultado = peliculaService.updatePelicula(1, datosNuevos);

        assertEquals("Matrix Reloaded", resultado.getTitulo());
        assertEquals("Accion", resultado.getGenero());
        assertEquals(138, resultado.getDuracion());
        verify(peliculaRepository, times(1)).findById(1);
        verify(peliculaRepository, times(1)).save(any(Pelicula.class));
    }

    @Test
    @DisplayName("updatePelicula: deberia lanzar excepcion si la pelicula no existe")
    void deberiaLanzarExcepcionAlActualizar() {
        Pelicula datosNuevos = new Pelicula(0, "Matrix Reloaded", "Accion", "R", 138, "Segunda parte");

        when(peliculaRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> peliculaService.updatePelicula(999, datosNuevos));

        verify(peliculaRepository, never()).save(any(Pelicula.class));
    }

    @Test
    @DisplayName("deletePelicula: deberia eliminar la pelicula cuando existe")
    void deberiaEliminar() {
        when(peliculaRepository.existsById(1)).thenReturn(true);

        peliculaService.deletePelicula(1);

        verify(peliculaRepository, times(1)).existsById(1);
        verify(peliculaRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("deletePelicula: deberia lanzar excepcion si la pelicula no existe")
    void deberiaLanzarExcepcionAlEliminar() {
        when(peliculaRepository.existsById(999)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class,
                () -> peliculaService.deletePelicula(999));

        verify(peliculaRepository, never()).deleteById(anyInt());
    }
}
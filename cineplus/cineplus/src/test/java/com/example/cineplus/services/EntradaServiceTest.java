package com.example.cineplus.services;

import com.example.cineplus.model.Entrada;
import com.example.cineplus.repository.EntradaRepository;
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
 * Pruebas unitarias de la capa de servicio (EntradaService).
 */
@ExtendWith(MockitoExtension.class)
class EntradaServiceTest {

    @Mock
    private EntradaRepository entradaRepository;

    @InjectMocks
    private EntradaService entradaService;

    private Entrada entrada;

    @BeforeEach
    void setUp() {
        entrada = new Entrada(1, 3, "Sala 1", "A5", "18:00", 4500);
    }

    @Test
    @DisplayName("getEntradas: deberia retornar todas las entradas")
    void deberiaRetornarTodas() {
        Entrada otra = new Entrada(2, 1, "Sala 2", "B3", "20:00", 3900);
        when(entradaRepository.findAll()).thenReturn(List.of(entrada, otra));

        assertEquals(2, entradaService.getEntradas().size());
        verify(entradaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getEntradas: deberia retornar lista vacia cuando no hay registros")
    void deberiaRetornarVacia() {
        when(entradaRepository.findAll()).thenReturn(Collections.emptyList());

        assertTrue(entradaService.getEntradas().isEmpty());
    }

    @Test
    @DisplayName("getEntradaId: deberia retornar la entrada cuando existe")
    void deberiaRetornarPorId() {
        when(entradaRepository.findById(1)).thenReturn(Optional.of(entrada));

        Entrada resultado = entradaService.getEntradaId(1);

        assertNotNull(resultado);
        assertEquals("Sala 1", resultado.getSala());
        verify(entradaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("getEntradaId: deberia lanzar RecursoNoEncontradoException cuando no existe")
    void deberiaLanzarExcepcionPorId() {
        when(entradaRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> entradaService.getEntradaId(999));
    }

    @Test
    @DisplayName("saveEntrada: deberia guardar y retornar la entrada")
    void deberiaGuardar() {
        when(entradaRepository.save(any(Entrada.class))).thenReturn(entrada);

        Entrada resultado = entradaService.saveEntrada(entrada);

        assertNotNull(resultado);
        assertEquals("A5", resultado.getAsiento());
        verify(entradaRepository, times(1)).save(entrada);
    }

    @Test
    @DisplayName("updateEntrada: deberia actualizar los campos de una entrada existente")
    void deberiaActualizar() {
        Entrada datosNuevos = new Entrada(0, 1, "Sala 2", "C7", "21:30", 5000);

        when(entradaRepository.findById(1)).thenReturn(Optional.of(entrada));
        when(entradaRepository.save(any(Entrada.class))).thenAnswer(inv -> inv.getArgument(0));

        Entrada resultado = entradaService.updateEntrada(1, datosNuevos);

        assertEquals("Sala 2", resultado.getSala());
        assertEquals("C7", resultado.getAsiento());
        assertEquals(5000, resultado.getPrecio());
        verify(entradaRepository, times(1)).save(any(Entrada.class));
    }

    @Test
    @DisplayName("updateEntrada: deberia lanzar excepcion si la entrada no existe")
    void deberiaLanzarExcepcionAlActualizar() {
        when(entradaRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> entradaService.updateEntrada(999, entrada));

        verify(entradaRepository, never()).save(any(Entrada.class));
    }

    @Test
    @DisplayName("deleteEntrada: deberia eliminar la entrada cuando existe")
    void deberiaEliminar() {
        when(entradaRepository.existsById(1)).thenReturn(true);

        entradaService.deleteEntrada(1);

        verify(entradaRepository, times(1)).existsById(1);
        verify(entradaRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("deleteEntrada: deberia lanzar excepcion si la entrada no existe")
    void deberiaLanzarExcepcionAlEliminar() {
        when(entradaRepository.existsById(999)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class,
                () -> entradaService.deleteEntrada(999));

        verify(entradaRepository, never()).deleteById(anyInt());
    }
}

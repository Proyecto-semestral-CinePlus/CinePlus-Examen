package com.example.cineplus.controller;

import com.example.cineplus.model.Pelicula;
import com.example.cineplus.security.JwtAuthenticationFilter;
import com.example.cineplus.services.PeliculaService;
import com.example.cineplus.util.RecursoNoEncontradoException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas a nivel de Controlador para PeliculaController.
 *
 * excludeFilters: evita que @WebMvcTest intente construir el JwtAuthenticationFilter,
 *                 que depende de JwtUtil y UserDetailsService (no disponibles en el slice web).
 * addFilters = false: desactiva los filtros de seguridad en las peticiones de prueba,
 *                 para no tener que simular tokens JWT.
 */
@WebMvcTest(controllers = PeliculaController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class PeliculaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PeliculaService peliculaService;

    // ========================================================================
    // DATOS DE PRUEBA REUTILIZABLES
    // ========================================================================

    private Pelicula crearPelicula() {
        return new Pelicula(1, "Matrix", "Sci-Fi", "PG-13", 136,
                "Un hacker descubre la verdad sobre su realidad");
    }

    private Pelicula crearOtraPelicula() {
        return new Pelicula(2, "Inception", "Ciencia Ficción", "PG-13", 148,
                "Un ladrón que roba secretos corporativos a través de sueños");
    }

    // ========================================================================
    // GET /api/v1/peliculas - Obtener todas
    // ========================================================================

    @Nested
    @DisplayName("GET /api/v1/peliculas - Obtener todas las películas")
    class ListarPeliculasTest {

        @Test
        @DisplayName("Debería retornar 200 OK con lista de películas y enlaces HATEOAS")
        void deberiaRetornar200ConLista() throws Exception {
            List<Pelicula> peliculas = List.of(crearPelicula(), crearOtraPelicula());
            when(peliculaService.getPeliculas()).thenReturn(peliculas);

            mockMvc.perform(get("/api/v1/peliculas")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.peliculaList").exists())
                    .andExpect(jsonPath("$._embedded.peliculaList.length()").value(2))
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._embedded.peliculaList[0]._links.self").exists())
                    .andExpect(jsonPath("$._embedded.peliculaList[0]._links.peliculas").exists())
                    .andExpect(jsonPath("$._embedded.peliculaList[0]._links.actualizar").exists())
                    .andExpect(jsonPath("$._embedded.peliculaList[0]._links.eliminar").exists());

            verify(peliculaService, times(1)).getPeliculas();
        }

        @Test
        @DisplayName("Debería retornar 204 No Content cuando no hay películas")
        void deberiaRetornar204CuandoListaVacia() throws Exception {
            when(peliculaService.getPeliculas()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/peliculas")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNoContent());

            verify(peliculaService, times(1)).getPeliculas();
        }
    }

    // ========================================================================
    // GET /api/v1/peliculas/{id} - Obtener por ID
    // ========================================================================

    @Nested
    @DisplayName("GET /api/v1/peliculas/{id} - Obtener película por ID")
    class BuscarPeliculaTest {

        @Test
        @DisplayName("Debería retornar 200 OK con la película encontrada y su enlace self")
        void deberiaRetornar200CuandoExiste() throws Exception {
            Pelicula pelicula = crearPelicula();
            when(peliculaService.getPeliculaId(1)).thenReturn(pelicula);

            mockMvc.perform(get("/api/v1/peliculas/1")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.titulo").value("Matrix"))
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._links.peliculas").exists());

            verify(peliculaService, times(1)).getPeliculaId(1);
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found cuando la película no existe")
        void deberiaRetornar404CuandoNoExiste() throws Exception {
            when(peliculaService.getPeliculaId(999))
                    .thenThrow(new RecursoNoEncontradoException("Película no encontrada con id: 999"));

            mockMvc.perform(get("/api/v1/peliculas/999")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNotFound());

            verify(peliculaService, times(1)).getPeliculaId(999);
        }
    }

    // ========================================================================
    // POST /api/v1/peliculas - Crear película
    // ========================================================================

    @Nested
    @DisplayName("POST /api/v1/peliculas - Crear nueva película")
    class AgregarPeliculaTest {

        @Test
        @DisplayName("Debería retornar 201 Created con la película creada y su enlace self")
        void deberiaRetornar201AlCrear() throws Exception {
            Pelicula nueva = new Pelicula(0, "Matrix", "Sci-Fi", "PG-13", 136,
                    "Un hacker descubre la verdad sobre su realidad");
            Pelicula guardada = crearPelicula();

            when(peliculaService.savePelicula(any(Pelicula.class))).thenReturn(guardada);

            mockMvc.perform(post("/api/v1/peliculas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaTypes.HAL_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(nueva)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$._links.self").exists());

            verify(peliculaService, times(1)).savePelicula(any(Pelicula.class));
        }

        @Test
        @DisplayName("Debería retornar 400 Bad Request cuando los datos son inválidos")
        void deberiaRetornar400CuandoDatosInvalidos() throws Exception {
            // Título vacío y duración negativa: viola @NotBlank y @Positive
            Pelicula invalida = new Pelicula(0, "", "Sci-Fi", "PG-13", -5, "Sinopsis");

            mockMvc.perform(post("/api/v1/peliculas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalida)))
                    .andExpect(status().isBadRequest());

            verify(peliculaService, never()).savePelicula(any(Pelicula.class));
        }
    }

    // ========================================================================
    // PUT /api/v1/peliculas/{id} - Actualizar película
    // ========================================================================

    @Nested
    @DisplayName("PUT /api/v1/peliculas/{id} - Actualizar película existente")
    class ActualizarPeliculaTest {

        @Test
        @DisplayName("Debería retornar 200 OK con la película actualizada y su enlace self")
        void deberiaRetornar200AlActualizar() throws Exception {
            Pelicula actualizada = new Pelicula(1, "Matrix Reloaded", "Sci-Fi", "PG-13", 138,
                    "La segunda parte de la trilogía");

            when(peliculaService.updatePelicula(eq(1), any(Pelicula.class))).thenReturn(actualizada);

            mockMvc.perform(put("/api/v1/peliculas/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaTypes.HAL_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(actualizada)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.titulo").value("Matrix Reloaded"))
                    .andExpect(jsonPath("$._links.self").exists());

            verify(peliculaService, times(1)).updatePelicula(eq(1), any(Pelicula.class));
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found al actualizar una película inexistente")
        void deberiaRetornar404AlActualizarInexistente() throws Exception {
            Pelicula datos = crearPelicula();

            when(peliculaService.updatePelicula(eq(999), any(Pelicula.class)))
                    .thenThrow(new RecursoNoEncontradoException("Película no encontrada con id: 999"));

            mockMvc.perform(put("/api/v1/peliculas/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(datos)))
                    .andExpect(status().isNotFound());
        }
    }

    // ========================================================================
    // DELETE /api/v1/peliculas/{id} - Eliminar película
    // ========================================================================

    @Nested
    @DisplayName("DELETE /api/v1/peliculas/{id} - Eliminar película")
    class EliminarPeliculaTest {

        @Test
        @DisplayName("Debería retornar 200 OK con enlace de vuelta a la colección")
        void deberiaRetornar200AlEliminar() throws Exception {
            doNothing().when(peliculaService).deletePelicula(1);

            mockMvc.perform(delete("/api/v1/peliculas/1")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._links.peliculas").exists());

            verify(peliculaService, times(1)).deletePelicula(1);
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found al eliminar una película inexistente")
        void deberiaRetornar404AlEliminarInexistente() throws Exception {
            doThrow(new RecursoNoEncontradoException("Película no encontrada con id: 999"))
                    .when(peliculaService).deletePelicula(999);

            mockMvc.perform(delete("/api/v1/peliculas/999")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNotFound());
        }
    }
}
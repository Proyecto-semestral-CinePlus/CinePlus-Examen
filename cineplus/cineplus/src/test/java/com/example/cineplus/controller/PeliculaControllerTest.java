package com.example.cineplus.controller;

import com.example.cineplus.model.Pelicula;
import com.example.cineplus.services.PeliculaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
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
 * @WebMvcTest: carga solo la capa web (controller), no toda la aplicación.
 * addFilters = false: desactiva los filtros de Spring Security (incluido
 *              JwtAuthenticationFilter) para probar la lógica del controller
 *              de forma aislada, sin necesidad de simular tokens JWT.
 */
@WebMvcTest(PeliculaController.class)
@AutoConfigureMockMvc(addFilters = false)
class PeliculaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                    .andExpect(jsonPath("$._embedded.peliculaList[0]._links.peliculas").exists());

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
            when(peliculaService.getPeliculaId(999)).thenThrow(new RuntimeException("Película no encontrada"));

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
            Pelicula guardada = crearPelicula(); // ya con id = 1

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
    }
}

package com.example.cineplus.controller;

import com.example.cineplus.model.Entrada;
import com.example.cineplus.security.JwtAuthenticationFilter;
import com.example.cineplus.services.EntradaService;
import com.example.cineplus.util.RecursoNoEncontradoException;
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


@WebMvcTest(controllers = EntradaController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class EntradaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EntradaService entradaService;

    
    private Entrada crearEntrada() {
        return new Entrada(1, 3, "Sala 1", "A5", "18:00", 4500);
    }

    private Entrada crearOtraEntrada() {
        return new Entrada(2, 1, "Sala 2", "B3", "20:00", 3900);
    }

    private static final String JSON_ENTRADA_VALIDA = """
            {
              "idPelicula": 3,
              "sala": "Sala 1",
              "asiento": "A5",
              "horario": "18:00",
              "precio": 4500
            }
            """;

    // ========================================================================
    // GET /api/v1/entradas - Obtener todas
    // ========================================================================

    @Nested
    @DisplayName("GET /api/v1/entradas - Obtener todas las entradas")
    class ListarEntradasTest {

        @Test
        @DisplayName("Debería retornar 200 OK con lista de entradas y enlaces HATEOAS")
        void deberiaRetornar200ConLista() throws Exception {
            when(entradaService.getEntradas()).thenReturn(List.of(crearEntrada(), crearOtraEntrada()));

            mockMvc.perform(get("/api/v1/entradas").accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.entradaList").exists())
                    .andExpect(jsonPath("$._embedded.entradaList.length()").value(2))
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._embedded.entradaList[0]._links.self").exists())
                    .andExpect(jsonPath("$._embedded.entradaList[0]._links.actualizar").exists())
                    .andExpect(jsonPath("$._embedded.entradaList[0]._links.eliminar").exists());

            verify(entradaService, times(1)).getEntradas();
        }

        @Test
        @DisplayName("Debería retornar 204 No Content cuando no hay entradas")
        void deberiaRetornar204CuandoListaVacia() throws Exception {
            when(entradaService.getEntradas()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/entradas").accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNoContent());

            verify(entradaService, times(1)).getEntradas();
        }
    }

    
    @Nested
    @DisplayName("GET /api/v1/entradas/{id} - Obtener entrada por ID")
    class BuscarEntradaTest {

        @Test
        @DisplayName("Debería retornar 200 OK con la entrada encontrada")
        void deberiaRetornar200CuandoExiste() throws Exception {
            when(entradaService.getEntradaId(1)).thenReturn(crearEntrada());

            mockMvc.perform(get("/api/v1/entradas/1").accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.sala").value("Sala 1"))
                    .andExpect(jsonPath("$.precio").value(4500))
                    .andExpect(jsonPath("$._links.self").exists());

            verify(entradaService, times(1)).getEntradaId(1);
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found cuando la entrada no existe")
        void deberiaRetornar404CuandoNoExiste() throws Exception {
            when(entradaService.getEntradaId(999))
                    .thenThrow(new RecursoNoEncontradoException("Entrada no encontrada con id: 999"));

            mockMvc.perform(get("/api/v1/entradas/999").accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNotFound());

            verify(entradaService, times(1)).getEntradaId(999);
        }
    }

   
    @Nested
    @DisplayName("POST /api/v1/entradas - Crear nueva entrada")
    class AgregarEntradaTest {

        @Test
        @DisplayName("Debería retornar 201 Created con la entrada creada")
        void deberiaRetornar201AlCrear() throws Exception {
            when(entradaService.saveEntrada(any(Entrada.class))).thenReturn(crearEntrada());

            mockMvc.perform(post("/api/v1/entradas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaTypes.HAL_JSON_VALUE)
                            .content(JSON_ENTRADA_VALIDA))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$._links.self").exists());

            verify(entradaService, times(1)).saveEntrada(any(Entrada.class));
        }

        @Test
        @DisplayName("Debería retornar 400 Bad Request cuando los datos son inválidos")
        void deberiaRetornar400CuandoDatosInvalidos() throws Exception {
            String jsonInvalido = """
                    {
                      "idPelicula": -1,
                      "sala": "",
                      "asiento": "A5",
                      "horario": "18:00",
                      "precio": -100
                    }
                    """;

            mockMvc.perform(post("/api/v1/entradas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonInvalido))
                    .andExpect(status().isBadRequest());

            verify(entradaService, never()).saveEntrada(any(Entrada.class));
        }
    }

   
    @Nested
    @DisplayName("PUT /api/v1/entradas/{id} - Actualizar entrada existente")
    class ActualizarEntradaTest {

        @Test
        @DisplayName("Debería retornar 200 OK con la entrada actualizada")
        void deberiaRetornar200AlActualizar() throws Exception {
            String jsonActualizado = """
                    {
                      "idPelicula": 3,
                      "sala": "Sala 2",
                      "asiento": "C7",
                      "horario": "21:30",
                      "precio": 5000
                    }
                    """;

            Entrada actualizada = new Entrada(1, 3, "Sala 2", "C7", "21:30", 5000);
            when(entradaService.updateEntrada(eq(1), any(Entrada.class))).thenReturn(actualizada);

            mockMvc.perform(put("/api/v1/entradas/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaTypes.HAL_JSON_VALUE)
                            .content(jsonActualizado))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sala").value("Sala 2"))
                    .andExpect(jsonPath("$.precio").value(5000))
                    .andExpect(jsonPath("$._links.self").exists());

            verify(entradaService, times(1)).updateEntrada(eq(1), any(Entrada.class));
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found al actualizar una entrada inexistente")
        void deberiaRetornar404AlActualizarInexistente() throws Exception {
            when(entradaService.updateEntrada(eq(999), any(Entrada.class)))
                    .thenThrow(new RecursoNoEncontradoException("Entrada no encontrada con id: 999"));

            mockMvc.perform(put("/api/v1/entradas/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JSON_ENTRADA_VALIDA))
                    .andExpect(status().isNotFound());
        }
    }

   
    @Nested
    @DisplayName("DELETE /api/v1/entradas/{id} - Eliminar entrada")
    class EliminarEntradaTest {

        @Test
        @DisplayName("Debería retornar 200 OK con enlace de vuelta a la colección")
        void deberiaRetornar200AlEliminar() throws Exception {
            doNothing().when(entradaService).deleteEntrada(1);

            mockMvc.perform(delete("/api/v1/entradas/1").accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._links.entradas").exists());

            verify(entradaService, times(1)).deleteEntrada(1);
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found al eliminar una entrada inexistente")
        void deberiaRetornar404AlEliminarInexistente() throws Exception {
            doThrow(new RecursoNoEncontradoException("Entrada no encontrada con id: 999"))
                    .when(entradaService).deleteEntrada(999);

            mockMvc.perform(delete("/api/v1/entradas/999").accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNotFound());
        }
    }
}
package com.example.cineplus.controller;

import com.example.cineplus.model.Usuario;
import com.example.cineplus.security.JwtAuthenticationFilter;
import com.example.cineplus.services.UsuarioService;
import com.example.cineplus.util.RecursoNoEncontradoException;
import com.example.cineplus.webclient.SuscripcionClient;
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
 * Pruebas a nivel de Controlador para UsuarioController.
 *
 * excludeFilters: evita que @WebMvcTest intente construir el JwtAuthenticationFilter,
 *                 que depende de JwtUtil y UserDetailsService (no disponibles en el slice web).
 * addFilters = false: desactiva los filtros de seguridad en las peticiones de prueba.
 *
 * Nota: los JSON de las peticiones se escriben a mano (y no con ObjectMapper) porque el
 * campo 'password' está marcado como WRITE_ONLY y no se serializaría. Se incluye "id"
 * explícitamente porque el campo es un int primitivo y Jackson no acepta null.
 */
@WebMvcTest(controllers = UsuarioController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private SuscripcionClient suscripcionClient;

    // ========================================================================
    // DATOS DE PRUEBA REUTILIZABLES
    // ========================================================================

    private Usuario crearUsuario() {
        return new Usuario(1, "Carlos", "Salfate", "carlos@mail.com", "1234");
    }

    private Usuario crearOtroUsuario() {
        return new Usuario(2, "Javier", "Vasquez", "javier@mail.com", "5678");
    }

    private static final String JSON_USUARIO_VALIDO = """
            {
              "id": 0,
              "nombre": "Carlos",
              "apellido": "Salfate",
              "correo": "carlos@mail.com",
              "password": "1234"
            }
            """;

    // ========================================================================
    // GET /api/v1/usuarios - Obtener todos
    // ========================================================================

    @Nested
    @DisplayName("GET /api/v1/usuarios - Obtener todos los usuarios")
    class ListarUsuariosTest {

        @Test
        @DisplayName("Debería retornar 200 OK con lista de usuarios y enlaces HATEOAS")
        void deberiaRetornar200ConLista() throws Exception {
            when(usuarioService.getUsuarios()).thenReturn(List.of(crearUsuario(), crearOtroUsuario()));

            mockMvc.perform(get("/api/v1/usuarios").accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.usuarioList").exists())
                    .andExpect(jsonPath("$._embedded.usuarioList.length()").value(2))
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._embedded.usuarioList[0]._links.self").exists())
                    .andExpect(jsonPath("$._embedded.usuarioList[0]._links.actualizar").exists())
                    .andExpect(jsonPath("$._embedded.usuarioList[0]._links.eliminar").exists());

            verify(usuarioService, times(1)).getUsuarios();
        }

        @Test
        @DisplayName("Debería retornar 204 No Content cuando no hay usuarios")
        void deberiaRetornar204CuandoListaVacia() throws Exception {
            when(usuarioService.getUsuarios()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/usuarios").accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNoContent());

            verify(usuarioService, times(1)).getUsuarios();
        }
    }

    // ========================================================================
    // GET /api/v1/usuarios/{id} - Obtener por ID
    // ========================================================================

    @Nested
    @DisplayName("GET /api/v1/usuarios/{id} - Obtener usuario por ID")
    class BuscarUsuarioTest {

        @Test
        @DisplayName("Debería retornar 200 OK con el usuario encontrado y sin exponer la contraseña")
        void deberiaRetornar200CuandoExiste() throws Exception {
            when(usuarioService.getUsuarioId(1)).thenReturn(crearUsuario());

            mockMvc.perform(get("/api/v1/usuarios/1").accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombre").value("Carlos"))
                    .andExpect(jsonPath("$.correo").value("carlos@mail.com"))
                    .andExpect(jsonPath("$.password").doesNotExist())
                    .andExpect(jsonPath("$._links.self").exists());

            verify(usuarioService, times(1)).getUsuarioId(1);
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found cuando el usuario no existe")
        void deberiaRetornar404CuandoNoExiste() throws Exception {
            when(usuarioService.getUsuarioId(999))
                    .thenThrow(new RecursoNoEncontradoException("Usuario no encontrado con id: 999"));

            mockMvc.perform(get("/api/v1/usuarios/999").accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNotFound());

            verify(usuarioService, times(1)).getUsuarioId(999);
        }
    }

    // ========================================================================
    // POST /api/v1/usuarios - Crear usuario
    // ========================================================================

    @Nested
    @DisplayName("POST /api/v1/usuarios - Crear nuevo usuario")
    class AgregarUsuarioTest {

        @Test
        @DisplayName("Debería retornar 201 Created con el usuario creado")
        void deberiaRetornar201AlCrear() throws Exception {
            when(usuarioService.saveUsuario(any(Usuario.class))).thenReturn(crearUsuario());

            mockMvc.perform(post("/api/v1/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaTypes.HAL_JSON_VALUE)
                            .content(JSON_USUARIO_VALIDO))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$._links.self").exists());

            verify(usuarioService, times(1)).saveUsuario(any(Usuario.class));
        }

        @Test
        @DisplayName("Debería retornar 400 Bad Request cuando el correo tiene formato inválido")
        void deberiaRetornar400CuandoCorreoInvalido() throws Exception {
            String jsonInvalido = """
                    {
                      "id": 0,
                      "nombre": "",
                      "apellido": "Salfate",
                      "correo": "correo-malo",
                      "password": "1234"
                    }
                    """;

            mockMvc.perform(post("/api/v1/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonInvalido))
                    .andExpect(status().isBadRequest());

            verify(usuarioService, never()).saveUsuario(any(Usuario.class));
        }
    }

    // ========================================================================
    // PUT /api/v1/usuarios/{id} - Actualizar usuario
    // ========================================================================

    @Nested
    @DisplayName("PUT /api/v1/usuarios/{id} - Actualizar usuario existente")
    class ActualizarUsuarioTest {

        @Test
        @DisplayName("Debería retornar 200 OK con el usuario actualizado")
        void deberiaRetornar200AlActualizar() throws Exception {
            String jsonActualizado = """
                    {
                      "id": 1,
                      "nombre": "Carlos Andres",
                      "apellido": "Salfate",
                      "correo": "carlos@mail.com",
                      "password": "1234"
                    }
                    """;

            Usuario actualizado = new Usuario(1, "Carlos Andres", "Salfate", "carlos@mail.com", "1234");
            when(usuarioService.updateUsuario(eq(1), any(Usuario.class))).thenReturn(actualizado);

            mockMvc.perform(put("/api/v1/usuarios/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaTypes.HAL_JSON_VALUE)
                            .content(jsonActualizado))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("Carlos Andres"))
                    .andExpect(jsonPath("$._links.self").exists());

            verify(usuarioService, times(1)).updateUsuario(eq(1), any(Usuario.class));
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found al actualizar un usuario inexistente")
        void deberiaRetornar404AlActualizarInexistente() throws Exception {
            when(usuarioService.updateUsuario(eq(999), any(Usuario.class)))
                    .thenThrow(new RecursoNoEncontradoException("Usuario no encontrado con id: 999"));

            mockMvc.perform(put("/api/v1/usuarios/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JSON_USUARIO_VALIDO))
                    .andExpect(status().isNotFound());
        }
    }

    // ========================================================================
    // DELETE /api/v1/usuarios/{id} - Eliminar usuario
    // ========================================================================

    @Nested
    @DisplayName("DELETE /api/v1/usuarios/{id} - Eliminar usuario")
    class EliminarUsuarioTest {

        @Test
        @DisplayName("Debería retornar 200 OK con enlace de vuelta a la colección")
        void deberiaRetornar200AlEliminar() throws Exception {
            doNothing().when(usuarioService).deleteUsuario(1);

            mockMvc.perform(delete("/api/v1/usuarios/1").accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._links.usuarios").exists());

            verify(usuarioService, times(1)).deleteUsuario(1);
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found al eliminar un usuario inexistente")
        void deberiaRetornar404AlEliminarInexistente() throws Exception {
            doThrow(new RecursoNoEncontradoException("Usuario no encontrado con id: 999"))
                    .when(usuarioService).deleteUsuario(999);

            mockMvc.perform(delete("/api/v1/usuarios/999").accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNotFound());
        }
    }
}
package com.example.cineplus.controller;

import com.example.cineplus.model.Usuario;
import com.example.cineplus.services.UsuarioService;
import com.example.cineplus.webclient.SuscripcionClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Gestión de los usuarios registrados en CinePlus")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SuscripcionClient suscripcionClient;

    @Operation(summary = "Listar todos los usuarios",
            description = "Devuelve todos los usuarios con enlaces HATEOAS. Retorna 204 si no hay registros.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "204", description = "No hay usuarios registrados")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Usuario>>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.getUsuarios();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<EntityModel<Usuario>> modelos = usuarios.stream()
                .map(this::ensamblarModelo)
                .collect(Collectors.toList());
        CollectionModel<EntityModel<Usuario>> coleccion = CollectionModel.of(
                modelos,
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel()
        );
        return ResponseEntity.ok(coleccion);
    }

    @Operation(summary = "Buscar usuario por ID", description = "Obtiene un usuario según su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> buscarUsuario(@PathVariable int id) {
        Usuario usuario = usuarioService.getUsuarioId(id);
        return ResponseEntity.ok(ensamblarModelo(usuario));
    }

    @Operation(summary = "Crear un nuevo usuario",
            description = "Registra un usuario nuevo. La contraseña se almacena encriptada con BCrypt.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Usuario>> agregarUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario guardado = usuarioService.saveUsuario(usuario);
        return ResponseEntity
                .created(linkTo(methodOn(UsuarioController.class).buscarUsuario(guardado.getId())).toUri())
                .body(ensamblarModelo(guardado));
    }

    @Operation(summary = "Actualizar un usuario", description = "Modifica un usuario existente por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese ID"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> actualizarUsuario(@PathVariable int id, @Valid @RequestBody Usuario usuario) {
        Usuario actualizado = usuarioService.updateUsuario(id, usuario);
        return ResponseEntity.ok(ensamblarModelo(actualizado));
    }

    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario del sistema por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<Map<String, String>>> eliminarUsuario(@PathVariable int id) {
        usuarioService.deleteUsuario(id);

        Map<String, String> cuerpo = Map.of("mensaje", "Usuario eliminado correctamente");

        EntityModel<Map<String, String>> respuesta = EntityModel.of(cuerpo,
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("usuarios")
        );

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Listar suscripciones disponibles",
            description = "Consulta el microservicio ms-suscripciones (puerto 8081) mediante WebClient.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suscripciones obtenidas del microservicio remoto"),
            @ApiResponse(responseCode = "204", description = "El microservicio remoto no devolvió suscripciones")
    })
    @GetMapping("/suscripciones")
    public ResponseEntity<List<Map<String, Object>>> listarSuscripciones() {
        List<Map<String, Object>> suscripciones = suscripcionClient.obtenerSuscripciones();
        if (suscripciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(suscripciones);
    }

    // HATEOAS: self + lista + enlaces a PUT y DELETE del propio registro
    private EntityModel<Usuario> ensamblarModelo(Usuario usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).buscarUsuario(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("usuarios"),
                linkTo(methodOn(UsuarioController.class).actualizarUsuario(usuario.getId(), null)).withRel("actualizar"),
                linkTo(methodOn(UsuarioController.class).eliminarUsuario(usuario.getId())).withRel("eliminar")
        );
    }
}
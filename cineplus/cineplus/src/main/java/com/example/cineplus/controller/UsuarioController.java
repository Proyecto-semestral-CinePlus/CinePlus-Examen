package com.example.cineplus.controller;

import com.example.cineplus.model.Usuario;
import com.example.cineplus.services.UsuarioService;
import com.example.cineplus.webclient.SuscripcionClient;
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
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SuscripcionClient suscripcionClient;

    // Lista todos los usuarios
    @GetMapping
    public ResponseEntity<?> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.getUsuarios();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Usuario>> usuariosModel = usuarios.stream()
                .map(this::ensamblarModelo)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Usuario>> collectionModel = CollectionModel.of(
                usuariosModel,
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel()
        );

        return ResponseEntity.ok(collectionModel);
    }

    // Busca un usuario por id
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarUsuario(@PathVariable int id) {
        try {
            Usuario usuario = usuarioService.getUsuarioId(id);
            return ResponseEntity.ok(ensamblarModelo(usuario));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Crea un nuevo usuario
    @PostMapping
    public ResponseEntity<?> agregarUsuario(@Valid @RequestBody Usuario usuario) {
        try {
            Usuario guardado = usuarioService.saveUsuario(usuario);
            return ResponseEntity
                    .created(linkTo(methodOn(UsuarioController.class).buscarUsuario(guardado.getId())).toUri())
                    .body(ensamblarModelo(guardado));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Actualiza un usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable int id, @Valid @RequestBody Usuario usuario) {
        try {
            Usuario actualizado = usuarioService.updateUsuario(id, usuario);
            return ResponseEntity.ok(ensamblarModelo(actualizado));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Elimina un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable int id) {
        try {
            usuarioService.deleteUsuario(id);

            EntityModel<String> respuesta = EntityModel.of("Usuario eliminado",
                    linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("usuarios")
            );

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Consulta al microservicio suscripciones
    @GetMapping("/suscripciones")
    public ResponseEntity<?> listarSuscripciones() {
        try {
            List<Map<String, Object>> suscripciones = suscripcionClient.obtenerSuscripciones();
            if (suscripciones.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(suscripciones);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Método auxiliar para construir el HATEOAS
    private EntityModel<Usuario> ensamblarModelo(Usuario usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).buscarUsuario(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("usuarios"),
                linkTo(methodOn(UsuarioController.class).actualizarUsuario(usuario.getId(), null)).withRel("actualizar"),
                linkTo(methodOn(UsuarioController.class).eliminarUsuario(usuario.getId())).withRel("eliminar")
        );
    }
}
 
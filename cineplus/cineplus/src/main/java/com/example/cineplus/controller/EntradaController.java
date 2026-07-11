package com.example.cineplus.controller;

import com.example.cineplus.model.Entrada;
import com.example.cineplus.services.EntradaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/entradas")
public class EntradaController {

    @Autowired
    private EntradaService entradaService;

    // Lista todas las entradas
    @GetMapping
    public ResponseEntity<?> listarEntradas() {
        List<Entrada> entradas = entradaService.getEntradas();
        if (entradas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Entrada>> entradasModel = entradas.stream()
                .map(this::ensamblarModelo)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Entrada>> collectionModel = CollectionModel.of(
                entradasModel,
                linkTo(methodOn(EntradaController.class).listarEntradas()).withSelfRel()
        );

        return ResponseEntity.ok(collectionModel);
    }

    // Busca una entrada por id
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarEntrada(@PathVariable int id) {
        try {
            Entrada entrada = entradaService.getEntradaId(id);
            return ResponseEntity.ok(ensamblarModelo(entrada));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Crea una nueva entrada
    @PostMapping
    public ResponseEntity<?> agregarEntrada(@Valid @RequestBody Entrada entrada) {
        try {
            Entrada guardada = entradaService.saveEntrada(entrada);
            return ResponseEntity
                    .created(linkTo(methodOn(EntradaController.class).buscarEntrada(guardada.getId())).toUri())
                    .body(ensamblarModelo(guardada));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Actualiza una entrada existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEntrada(@PathVariable int id, @Valid @RequestBody Entrada entrada) {
        try {
            Entrada actualizada = entradaService.updateEntrada(id, entrada);
            return ResponseEntity.ok(ensamblarModelo(actualizada));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Elimina una entrada
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEntrada(@PathVariable int id) {
        try {
            entradaService.deleteEntrada(id);

            EntityModel<String> respuesta = EntityModel.of("Entrada eliminada",
                    linkTo(methodOn(EntradaController.class).listarEntradas()).withRel("entradas")
            );

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Método auxiliar para construir el HATEOAS
    private EntityModel<Entrada> ensamblarModelo(Entrada entrada) {
        return EntityModel.of(entrada,
                linkTo(methodOn(EntradaController.class).buscarEntrada(entrada.getId())).withSelfRel(),
                linkTo(methodOn(EntradaController.class).listarEntradas()).withRel("entradas")
        );
    }
}

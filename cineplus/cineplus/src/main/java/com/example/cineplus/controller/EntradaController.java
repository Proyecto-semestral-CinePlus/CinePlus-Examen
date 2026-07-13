package com.example.cineplus.controller;

import com.example.cineplus.model.Entrada;
import com.example.cineplus.services.EntradaService;
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
@RequestMapping("/api/v1/entradas")
@Tag(name = "Entradas", description = "Gestión de las entradas vendidas en CinePlus")
public class EntradaController {

    @Autowired
    private EntradaService entradaService;

    @Operation(summary = "Listar todas las entradas",
            description = "Devuelve todas las entradas con enlaces HATEOAS. Retorna 204 si no hay registros.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "204", description = "No hay entradas registradas")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Entrada>>> listarEntradas() {
        List<Entrada> entradas = entradaService.getEntradas();
        if (entradas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<EntityModel<Entrada>> modelos = entradas.stream()
                .map(this::ensamblarModelo)
                .collect(Collectors.toList());
        CollectionModel<EntityModel<Entrada>> coleccion = CollectionModel.of(
                modelos,
                linkTo(methodOn(EntradaController.class).listarEntradas()).withSelfRel()
        );
        return ResponseEntity.ok(coleccion);
    }

    @Operation(summary = "Buscar entrada por ID", description = "Obtiene una entrada según su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrada encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una entrada con ese ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Entrada>> buscarEntrada(@PathVariable int id) {
        Entrada entrada = entradaService.getEntradaId(id);
        return ResponseEntity.ok(ensamblarModelo(entrada));
    }

    @Operation(summary = "Crear una nueva entrada",
            description = "Registra una entrada nueva. Los datos se validan con Bean Validation.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entrada creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Entrada>> agregarEntrada(@Valid @RequestBody Entrada entrada) {
        Entrada guardada = entradaService.saveEntrada(entrada);
        return ResponseEntity
                .created(linkTo(methodOn(EntradaController.class).buscarEntrada(guardada.getId())).toUri())
                .body(ensamblarModelo(guardada));
    }

    @Operation(summary = "Actualizar una entrada", description = "Modifica una entrada existente por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrada actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe una entrada con ese ID"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Entrada>> actualizarEntrada(@PathVariable int id, @Valid @RequestBody Entrada entrada) {
        Entrada actualizada = entradaService.updateEntrada(id, entrada);
        return ResponseEntity.ok(ensamblarModelo(actualizada));
    }

    @Operation(summary = "Eliminar una entrada", description = "Elimina una entrada del sistema por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrada eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe una entrada con ese ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<Map<String, String>>> eliminarEntrada(@PathVariable int id) {
        entradaService.deleteEntrada(id);

        Map<String, String> cuerpo = Map.of("mensaje", "Entrada eliminada correctamente");

        EntityModel<Map<String, String>> respuesta = EntityModel.of(cuerpo,
                linkTo(methodOn(EntradaController.class).listarEntradas()).withRel("entradas")
        );

        return ResponseEntity.ok(respuesta);
    }

    // HATEOAS: self + lista + enlaces a PUT y DELETE del propio registro
    private EntityModel<Entrada> ensamblarModelo(Entrada entrada) {
        return EntityModel.of(entrada,
                linkTo(methodOn(EntradaController.class).buscarEntrada(entrada.getId())).withSelfRel(),
                linkTo(methodOn(EntradaController.class).listarEntradas()).withRel("entradas"),
                linkTo(methodOn(EntradaController.class).actualizarEntrada(entrada.getId(), null)).withRel("actualizar"),
                linkTo(methodOn(EntradaController.class).eliminarEntrada(entrada.getId())).withRel("eliminar")
        );
    }
}
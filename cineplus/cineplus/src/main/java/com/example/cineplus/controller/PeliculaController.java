package com.example.cineplus.controller;

import com.example.cineplus.model.Pelicula;
import com.example.cineplus.services.PeliculaService;
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
import java.util.Map;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/peliculas")
@Tag(name = "Películas", description = "Gestión del catálogo de películas de CinePlus")
public class PeliculaController {

        @Autowired
        private PeliculaService peliculaService;

        @Operation(summary = "Listar todas las películas", description = "Devuelve el catálogo completo con enlaces HATEOAS. Retorna 204 si no hay registros.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
                        @ApiResponse(responseCode = "204", description = "No hay películas registradas")
        })
        @GetMapping
        public ResponseEntity<CollectionModel<EntityModel<Pelicula>>> listarPeliculas() {
                List<Pelicula> peliculas = peliculaService.getPeliculas();
                if (peliculas.isEmpty()) {
                        return ResponseEntity.noContent().build();
                }
                List<EntityModel<Pelicula>> modelos = peliculas.stream()
                                .map(this::ensamblarModelo)
                                .collect(Collectors.toList());
                CollectionModel<EntityModel<Pelicula>> coleccion = CollectionModel.of(
                                modelos,
                                linkTo(methodOn(PeliculaController.class).listarPeliculas()).withSelfRel());
                return ResponseEntity.ok(coleccion);
        }

        @Operation(summary = "Buscar película por ID", description = "Obtiene una película según su identificador.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Película encontrada"),
                        @ApiResponse(responseCode = "404", description = "No existe una película con ese ID")
        })
        @GetMapping("/{id}")
        public ResponseEntity<EntityModel<Pelicula>> buscarPelicula(@PathVariable int id) {
                Pelicula pelicula = peliculaService.getPeliculaId(id);
                return ResponseEntity.ok(ensamblarModelo(pelicula));
        }

        @Operation(summary = "Crear una nueva película", description = "Registra una película nueva. Los datos se validan con Bean Validation.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Película creada correctamente"),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        })
        @PostMapping
        public ResponseEntity<EntityModel<Pelicula>> agregarPelicula(@Valid @RequestBody Pelicula pelicula) {
                Pelicula guardada = peliculaService.savePelicula(pelicula);
                return ResponseEntity
                                .created(linkTo(methodOn(PeliculaController.class).buscarPelicula(guardada.getId()))
                                                .toUri())
                                .body(ensamblarModelo(guardada));
        }

        @Operation(summary = "Actualizar una película", description = "Modifica una película existente por su ID.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Película actualizada correctamente"),
                        @ApiResponse(responseCode = "404", description = "No existe una película con ese ID"),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        })
        @PutMapping("/{id}")
        public ResponseEntity<EntityModel<Pelicula>> actualizarPelicula(@PathVariable int id,
                        @Valid @RequestBody Pelicula pelicula) {
                Pelicula actualizada = peliculaService.updatePelicula(id, pelicula);
                return ResponseEntity.ok(ensamblarModelo(actualizada));
        }

        @Operation(summary = "Eliminar una película", description = "Elimina una película del catálogo por su ID.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Película eliminada correctamente"),
                        @ApiResponse(responseCode = "404", description = "No existe una película con ese ID")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<EntityModel<Map<String, String>>> eliminarPelicula(@PathVariable int id) {
                peliculaService.deletePelicula(id);

                Map<String, String> cuerpo = Map.of("mensaje", "Película eliminada correctamente");

                EntityModel<Map<String, String>> respuesta = EntityModel.of(cuerpo,
                                linkTo(methodOn(PeliculaController.class).listarPeliculas()).withRel("peliculas"));

                return ResponseEntity.ok(respuesta);
        }

        // HATEOAS: self + lista + enlaces a PUT y DELETE del propio registro
        private EntityModel<Pelicula> ensamblarModelo(Pelicula pelicula) {
                return EntityModel.of(pelicula,
                                linkTo(methodOn(PeliculaController.class).buscarPelicula(pelicula.getId()))
                                                .withSelfRel(),
                                linkTo(methodOn(PeliculaController.class).listarPeliculas()).withRel("peliculas"),
                                linkTo(methodOn(PeliculaController.class).actualizarPelicula(pelicula.getId(), null))
                                                .withRel("actualizar"),
                                linkTo(methodOn(PeliculaController.class).eliminarPelicula(pelicula.getId()))
                                                .withRel("eliminar"));
        }
}
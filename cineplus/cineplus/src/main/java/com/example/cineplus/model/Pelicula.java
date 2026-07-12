package com.example.cineplus.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "peliculas")
@Schema(description = "Representa una película del catálogo de CinePlus")
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la película", example = "1")
    private int id;

    @NotBlank(message = "El título no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Título de la película", example = "Interstellar")
    private String titulo;

    @NotBlank(message = "El género no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Género principal de la película", example = "Ciencia ficción")
    private String genero;

    @NotBlank(message = "La clasificación no puede estar vacía")
    @Column(nullable = false)
    @Schema(description = "Clasificación por edad", example = "PG-13")
    private String clasificacion;

    @Positive(message = "La duración debe ser mayor a 0")
    @Column(nullable = false)
    @Schema(description = "Duración en minutos", example = "169")
    private int duracion;

    @NotBlank(message = "La sinopsis no puede estar vacía")
    @Column(nullable = false)
    @Schema(description = "Resumen argumental de la película", example = "Exploradores viajan por un agujero de gusano buscando un nuevo hogar para la humanidad.")
    private String sinopsis;
}
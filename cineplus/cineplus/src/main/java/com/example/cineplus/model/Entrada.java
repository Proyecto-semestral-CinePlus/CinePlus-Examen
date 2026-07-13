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
@Table(name = "entradas")
@Schema(description = "Representa una entrada vendida para una función de cine")
public class Entrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la entrada", example = "1")
    private Integer id;

    @Positive(message = "El id de película debe ser mayor a 0")
    @Column(nullable = false)
    @Schema(description = "ID de la película asociada a esta entrada", example = "3")
    private int idPelicula;

    @NotBlank(message = "La sala no puede estar vacía")
    @Column(nullable = false)
    @Schema(description = "Sala de proyección asignada", example = "Sala 1")
    private String sala;

    @NotBlank(message = "El asiento no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Número o código del asiento", example = "A5")
    private String asiento;

    @NotBlank(message = "El horario no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Horario de la función", example = "18:00")
    private String horario;

    @Positive(message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    @Schema(description = "Precio de la entrada en pesos", example = "4500")
    private double precio;
}
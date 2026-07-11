package com.example.cineplus.model;
 
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
 
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String nombre;
 
    @NotBlank(message = "El apellido no puede estar vacío")
    @Column(nullable = false)
    private String apellido;
 
    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "El correo debe tener un formato válido")
    @Column(nullable = false, unique = true)
    private String correo;
 
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Column(nullable = false)
    private String password;
}
 
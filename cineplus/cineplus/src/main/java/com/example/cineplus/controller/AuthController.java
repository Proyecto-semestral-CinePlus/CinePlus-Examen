package com.example.cineplus.controller;

import com.example.cineplus.model.Usuario;
import com.example.cineplus.security.JwtUtil;
import com.example.cineplus.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Registro de usuarios y login con JWT")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Iniciar sesión",
            description = "Recibe correo y contraseña, devuelve un token JWT si las credenciales son válidas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso, token JWT generado"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        String password = credenciales.get("password");

        log.info("Intento de login con correo: {}", correo);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(correo, password)
            );
        } catch (BadCredentialsException e) {
            log.warn("Login fallido para correo: {}", correo);
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas"));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(correo);
        String token = jwtUtil.generateToken(userDetails);

        log.info("Login exitoso para correo: {}", correo);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @Operation(summary = "Registrar un nuevo usuario",
            description = "Crea un usuario nuevo. La contraseña se almacena encriptada con BCrypt. Los datos se validan con Bean Validation.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@Valid @RequestBody Usuario usuario) {
        log.info("Registrando nuevo usuario con correo: {}", usuario.getCorreo());
        Usuario guardado = usuarioService.saveUsuario(usuario);
        return ResponseEntity.status(201).body(guardado);
    }
}
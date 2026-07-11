package com.example.cineplus.controller;

import com.example.cineplus.model.Usuario;
import com.example.cineplus.security.JwtUtil;
import com.example.cineplus.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    // POST /api/v1/auth/login → recibe correo y password, devuelve token JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            String correo = credenciales.get("correo");
            String password = credenciales.get("password");

            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(correo, password)
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(correo);
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }

    // POST /api/v1/auth/register → registra usuario con password encriptada
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> datos) {
        try {
            Usuario usuario = new Usuario();
            usuario.setNombre(datos.get("nombre"));
            usuario.setApellido(datos.get("apellido"));
            usuario.setCorreo(datos.get("correo"));
            usuario.setPassword(datos.get("password"));
            return ResponseEntity.status(201).body(usuarioService.saveUsuario(usuario));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
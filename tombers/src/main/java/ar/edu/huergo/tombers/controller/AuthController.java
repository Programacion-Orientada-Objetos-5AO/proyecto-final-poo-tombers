package ar.edu.huergo.tombers.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.huergo.tombers.dto.auth.AuthResponse;
import ar.edu.huergo.tombers.dto.auth.LoginRequest;
import ar.edu.huergo.tombers.dto.auth.RegisterRequest;
import ar.edu.huergo.tombers.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para manejar operaciones de autenticación.
 * Proporciona endpoints para registro y login de usuarios.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint para registrar un nuevo usuario.
     * @param request Los datos de registro del usuario.
     * @return Una respuesta con el token de autenticación y datos del usuario.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Endpoint para iniciar sesión de un usuario existente.
     * @param request Los datos de login del usuario.
     * @return Una respuesta con el token de autenticación y datos del usuario.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}

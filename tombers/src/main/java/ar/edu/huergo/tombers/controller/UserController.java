package ar.edu.huergo.tombers.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.huergo.tombers.dto.user.CreateProfileRequest;
import ar.edu.huergo.tombers.dto.user.UserResponse;
import ar.edu.huergo.tombers.dto.user.UserUpdateRequest;
import ar.edu.huergo.tombers.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de usuarios.
 * Proporciona endpoints para obtener y actualizar perfiles, búsqueda y creación de perfiles.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    /**
     * Obtiene el perfil del usuario autenticado.
     * @param authentication Información de autenticación del usuario.
     * @return Perfil del usuario en la respuesta HTTP.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        UserResponse response = userService.getUserProfile(email);
        return ResponseEntity.ok(response);
    }

    // ver todos los usuarios
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Actualiza el perfil del usuario autenticado.
     * @param authentication Información de autenticación del usuario.
     * @param request Datos para actualizar el perfil.
     * @return Perfil actualizado en la respuesta HTTP.
     */
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateUserProfile(
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequest request) {
        String email = authentication.getName();
        UserResponse response = userService.updateUserProfile(email, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Crea un perfil de usuario y le asigna un rol.
     * Solo accesible por administradores.
     * @param request Datos para crear el perfil.
     * @return Perfil creado en la respuesta HTTP.
     */
    @PostMapping("/CreateProfile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createProfile(@Valid @RequestBody CreateProfileRequest request) {
        UserResponse response = userService.createProfile(request.getEmail(), request.getUsername(), request.getRole());
        return ResponseEntity.ok(response);
    }
}

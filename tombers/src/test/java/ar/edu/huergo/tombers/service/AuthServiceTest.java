package ar.edu.huergo.tombers.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.huergo.tombers.dto.auth.AuthResponse;
import ar.edu.huergo.tombers.dto.auth.LoginRequest;
import ar.edu.huergo.tombers.dto.auth.RegisterRequest;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.repository.UserRepository;
import ar.edu.huergo.tombers.security.JwtService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Servicio - AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Debería registrar un nuevo usuario exitosamente")
    void deberiaRegistrarUsuarioExitosamente() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("nuevo@example.com");
        request.setUsername("nuevousuario");
        request.setPassword("Password123@");

        when(userRepository.existsByEmail(any(String.class))).thenReturn(false);
        when(userRepository.existsByUsername(any(String.class))).thenReturn(false);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        
        User savedUser = User.builder().id(1L).email(request.getEmail()).username(request.getUsername()).build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        // When
        AuthResponse response = authService.register(request);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Usuario registrado exitosamente", response.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar una excepción si el email ya existe al registrar")
    void deberiaLanzarExcepcionSiEmailExiste() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existente@example.com");
        
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(request);
        });
        
        assertEquals("El email ya está registrado", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar una excepción si el username ya existe al registrar")
    void deberiaLanzarExcepcionSiUsernameExiste() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("nuevo@example.com");
        request.setUsername("usuarioexistente");
        
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(request);
        });
        
        assertEquals("El nombre de usuario ya está en uso", exception.getMessage());
    }

    @Test
    @DisplayName("Debería iniciar sesión correctamente")
    void deberiaIniciarSesionCorrectamente() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "password");
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        // When
        AuthResponse response = authService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Inicio de sesión exitoso", response.getMessage());
    }
    
    @Test
    @DisplayName("Debería lanzar EntityNotFoundException si el usuario no existe al iniciar sesión")
    void deberiaLanzarExcepcionSiUsuarioNoExisteAlIniciarSesion() {
        // Given
        LoginRequest request = new LoginRequest("noexiste@example.com", "password");
        
        // Simula la autenticación correcta, pero el usuario no se encuentra después
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            authService.login(request);
        });
    }
}
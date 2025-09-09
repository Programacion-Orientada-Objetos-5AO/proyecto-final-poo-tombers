package ar.edu.huergo.tombers.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.huergo.tombers.dto.auth.AuthResponse;
import ar.edu.huergo.tombers.dto.auth.RegisterRequest;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.repository.UserRepository;
import ar.edu.huergo.tombers.security.JwtService;

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
        // Given: Una solicitud de registro válida
        RegisterRequest request = new RegisterRequest();
        request.setEmail("nuevo@example.com");
        request.setUsername("nuevousuario");
        request.setPassword("Password123@");

        // Configuramos los mocks para simular el comportamiento esperado
        when(userRepository.existsByEmail(any(String.class))).thenReturn(false);
        when(userRepository.existsByUsername(any(String.class))).thenReturn(false);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        
        User savedUser = User.builder().id(1L).email(request.getEmail()).username(request.getUsername()).build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        // When: Se llama al método de registro
        AuthResponse response = authService.register(request);

        // Then: La respuesta no debe ser nula y debe contener el token
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Usuario registrado exitosamente", response.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar una excepción si el email ya existe")
    void deberiaLanzarExcepcionSiEmailExiste() {
        // Given: Una solicitud con un email que ya está registrado
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existente@example.com");
        
        // Configuramos el mock para que devuelva 'true' cuando se verifique el email
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When & Then: Se espera que se lance una IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(request);
        });
        
        assertEquals("El email ya está registrado", exception.getMessage());
    }
}
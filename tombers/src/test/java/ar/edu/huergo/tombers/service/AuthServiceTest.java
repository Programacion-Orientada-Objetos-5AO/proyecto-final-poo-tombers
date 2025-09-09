package ar.edu.huergo.tombers.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.huergo.tombers.dto.auth.AuthResponse;
import ar.edu.huergo.tombers.dto.auth.LoginRequest;
import ar.edu.huergo.tombers.dto.auth.RegisterRequest;
import ar.edu.huergo.tombers.entity.Role;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.repository.UserRepository;
import ar.edu.huergo.tombers.security.JwtTokenService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Servicio - AuthService")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenService jwtTokenService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthService authService;

    private User baseUser() {
        return User.builder()
                .id(1L)
                .firstName("Ana").lastName("Lopez")
                .email("ana@test.com")
                .username("anita")
                .password("enc")
                .roles(Set.of(Role.USER))
                .build();
    }

    @Test
    @DisplayName("register crea usuario y genera token")
    void registerOk() {
        RegisterRequest req = RegisterRequest.builder()
                .firstName("Ana").lastName("Lopez")
                .email("ana@test.com")
                .username("anita")
                .password("PasswordSegura1234!")
                .build();

        when(userRepository.existsByEmail("ana@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("anita")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("enc");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtTokenService.generarToken(any(), eq(List.of("ROLE_USER")))).thenReturn("jwt-token");

        AuthResponse resp = authService.register(req);
        assertEquals("jwt-token", resp.getToken());
        assertEquals("ana@test.com", resp.getUser().getEmail());
    }

    @Test
    @DisplayName("register falla si email o username existen")
    void registerConflicts() {
        RegisterRequest req = RegisterRequest.builder()
                .firstName("Ana").lastName("Lopez")
                .email("ana@test.com").username("anita").password("x").build();
        when(userRepository.existsByEmail("ana@test.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> authService.register(req));

        when(userRepository.existsByEmail("ana@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("anita")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
    }

    @Test
    @DisplayName("login autentica y genera token")
    void loginOk() {
        LoginRequest req = LoginRequest.builder().email("ana@test.com").password("pwd").build();
        var u = baseUser();
        when(userRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(u));
        when(jwtTokenService.generarToken(eq(u), eq(List.of("ROLE_USER")))).thenReturn("jwt");

        // Stub de authenticate devolviendo un token autenticado
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("ana@test.com", "pwd"));

        AuthResponse resp = authService.login(req);
        assertEquals("jwt", resp.getToken());
        // Por implementación actual, AuthResponse.username usa getUsername() (email)
        assertEquals("ana@test.com", resp.getUser().getUsername());
    }

    @Test
    @DisplayName("login lanza EntityNotFound si usuario no existe")
    void loginNotFound() {
        LoginRequest req = LoginRequest.builder().email("no@test.com").password("pwd").build();
        when(userRepository.findByEmail("no@test.com")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> authService.login(req));
    }
}

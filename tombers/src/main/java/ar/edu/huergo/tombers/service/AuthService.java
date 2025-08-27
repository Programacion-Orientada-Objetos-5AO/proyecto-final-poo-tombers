package ar.edu.huergo.tombers.service;

import ar.edu.huergo.tombers.dto.auth.AuthResponse;
import ar.edu.huergo.tombers.dto.auth.LoginRequest;
import ar.edu.huergo.tombers.dto.auth.RegisterRequest;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.repository.UserRepository;
import ar.edu.huergo.tombers.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Verificar si el email ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Verificar si el username ya existe
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // Crear nuevo usuario
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .skills(request.getSkills() != null ? request.getSkills() : List.of())
                .age(request.getAge())
                .birthDate(request.getBirthDate())
                .languages(request.getLanguages())
                .specialization(request.getSpecialization())
                .phone(request.getPhone())
                .linkedin(request.getLinkedin())
                .github(request.getGithub())
                .portfolio(request.getPortfolio())
                .bio(request.getBio())
                .status(User.UserStatus.DISPONIBLE)
                .certifications(request.getCertifications() != null ? request.getCertifications() : List.of())
                .interests(request.getInterests() != null ? request.getInterests() : List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        // Generar token JWT
        String token = jwtService.generateToken(savedUser);

        return AuthResponse.builder()
                .message("Usuario registrado exitosamente")
                .token(token)
                .user(AuthResponse.UserInfo.builder()
                        .id(savedUser.getId())
                        .username(savedUser.getUsername())
                        .firstName(savedUser.getFirstName())
                        .lastName(savedUser.getLastName())
                        .email(savedUser.getEmail())
                        .build())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Autenticar usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Obtener usuario autenticado
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar token JWT
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .message("Inicio de sesión exitoso")
                .token(token)
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .build())
                .build();
    }
}



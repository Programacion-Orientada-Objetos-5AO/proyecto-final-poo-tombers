package ar.edu.huergo.tombers.config;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ar.edu.huergo.tombers.entity.Role;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * Componente para inicializar datos en la base de datos al iniciar la aplicación.
 * Crea un usuario administrador por defecto si no existe.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Método ejecutado después de la construcción del bean para inicializar datos.
     * Crea un usuario administrador con credenciales predeterminadas si no existe.
     */
    @PostConstruct
    public void init() {
        if (!userRepository.existsByEmail("admin@tombers.com")) {
            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("Tombers")
                    .email("admin@tombers.com")
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .status(User.UserStatus.DISPONIBLE)
                    .roles(Set.of(Role.ADMIN))
                    .build();
            userRepository.save(admin);
        }
    }
}

package ar.edu.huergo.tombers.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ar.edu.huergo.tombers.entity.Rol;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.repository.UserRepository;
import ar.edu.huergo.tombers.repository.security.RolRepository;
import lombok.RequiredArgsConstructor;

/**
 * Componente para inicializar datos en la base de datos al iniciar la aplicación.
 * Crea un usuario administrador por defecto si no existe.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RolRepository rolRepository, UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            Rol admin = rolRepository.findByNombre("ADMIN").orElseGet(() -> rolRepository.save(new Rol("ADMIN")));
            Rol cliente = rolRepository.findByNombre("CLIENTE").orElseGet(() -> rolRepository.save(new Rol("CLIENTE")));

            if (!userRepository.existsByEmail("admin@tombers.com")) {
                User userAdmin = User.builder()
                        .firstName("Admin")
                        .lastName("Tombers")
                        .email("admin@tombers.com")
                        .username("admin")
                        .password(encoder.encode("admin123"))
                        .status(User.UserStatus.DISPONIBLE)
                        .roles(Set.of(admin))
                        .build();
                userRepository.save(userAdmin);
            }
        };
    }
}

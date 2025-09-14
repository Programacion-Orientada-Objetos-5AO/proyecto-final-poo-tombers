package ar.edu.huergo.tombers.repository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ar.edu.huergo.tombers.entity.Rol;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.repository.security.RolRepository;

@DataJpaTest
@DisplayName("Tests de Repositorio - UserRepository")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;



    @Test
    @DisplayName("findByEmail / findByUsername / exists* funcionan")
    void findAndExistsMethods() {
        // Given
        Rol userRole = rolRepository.save(new Rol("USER"));
        User u = User.builder()
                .firstName("Ana")
                .lastName("Alvarez")
                .email("a@a.com")
                .username("userA")
                .password("pwd")
                .skills(List.of("java", "spring"))
                .status(User.UserStatus.DISPONIBLE)
                .roles(Set.of(userRole))
                .build();
        userRepository.save(u);

        // When - Then
        assertTrue(userRepository.findByEmail("a@a.com").isPresent());
        assertTrue(userRepository.findByUsername("userA").isPresent());
        assertTrue(userRepository.existsByEmail("a@a.com"));
        assertTrue(userRepository.existsByUsername("userA"));
        assertFalse(userRepository.existsByEmail("x@x.com"));
    }

}


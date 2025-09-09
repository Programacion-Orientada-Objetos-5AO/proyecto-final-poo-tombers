package ar.edu.huergo.tombers.repository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ar.edu.huergo.tombers.entity.Role;
import ar.edu.huergo.tombers.entity.User;

@DataJpaTest
@DisplayName("Tests de Repositorio - UserRepository")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User newUser(String email, String username, String firstName, String lastName) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .username(username)
                .password("pwd")
                .skills(List.of("java", "spring"))
                .status(User.UserStatus.DISPONIBLE)
                .roles(Set.of(Role.USER))
                .build();
    }

    @Test
    @DisplayName("findByEmail / findByUsername / exists* funcionan")
    void findAndExistsMethods() {
        // Given
        User u = newUser("a@a.com", "userA", "Ana", "Alvarez");
        userRepository.save(u);

        // When - Then
        assertTrue(userRepository.findByEmail("a@a.com").isPresent());
        assertTrue(userRepository.findByUsername("userA").isPresent());
        assertTrue(userRepository.existsByEmail("a@a.com"));
        assertTrue(userRepository.existsByUsername("userA"));
        assertFalse(userRepository.existsByEmail("x@x.com"));
    }

}


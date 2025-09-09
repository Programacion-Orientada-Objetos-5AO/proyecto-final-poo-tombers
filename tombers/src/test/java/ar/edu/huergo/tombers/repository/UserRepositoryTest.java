package ar.edu.huergo.tombers.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

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

    @Test
    @DisplayName("findAvailableUsers devuelve solo DISPONIBLE")
    void findAvailableUsers() {
        // Given
        userRepository.save(newUser("d@d.com", "userD", "Diego", "Diaz"));
        User ocupado = newUser("o@o.com", "userO", "Olga", "Ortega");
        ocupado.setStatus(User.UserStatus.OCUPADO);
        userRepository.save(ocupado);

        // When
        var disponibles = userRepository.findAvailableUsers();

        // Then
        assertEquals(1, disponibles.size());
        assertEquals("d@d.com", disponibles.get(0).getEmail());
    }

    @Test
    @DisplayName("searchUsers busca por nombre, apellido, especialización y skills")
    void searchUsersByFields() {
        // Given
        userRepository.save(newUser("a@a.com", "userA", "Ana", "Lopez"));
        var u2 = newUser("b@b.com", "userB", "Beto", "Gomez");
        u2.setSpecialization("backend java");
        u2.setSkills(List.of("spring", "docker"));
        userRepository.save(u2);

        // When - Then
        assertEquals(1, userRepository.searchUsers("ana").size());
        assertEquals(1, userRepository.searchUsers("gomez").size());
        assertEquals(1, userRepository.searchUsers("backend").size());
        assertEquals(1, userRepository.searchUsers("docker").size());
        assertEquals(0, userRepository.searchUsers("no existe").size());
    }
}


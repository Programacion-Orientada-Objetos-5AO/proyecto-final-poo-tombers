package ar.edu.huergo.tombers.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ar.edu.huergo.tombers.entity.User;

@DataJpaTest
@DisplayName("Tests de Repositorio - UserRepository")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Limpiamos la base de datos antes de cada test para evitar interferencias
        userRepository.deleteAll();
        
        // Given: Creamos datos de prueba
        user1 = User.builder()
                .firstName("Juan")
                .lastName("Perez")
                .email("juan.perez@example.com")
                .username("juanperez")
                .password("password123")
                .status(User.UserStatus.DISPONIBLE)
                .specialization("Backend Developer")
                .skills(List.of("Java", "Spring"))
                .build();

        user2 = User.builder()
                .firstName("Ana")
                .lastName("Gomez")
                .email("ana.gomez@example.com")
                .username("anagomez")
                .password("password456")
                .status(User.UserStatus.OCUPADO)
                .specialization("Frontend Developer")
                .skills(List.of("JavaScript", "React"))
                .build();
        
        userRepository.saveAll(List.of(user1, user2));
    }

    @Test
    @DisplayName("Debería encontrar un usuario por su email")
    void deberiaEncontrarUsuarioPorEmail() {
        // When: Se busca un usuario por un email existente
        Optional<User> foundUser = userRepository.findByEmail("juan.perez@example.com");

        // Then: Debería encontrar el usuario y el email debería coincidir
        assertTrue(foundUser.isPresent());
        assertEquals("juan.perez@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("No debería encontrar un usuario por un email que no existe")
    void noDeberiaEncontrarUsuarioPorEmailInexistente() {
        // When: Se busca un usuario por un email que no existe
        Optional<User> foundUser = userRepository.findByEmail("noexiste@example.com");

        // Then: No debería encontrar ningún usuario
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Debería encontrar usuarios disponibles")
    void deberiaEncontrarUsuariosDisponibles() {
        // When: Se buscan todos los usuarios con estado DISPONIBLE
        List<User> availableUsers = userRepository.findAvailableUsers();

        // Then: Debería encontrar solo un usuario (user1)
        assertEquals(1, availableUsers.size());
        assertEquals("juan.perez@example.com", availableUsers.get(0).getEmail());
    }

    @Test
    @DisplayName("Debería buscar usuarios por nombre, especialización o habilidad")
    void deberiaBuscarUsuariosPorQuery() {
        // When: Se buscan usuarios por diferentes términos
        List<User> foundByFirstName = userRepository.searchUsers("juan");
        List<User> foundBySpecialization = userRepository.searchUsers("Frontend");
        List<User> foundBySkill = userRepository.searchUsers("java");

        // Then: Las búsquedas deberían devolver los usuarios correctos
        assertEquals(1, foundByFirstName.size());
        assertEquals("juanperez", foundByFirstName.get(0).getUsername());

        assertEquals(1, foundBySpecialization.size());
        assertEquals("anagomez", foundBySpecialization.get(0).getUsername());

        assertEquals(1, foundBySkill.size());
        assertEquals("juanperez", foundBySkill.get(0).getUsername());
    }
}
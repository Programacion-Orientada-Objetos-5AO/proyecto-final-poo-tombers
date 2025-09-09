package ar.edu.huergo.tombers.repository;

import ar.edu.huergo.tombers.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Configura un entorno de test para JPA con una BD en memoria
@DisplayName("Tests de Repositorio - UserRepository")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // Ayuda a preparar los datos en la BD de prueba

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Debería encontrar un usuario por su email")
    void deberiaEncontrarUsuarioPorEmail() {
        // Given: Un usuario guardado en la base de datos de prueba
        User user = User.builder().email("test@example.com").username("testuser").password("pass").build();
        entityManager.persist(user);
        entityManager.flush(); // Sincroniza con la BD

        // When: Se busca el usuario por email
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Then: El usuario debería ser encontrado
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Debería verificar si un email existe")
    void deberiaVerificarSiEmailExiste() {
        // Given
        User user = User.builder().email("existe@example.com").username("existe").password("pass").build();
        entityManager.persist(user);
        entityManager.flush();
        
        // When & Then
        assertTrue(userRepository.existsByEmail("existe@example.com"));
        assertFalse(userRepository.existsByEmail("noexiste@example.com"));
    }

    @Test
    @DisplayName("Debería buscar usuarios por coincidencia en nombre, apellido o username")
    void deberiaBuscarUsuariosPorQuery() {
        // Given
        User user1 = User.builder().firstName("Juan").lastName("Perez").username("jperez").email("juan@a.com").password("p").build();
        User user2 = User.builder().firstName("Juana").lastName("Gomez").username("juana_g").email("juana@a.com").password("p").build();
        User user3 = User.builder().firstName("Pedro").lastName("Lopez").username("plopez").email("pedro@a.com").password("p").build();
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();
        
        // When
        List<User> result = userRepository.searchUsers("juan");

        // Then
        assertEquals(2, result.size()); // Debería encontrar a Juan y a Juana
    }
}
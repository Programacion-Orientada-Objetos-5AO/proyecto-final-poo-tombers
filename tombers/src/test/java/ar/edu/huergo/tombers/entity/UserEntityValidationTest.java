package ar.edu.huergo.tombers.entity;

import org.hibernate.exception.ConstraintViolationException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@DisplayName("Tests de Validación de Entidad - User")
class UserEntityValidationTest {

    @Autowired
    private TestEntityManager entityManager; // Proporciona métodos para interactuar con el contexto de persistencia en los tests

    @Test
    @DisplayName("Debería fallar al guardar un usuario con email nulo")
    void deberiaFallarConEmailNulo() {
        // Given: Un usuario con el campo 'email' nulo, que es obligatorio
        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email(null) // Campo nulo
                .username("testuser")
                .password("password")
                .build();

        // When & Then: Se espera que al intentar persistir, se lance una ConstraintViolationException
        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persistAndFlush(user); // persistAndFlush fuerza la sincronización con la BD y dispara las validaciones
        });
    }

    @Test
    @DisplayName("Debería fallar al guardar un usuario con username duplicado")
    void deberiaFallarConUsernameDuplicado() {
        // Given: Un usuario ya existente en la base de datos
        User userExistente = User.builder()
                .firstName("Juan")
                .lastName("Perez")
                .email("juan@example.com")
                .username("juanperez")
                .password("password")
                .build();
        entityManager.persistAndFlush(userExistente);

        // Y un nuevo usuario con el mismo 'username'
        User userNuevo = User.builder()
                .firstName("Pedro")
                .lastName("Gomez")
                .email("pedro@example.com")
                .username("juanperez") // Username duplicado
                .password("password")
                .build();
        
        // When & Then: Se espera una excepción de violación de constraint (unicidad)
        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persistAndFlush(userNuevo);
        });
    }
    
    @Test
    @DisplayName("Debería guardar exitosamente un usuario con todos los campos obligatorios")
    void deberiaGuardarUsuarioValido() {
        // Given: Un usuario con todos los campos obligatorios y válidos
        User user = User.builder()
                .firstName("Maria")
                .lastName("Lopez")
                .email("maria@example.com")
                .username("marialopez")
                .password("password")
                .build();

        // When: Se persiste el usuario
        User savedUser = entityManager.persistAndFlush(user);

        // Then: El usuario guardado debe tener un ID asignado por la base de datos
        assertTrue(savedUser.getId() > 0);
    }
}
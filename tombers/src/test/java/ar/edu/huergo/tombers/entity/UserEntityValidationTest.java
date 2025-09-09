package ar.edu.huergo.tombers.entity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de Validación - Entidad User")
class UserEntityValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        // Se inicializa el validador una sola vez para todas las pruebas
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería ser válido con todos los campos requeridos correctos")
    void deberiaSerValido() {
        // Given: Un usuario con todos los campos obligatorios
        User user = User.builder()
                .firstName("Juan")
                .lastName("Perez")
                .email("juan.perez@example.com")
                .username("jperez")
                .password("password123")
                .build();

        // When: Se valida la entidad
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then: No debería haber ninguna violación de las restricciones
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Debería ser inválido si el email es nulo o no tiene formato correcto")
    void deberiaSerInvalidoPorEmail() {
        // Given: Un usuario con email nulo
        User userSinEmail = User.builder().username("user1").password("pass").build();
        // Given: Un usuario con email en formato incorrecto
        User userConEmailInvalido = User.builder().email("email-invalido").username("user2").password("pass").build();
        
        // When
        Set<ConstraintViolation<User>> violations1 = validator.validate(userSinEmail);
        Set<ConstraintViolation<User>> violations2 = validator.validate(userConEmailInvalido);

        // Then
        assertFalse(violations1.isEmpty());
        assertEquals(1, violations1.size()); // Esperamos 1 error: email no puede ser nulo
        
        assertFalse(violations2.isEmpty());
        assertEquals(1, violations2.size()); // Esperamos 1 error: formato de email inválido
    }

    @Test
    @DisplayName("Debería ser inválido si el username es nulo o está en blanco")
    void deberiaSerInvalidoPorUsername() {
        // Given: Un usuario con username en blanco
        User user = User.builder()
                .email("test@example.com")
                .username(" ") // Username en blanco
                .password("password123")
                .build();
                
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size()); // Esperamos 1 error
    }
}
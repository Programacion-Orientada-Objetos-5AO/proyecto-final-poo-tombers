package ar.edu.huergo.tombers.dto.user;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de Validación - DTO CreateProfileRequest")
public class CreateProfileRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería fallar la validación si el rol está en blanco")
    void deberiaFallarConRolEnBlanco() {
        // Given
        CreateProfileRequest request = new CreateProfileRequest();
        request.setEmail("test@example.com");
        request.setUsername("testuser");
        request.setRole(""); // Rol en blanco
        
        // When
        Set<ConstraintViolation<CreateProfileRequest>> violations = validator.validate(request);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("role")));
    }
}
package ar.edu.huergo.tombers.dto.auth;

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

@DisplayName("Tests de Validación - DTO LoginRequest")
public class LoginRequestValidationTest {
    
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería fallar la validación si el email está en blanco")
    void deberiaFallarConEmailEnBlanco() {
        // Given
        LoginRequest request = new LoginRequest("", "password");
        
        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }
    
    @Test
    @DisplayName("Debería fallar la validación si la contraseña está en blanco")
    void deberiaFallarConPasswordEnBlanco() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "");
        
        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }
}
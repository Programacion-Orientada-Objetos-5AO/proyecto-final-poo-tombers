package ar.edu.huergo.tombers.dto.auth;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de Validación - DTO RegisterRequest")
class RegisterRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        // Se inicializa el validador de Jakarta Validation
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería validar un RegisterRequest correcto sin errores")
    void deberiaValidarRequestCorrecto() {
        // Given: Un DTO con todos los campos válidos
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Juan")
                .lastName("Perez")
                .email("juan.perez@example.com")
                .username("juanperez")
                .password("Password123@Valid")
                .build();

        // When: Se validan las restricciones del DTO
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then: No debería haber ninguna violación
        assertTrue(violations.isEmpty(), "Un request válido no debería tener violaciones");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "A"})
    @DisplayName("Debería fallar la validación con un firstName inválido (corto, vacío o en blanco)")
    void deberiaFallarConFirstNameInvalido(String firstName) {
        // Given: Un DTO con un nombre inválido
        RegisterRequest request = RegisterRequest.builder()
                .firstName(firstName)
                .lastName("Perez")
                .email("juan.perez@example.com")
                .username("juanperez")
                .password("Password123@Valid")
                .build();

        // When: Se valida el DTO
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then: Debería haber una violación para el campo 'firstName'
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
    }

    @Test
    @DisplayName("Debería fallar la validación con un email con formato incorrecto")
    void deberiaFallarConEmailInvalido() {
        // Given: Un DTO con un email inválido
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Juan")
                .lastName("Perez")
                .email("email-invalido")
                .username("juanperez")
                .password("Password123@Valid")
                .build();

        // When: Se valida el DTO
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then: Debería haber una violación para el campo 'email'
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email") && v.getMessage().contains("formato del email no es válido")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"pass", "password123", "PASSWORD123@", "PasswordSinNumero@"})
    @DisplayName("Debería fallar la validación con una contraseña que no cumple los requisitos de seguridad")
    void deberiaFallarConPasswordInvalida(String password) {
        // Given: Un DTO con una contraseña inválida
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Juan")
                .lastName("Perez")
                .email("juan.perez@example.com")
                .username("juanperez")
                .password(password)
                .build();

        // When: Se valida el DTO
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then: Debería haber una violación para el campo 'password'
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }
}
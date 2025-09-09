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


@DisplayName("Tests de Validación - Entidad Project")
class ProjectEntityValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería ser válido con un título y descripción correctos")
    void deberiaSerValido() {
        // Given: Un proyecto con todos sus campos requeridos
        Project project = Project.builder()
                .title("Proyecto Válido")
                .description("Esta es una descripción válida.")
                .build();

        // When: Se valida la entidad
        Set<ConstraintViolation<Project>> violations = validator.validate(project);

        // Then: No debería haber errores de validación
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Debería ser inválido si el título está en blanco")
    void deberiaSerInvalidoPorTituloEnBlanco() {
        // Given: Un proyecto con el título vacío
        Project project = Project.builder()
                .title("") // Título en blanco
                .description("Descripción del proyecto.")
                .build();

        // When
        Set<ConstraintViolation<Project>> violations = validator.validate(project);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size()); // Esperamos un error de validación para el título
    }
    
    @Test
    @DisplayName("Debería ser inválido si la descripción es nula")
    void deberiaSerInvalidoPorDescripcionNula() {
        // Given: Un proyecto con la descripción nula
        Project project = Project.builder()
                .title("Título del Proyecto")
                .description(null) // Descripción nula
                .build();
    
        // When
        Set<ConstraintViolation<Project>> violations = validator.validate(project);
    
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size()); // Esperamos un error de validación
    }
}
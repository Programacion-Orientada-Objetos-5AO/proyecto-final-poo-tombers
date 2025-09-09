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
@DisplayName("Tests de Validación de Entidad - Project")
class ProjectEntityValidationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debería fallar al guardar un proyecto con título nulo")
    void deberiaFallarConTituloNulo() {
        // Given: Un proyecto con el título nulo, que es un campo obligatorio
        Project project = Project.builder()
                .title(null) // Campo nulo
                .description("Una descripción válida.")
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        // When & Then: Se espera una excepción de violación de constraint al intentar persistir
        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persistAndFlush(project);
        });
    }
    
    @Test
    @DisplayName("Debería fallar al guardar un proyecto con descripción nula")
    void deberiaFallarConDescripcionNula() {
        // Given: Un proyecto con la descripción nula, que es obligatoria
        Project project = Project.builder()
                .title("Título Válido")
                .description(null) // Campo nulo
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        // When & Then: Se espera una excepción al persistir
        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persistAndFlush(project);
        });
    }

    @Test
    @DisplayName("Debería guardar exitosamente un proyecto con todos los campos obligatorios")
    void deberiaGuardarProyectoValido() {
        // Given: Un proyecto con todos sus campos obligatorios correctamente establecidos
        Project project = Project.builder()
                .title("Proyecto Válido")
                .description("Esta es una descripción completa y válida para el proyecto.")
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        // When: Se persiste el proyecto en la base de datos de prueba
        Project savedProject = entityManager.persistAndFlush(project);

        // Then: El proyecto guardado debe tener un ID generado, lo que confirma la persistencia exitosa
        assertTrue(savedProject.getId() > 0);
    }
}
package ar.edu.huergo.tombers.repository;

import ar.edu.huergo.tombers.entity.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DisplayName("Tests de Repositorio - ProjectRepository")
class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @DisplayName("Debería guardar y encontrar un proyecto por ID")
    void deberiaGuardarYEncontrarProyecto() {
        // Given
        Project project = Project.builder().title("Nuevo Proyecto").description("Desc...").build();
        Project savedProject = entityManager.persistAndFlush(project);

        // When
        Project foundProject = projectRepository.findById(savedProject.getId()).orElse(null);

        // Then
        assertNotNull(foundProject);
        assertEquals("Nuevo Proyecto", foundProject.getTitle());
    }

    @Test
    @DisplayName("Debería buscar proyectos por coincidencia en el título")
    void deberiaBuscarProyectosPorTitulo() {
        // Given
        entityManager.persist(Project.builder().title("Proyecto de Java").description("...").build());
        entityManager.persist(Project.builder().title("Proyecto de Spring").description("...").build());
        entityManager.persist(Project.builder().title("Otro Tema").description("...").build());
        entityManager.flush();

        // When
        List<Project> result = projectRepository.searchProjects("proyecto");

        // Then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Debería encontrar solo proyectos activos y ordenarlos por fecha de creación")
    void deberiaEncontrarProyectosActivos() {
        // Given: Proyectos con diferentes estados
        // CORRECCIÓN: Usamos los valores correctos del enum, probablemente 'Activo' e 'Inactivo'
        Project p1 = Project.builder().title("P1").description("d").status(Project.ProjectStatus.valueOf("ACTIVO")).build();
        Project p2 = Project.builder().title("P2").description("d").status(Project.ProjectStatus.valueOf("INACTIVO")).build();
        Project p3 = Project.builder().title("P3").description("d").status(Project.ProjectStatus.valueOf("ACTIVO")).build();
        
        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.persist(p3);
        entityManager.flush();
        
        // When: Se llama al método del repositorio que busca proyectos activos
        List<Project> activeProjects = projectRepository.findActiveProjectsOrderByCreatedAt();
        
        // Then: Debería devolver solo los dos proyectos activos
        assertEquals(2, activeProjects.size());
        assertEquals("P1", activeProjects.get(0).getTitle());
        assertEquals("P3", activeProjects.get(1).getTitle());
    }
}
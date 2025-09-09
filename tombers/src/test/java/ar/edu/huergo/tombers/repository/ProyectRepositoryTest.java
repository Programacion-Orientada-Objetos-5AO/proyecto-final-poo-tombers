package ar.edu.huergo.tombers.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ar.edu.huergo.tombers.entity.Project;

@DataJpaTest
@DisplayName("Tests de Repositorio - ProjectRepository")
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        // Limpiamos la base de datos antes de cada test
        projectRepository.deleteAll();

        // Given: Creamos datos de prueba
        Project project1 = Project.builder()
                .title("Proyecto Alpha")
                .description("Descripción del proyecto Alpha con Java.")
                .status(Project.ProjectStatus.ACTIVE)
                .progress(50)
                .skillsNeeded(List.of("Java", "Spring"))
                .build();

        Project project2 = Project.builder()
                .title("Proyecto Beta")
                .description("Descripción del proyecto Beta.")
                .status(Project.ProjectStatus.INACTIVE)
                .progress(100)
                .skillsNeeded(List.of("React", "JavaScript"))
                .build();

        Project project3 = Project.builder()
                .title("Proyecto Gamma (Java)")
                .description("Otro proyecto activo.")
                .status(Project.ProjectStatus.ACTIVE)
                .progress(25)
                .skillsNeeded(List.of("Java", "Docker"))
                .build();

        projectRepository.saveAll(List.of(project1, project2, project3));
    }

    @Test
    @DisplayName("Debería encontrar proyectos por status")
    void deberiaEncontrarProyectosPorStatus() {
        // When: Buscamos proyectos con estado ACTIVE
        List<Project> activeProjects = projectRepository.findByStatus(Project.ProjectStatus.ACTIVE);

        // Then: Deberíamos encontrar 2 proyectos activos
        assertEquals(2, activeProjects.size());
    }

    @Test
    @DisplayName("Debería buscar proyectos por título, descripción o habilidad")
    void deberiaBuscarProyectosPorQuery() {
        // When: Realizamos búsquedas por diferentes términos
        List<Project> foundByTitle = projectRepository.searchProjects("Alpha");
        List<Project> foundByDescription = projectRepository.searchProjects("activo");
        List<Project> foundBySkill = projectRepository.searchProjects("React");
        List<Project> foundByCommonSkill = projectRepository.searchProjects("Java");

        // Then: Las búsquedas deberían devolver los resultados correctos
        assertEquals(1, foundByTitle.size());
        assertEquals("Proyecto Alpha", foundByTitle.get(0).getTitle());

        assertEquals(1, foundByDescription.size());
        assertEquals("Proyecto Gamma (Java)", foundByDescription.get(0).getTitle());
        
        assertEquals(1, foundBySkill.size());
        assertEquals("Proyecto Beta", foundBySkill.get(0).getTitle());

        assertEquals(2, foundByCommonSkill.size());
    }

    @Test
    @DisplayName("Debería encontrar proyectos incompletos ordenados por progreso")
    void deberiaEncontrarProyectosIncompletos() {
        // When: Buscamos proyectos con progreso menor a 100
        List<Project> incompleteProjects = projectRepository.findIncompleteProjectsOrderByProgress();

        // Then: Deberíamos encontrar 2 proyectos incompletos, ordenados descendentemente por progreso
        assertEquals(2, incompleteProjects.size());
        assertEquals(50, incompleteProjects.get(0).getProgress()); // Proyecto Alpha
        assertEquals(25, incompleteProjects.get(1).getProgress()); // Proyecto Gamma
    }
}
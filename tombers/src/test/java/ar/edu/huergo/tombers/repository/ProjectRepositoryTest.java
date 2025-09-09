package ar.edu.huergo.tombers.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

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

    private Project newProject(String title, String desc, int progress, Project.ProjectStatus status) {
        Project p = Project.builder()
                .title(title)
                .description(desc)
                .progress(progress)
                .status(status)
                .skillsNeeded(List.of("java", "spring"))
                .build();
        p.setCreatedAt(LocalDate.now());
        p.setUpdatedAt(LocalDate.now());
        return p;
    }

    @Test
    @DisplayName("findByStatus, findActiveProjectsOrderByCreatedAt y findIncompleteProjectsOrderByProgress")
    void queriesByStatusAndOrder() {
        // Given
        Project p1 = newProject("A", "desc A", 50, Project.ProjectStatus.ACTIVE);
        p1.setCreatedAt(LocalDate.now().minusDays(1));
        Project p2 = newProject("B", "desc B", 80, Project.ProjectStatus.ACTIVE);
        p2.setCreatedAt(LocalDate.now());
        Project p3 = newProject("C", "desc C", 100, Project.ProjectStatus.INACTIVE);
        projectRepository.saveAll(List.of(p1, p2, p3));

        // When - Then
        var actives = projectRepository.findByStatus(Project.ProjectStatus.ACTIVE);
        assertEquals(2, actives.size());

        var orderedByCreated = projectRepository.findActiveProjectsOrderByCreatedAt();
        assertEquals(2, orderedByCreated.size());
        // Validamos orden por fecha de creación (desc)
        assertTrue(!orderedByCreated.get(0).getCreatedAt().isBefore(orderedByCreated.get(1).getCreatedAt()));

        var incompletes = projectRepository.findIncompleteProjectsOrderByProgress();
        assertEquals(List.of(p2.getTitle(), p1.getTitle()),
                incompletes.stream().map(Project::getTitle).toList());
    }

    @Test
    @DisplayName("searchProjects busca por título, descripción, tecnologías/skills")
    void searchProjects() {
        // Given
        Project p = newProject("Proyecto Java", "Un proyecto con Spring", 10, Project.ProjectStatus.ACTIVE);
        projectRepository.save(p);

        // When - Then
        assertFalse(projectRepository.searchProjects("java").isEmpty());
        assertFalse(projectRepository.searchProjects("spring").isEmpty());
        assertTrue(projectRepository.searchProjects("no existe").isEmpty());
    }
}

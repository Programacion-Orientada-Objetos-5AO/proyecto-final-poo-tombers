package ar.edu.huergo.tombers.repository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.tombers.entity.Project;

@DataJpaTest
public class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    private Project project;

    @BeforeEach
    public void setup() {
        project = Project.builder()
                .title("Test Project")
                .description("Test Description")
                .status(Project.ProjectStatus.ACTIVE)
                .createdAt(LocalDate.now())
                .build();
        entityManager.persist(project);
        entityManager.flush();
    }

    @Test
    public void testFindByStatus() {
        List<Project> projects = projectRepository.findByStatus(Project.ProjectStatus.ACTIVE);
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertEquals(Project.ProjectStatus.ACTIVE, projects.get(0).getStatus());
    }

    @Test
    public void testSearchProjects() {
        List<Project> projects = projectRepository.searchProjects("Test");
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.get(0).getTitle().contains("Test") || projects.get(0).getDescription().contains("Test"));
    }

    @Test
    public void testFindActiveProjectsOrderByCreatedAt() {
        List<Project> projects = projectRepository.findActiveProjectsOrderByCreatedAt();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertEquals(Project.ProjectStatus.ACTIVE, projects.get(0).getStatus());
    }

    @Test
    public void testFindIncompleteProjectsOrderByProgress() {
        List<Project> projects = projectRepository.findIncompleteProjectsOrderByProgress();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.get(0).getProgress() == null || projects.get(0).getProgress() < 100);
    }
}

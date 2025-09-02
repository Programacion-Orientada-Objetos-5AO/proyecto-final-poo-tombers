package ar.edu.huergo.tombers.repository;

import ar.edu.huergo.tombers.entity.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void testSaveProject() {
        Project project = Project.builder()
                .title("Test Project")
                .description("A test project")
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        Project savedProject = projectRepository.save(project);

        assertThat(savedProject.getId()).isNotNull();
        assertThat(savedProject.getTitle()).isEqualTo("Test Project");
    }

    @Test
    void testFindByStatus() {
        Project project1 = Project.builder()
                .title("Active Project")
                .description("Active")
                .status(Project.ProjectStatus.ACTIVE)
                .build();
        Project project2 = Project.builder()
                .title("Inactive Project")
                .description("Inactive")
                .status(Project.ProjectStatus.INACTIVE)
                .build();
        projectRepository.save(project1);
        projectRepository.save(project2);

        List<Project> activeProjects = projectRepository.findByStatus(Project.ProjectStatus.ACTIVE);

        assertThat(activeProjects).hasSize(1);
        assertThat(activeProjects.get(0).getTitle()).isEqualTo("Active Project");
    }

    @Test
    void testSearchProjects() {
        Project project = Project.builder()
                .title("Java Project")
                .description("A project using Java")
                .status(Project.ProjectStatus.ACTIVE)
                .build();
        projectRepository.save(project);

        List<Project> searchResults = projectRepository.searchProjects("Java");

        assertThat(searchResults).hasSize(1);
        assertThat(searchResults.get(0).getTitle()).isEqualTo("Java Project");
    }

    @Test
    void testFindActiveProjectsOrderByCreatedAt() {
        Project project1 = Project.builder()
                .title("First Project")
                .description("First")
                .status(Project.ProjectStatus.ACTIVE)
                .build();
        Project project2 = Project.builder()
                .title("Second Project")
                .description("Second")
                .status(Project.ProjectStatus.ACTIVE)
                .build();
        projectRepository.save(project1);
        projectRepository.save(project2);

        List<Project> activeProjects = projectRepository.findActiveProjectsOrderByCreatedAt();

        assertThat(activeProjects).hasSize(2);
        // Assuming order by createdAt desc, but since same time, order may vary
    }

    @Test
    void testFindIncompleteProjectsOrderByProgress() {
        Project project1 = Project.builder()
                .title("Incomplete 1")
                .description("Incomplete")
                .status(Project.ProjectStatus.ACTIVE)
                .progress(50)
                .build();
        Project project2 = Project.builder()
                .title("Incomplete 2")
                .description("Incomplete")
                .status(Project.ProjectStatus.ACTIVE)
                .progress(30)
                .build();
        projectRepository.save(project1);
        projectRepository.save(project2);

        List<Project> incompleteProjects = projectRepository.findIncompleteProjectsOrderByProgress();

        assertThat(incompleteProjects).hasSize(2);
        assertThat(incompleteProjects.get(0).getProgress()).isEqualTo(50);
    }
}

package ar.edu.huergo.tombers.repository;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ar.edu.huergo.tombers.entity.Project;
import ar.edu.huergo.tombers.entity.Skill;

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
                .skillsNeeded(List.of(Skill.builder().nombre("Python").nivel("Principiante").build() , Skill.builder().nombre("Java").nivel("Avanzado").build()))
                .build();
        p.setCreatedAt(LocalDate.now());
        p.setUpdatedAt(LocalDate.now());
        return p;
    }

}

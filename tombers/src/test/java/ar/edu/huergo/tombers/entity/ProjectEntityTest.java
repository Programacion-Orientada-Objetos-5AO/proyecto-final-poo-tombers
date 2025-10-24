package ar.edu.huergo.tombers.entity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de Entidad - Project")
public class ProjectEntityTest {

    @Test
    @DisplayName("Campos se guardan en la entidad correctamente")
    public void fieldsWorking() {
        Project p = Project.builder()
                .title("Proyecto")
                .description("desc")
                .teamCurrent(2)
                .teamMax(5)
                .duration("3m")
                .language("ES")
                .type("OSS")
                .technologies(List.of("Java"))
                .objectives(List.of("Crear API"))
                .skillsNeeded(List.of(Skill.builder().nombre("Python").nivel("Principiante").build()))
                .status(Project.ProjectStatus.ACTIVE)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        assertEquals(2, p.getTeamCurrent());
        assertEquals("Java", p.getTechnologies().get(0));
        assertEquals("Crear API", p.getObjectives().get(0));
        assertEquals(Project.ProjectStatus.ACTIVE, p.getStatus());
    }
}


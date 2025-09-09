package ar.edu.huergo.tombers.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de Entidad - Project")
class ProjectEntityTest {

    @Test
    @DisplayName("Embeddables se guardan en la entidad correctamente")
    void embeddablesWorking() {
        Project.ProjectStats stats = Project.ProjectStats.builder()
                .teamCurrent(2)
                .teamMax(5)
                .duration("3m")
                .language("ES")
                .type("OSS")
                .build();

        Project.Technology tech = Project.Technology.builder().name("Java").icon("java.svg").build();
        Project.Objective obj = Project.Objective.builder()
                .text("Crear API")
                .status(Project.Objective.ObjectiveStatus.IN_PROGRESS)
                .icon("api.svg").build();

        Project p = Project.builder()
                .title("Proyecto")
                .description("desc")
                .stats(stats)
                .technologies(List.of(tech))
                .objectives(List.of(obj))
                .skillsNeeded(List.of("spring"))
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        assertEquals(2, p.getStats().getTeamCurrent());
        assertEquals("Java", p.getTechnologies().get(0).getName());
        assertEquals(Project.Objective.ObjectiveStatus.IN_PROGRESS, p.getObjectives().get(0).getStatus());
        assertEquals(Project.ProjectStatus.ACTIVE, p.getStatus());
    }
}


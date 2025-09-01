package ar.edu.huergo.tombers.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ar.edu.huergo.tombers.dto.project.ProjectCreateRequest;
import ar.edu.huergo.tombers.dto.project.ProjectResponse;
import ar.edu.huergo.tombers.entity.Project;

public class ProjectMapperTest {

    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    private Project project;
    private ProjectResponse projectResponse;
    private ProjectCreateRequest createRequest;

    @BeforeEach
    public void setup() {
        project = Project.builder()
                .id(1L)
                .title("Test Project")
                .description("Test Description")
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        projectResponse = ProjectResponse.builder()
                .id(1L)
                .title("Test Project")
                .description("Test Description")
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        createRequest = new ProjectCreateRequest();
        createRequest.setTitle("Test Project");
        createRequest.setDescription("Test Description");
        createRequest.setStatus(ProjectCreateRequest.ProjectStatus.ACTIVE);
    }

    @Test
    public void testToEntity() {
        Project result = projectMapper.toEntity(createRequest);

        assertNotNull(result);
        assertNull(result.getId()); // Should be ignored
        assertEquals(createRequest.getTitle(), result.getTitle());
        assertEquals(createRequest.getDescription(), result.getDescription());
        assertEquals(Project.ProjectStatus.ACTIVE, result.getStatus()); // Constant mapping
    }

    @Test
    public void testToResponse() {
        ProjectResponse result = projectMapper.toResponse(project);

        assertNotNull(result);
        assertEquals(project.getId(), result.getId());
        assertEquals(project.getTitle(), result.getTitle());
        assertEquals(project.getDescription(), result.getDescription());
        assertEquals(project.getStatus(), result.getStatus());
    }

    @Test
    public void testUpdateEntity() {
        Project target = new Project();
        target.setId(1L);
        target.setTitle("Original Title");
        target.setDescription("Original Description");

        projectMapper.updateEntity(target, createRequest);

        assertEquals("Test Project", target.getTitle());
        assertEquals("Test Description", target.getDescription());
        assertEquals(1L, target.getId()); // Should not be updated
    }
}

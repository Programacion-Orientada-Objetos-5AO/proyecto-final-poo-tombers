package ar.edu.huergo.tombers.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import ar.edu.huergo.tombers.dto.project.ProjectCreateRequest;
import ar.edu.huergo.tombers.dto.project.ProjectResponse;
import ar.edu.huergo.tombers.entity.Project;
import ar.edu.huergo.tombers.mapper.ProjectMapper;
import ar.edu.huergo.tombers.repository.ProjectRepository;

public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectResponse projectResponse;
    private ProjectCreateRequest createRequest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

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
    public void testGetAllProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        List<ProjectResponse> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectRepository).findAll();
        verify(projectMapper).toResponse(project);
    }

    @Test
    public void testGetProjectById_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        ProjectResponse result = projectService.getProjectById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(projectRepository).findById(1L);
        verify(projectMapper).toResponse(project);
    }

    @Test
    public void testGetProjectById_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.getProjectById(1L);
        });

        assertEquals("Proyecto no encontrado", exception.getMessage());
    }

    @Test
    public void testCreateProject() {
        when(projectMapper.toEntity(createRequest)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        ProjectResponse result = projectService.createProject(createRequest);

        assertNotNull(result);
        verify(projectMapper).toEntity(createRequest);
        verify(projectRepository).save(any(Project.class));
        verify(projectMapper).toResponse(project);
    }

    @Test
    public void testUpdateProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        ProjectResponse result = projectService.updateProject(1L, createRequest);

        assertNotNull(result);
        verify(projectRepository).findById(1L);
        verify(projectMapper).updateEntity(project, createRequest);
        verify(projectRepository).save(project);
        verify(projectMapper).toResponse(project);
    }

    @Test
    public void testDeleteProject_Success() {
        when(projectRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> projectService.deleteProject(1L));
        verify(projectRepository).deleteById(1L);
    }

    @Test
    public void testDeleteProject_NotFound() {
        when(projectRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.deleteProject(1L);
        });

        assertEquals("Proyecto no encontrado", exception.getMessage());
    }

    @Test
    public void testSearchProjects() {
        when(projectRepository.searchProjects("test")).thenReturn(List.of(project));
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        List<ProjectResponse> result = projectService.searchProjects("test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectRepository).searchProjects("test");
        verify(projectMapper).toResponse(project);
    }

    @Test
    public void testGetActiveProjects() {
        when(projectRepository.findActiveProjectsOrderByCreatedAt()).thenReturn(List.of(project));
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        List<ProjectResponse> result = projectService.getActiveProjects();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectRepository).findActiveProjectsOrderByCreatedAt();
        verify(projectMapper).toResponse(project);
    }

    @Test
    public void testGetIncompleteProjects() {
        when(projectRepository.findIncompleteProjectsOrderByProgress()).thenReturn(List.of(project));
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        List<ProjectResponse> result = projectService.getIncompleteProjects();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectRepository).findIncompleteProjectsOrderByProgress();
        verify(projectMapper).toResponse(project);
    }
}

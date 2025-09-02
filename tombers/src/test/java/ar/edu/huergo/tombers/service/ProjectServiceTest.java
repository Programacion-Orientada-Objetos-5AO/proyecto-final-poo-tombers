package ar.edu.huergo.tombers.service;

import ar.edu.huergo.tombers.dto.project.ProjectCreateRequest;
import ar.edu.huergo.tombers.dto.project.ProjectResponse;
import ar.edu.huergo.tombers.entity.Project;
import ar.edu.huergo.tombers.mapper.ProjectMapper;
import ar.edu.huergo.tombers.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void testGetAllProjects() {
        Project project = Project.builder().title("Test Project").build();
        ProjectResponse response = ProjectResponse.builder().title("Test Project").build();

        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toResponse(project)).thenReturn(response);

        List<ProjectResponse> results = projectService.getAllProjects();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Test Project");
    }

    @Test
    void testGetProjectById() {
        Long id = 1L;
        Project project = Project.builder().id(id).title("Test Project").build();
        ProjectResponse response = ProjectResponse.builder().id(id).title("Test Project").build();

        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(projectMapper.toResponse(project)).thenReturn(response);

        ProjectResponse result = projectService.getProjectById(id);

        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void testGetProjectByIdNotFound() {
        Long id = 1L;

        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Proyecto no encontrado");
    }

    @Test
    void testCreateProject() {
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setTitle("New Project");
        Project project = Project.builder().title("New Project").build();
        Project savedProject = Project.builder().id(1L).title("New Project").build();
        ProjectResponse response = ProjectResponse.builder().id(1L).title("New Project").build();

        when(projectMapper.toEntity(request)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        when(projectMapper.toResponse(savedProject)).thenReturn(response);

        ProjectResponse result = projectService.createProject(request);

        assertThat(result.getTitle()).isEqualTo("New Project");
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testUpdateProject() {
        Long id = 1L;
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setTitle("Updated Project");
        Project project = Project.builder().id(id).title("Old Project").build();
        Project updatedProject = Project.builder().id(id).title("Updated Project").build();
        ProjectResponse response = ProjectResponse.builder().id(id).title("Updated Project").build();

        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);
        when(projectMapper.toResponse(updatedProject)).thenReturn(response);

        ProjectResponse result = projectService.updateProject(id, request);

        assertThat(result.getTitle()).isEqualTo("Updated Project");
        verify(projectMapper).updateEntity(project, request);
    }

    @Test
    void testDeleteProject() {
        Long id = 1L;

        when(projectRepository.existsById(id)).thenReturn(true);

        projectService.deleteProject(id);

        verify(projectRepository).deleteById(id);
    }

    @Test
    void testDeleteProjectNotFound() {
        Long id = 1L;

        when(projectRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> projectService.deleteProject(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Proyecto no encontrado");
    }

    @Test
    void testSearchProjects() {
        String query = "Java";
        Project project = Project.builder().title("Java Project").build();
        ProjectResponse response = ProjectResponse.builder().title("Java Project").build();

        when(projectRepository.searchProjects(query)).thenReturn(List.of(project));
        when(projectMapper.toResponse(project)).thenReturn(response);

        List<ProjectResponse> results = projectService.searchProjects(query);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Java Project");
    }
}

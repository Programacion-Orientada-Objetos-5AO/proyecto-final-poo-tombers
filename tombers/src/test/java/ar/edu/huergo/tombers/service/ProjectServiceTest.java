package ar.edu.huergo.tombers.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.huergo.tombers.dto.project.ProjectCreateRequest;
import ar.edu.huergo.tombers.dto.project.ProjectResponse;
import ar.edu.huergo.tombers.entity.Project;
import ar.edu.huergo.tombers.mapper.ProjectMapper;
import ar.edu.huergo.tombers.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Servicio - ProjectService")
class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectMapper projectMapper;
    @InjectMocks private ProjectService projectService;

    private Project project(Long id, String title) {
        Project p = Project.builder().id(id).title(title).description("desc").status(Project.ProjectStatus.ACTIVE).build();
        p.setCreatedAt(LocalDate.now());
        p.setUpdatedAt(LocalDate.now());
        return p;
    }

    @Test
    @DisplayName("getAllProjects mapea lista")
    void getAllProjects() {
        var p = project(1L, "A");
        when(projectRepository.findAll()).thenReturn(List.of(p));
        when(projectMapper.toResponse(p)).thenReturn(ProjectResponse.builder().title("A").build());

        var list = projectService.getAllProjects();
        assertEquals(1, list.size());
        assertEquals("A", list.get(0).getTitle());
    }

    @Test
    @DisplayName("getProjectById ok y not found")
    void getProjectById() {
        var p = project(2L, "B");
        when(projectRepository.findById(2L)).thenReturn(Optional.of(p));
        when(projectMapper.toResponse(p)).thenReturn(ProjectResponse.builder().title("B").build());
        assertEquals("B", projectService.getProjectById(2L).getTitle());

        when(projectRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(3L));
    }

    @Test
    @DisplayName("createProject usa mapper y guarda")
    void createProject() {
        var req = new ProjectCreateRequest();
        var entity = project(null, "X");
        when(projectMapper.toEntity(req)).thenReturn(entity);
        when(projectRepository.save(entity)).thenReturn(entity);
        when(projectMapper.toResponse(entity)).thenReturn(ProjectResponse.builder().title("X").build());

        var dto = projectService.createProject(req);
        assertEquals("X", dto.getTitle());
        verify(projectRepository).save(entity);
    }

    @Test
    @DisplayName("updateProject actualiza y guarda, y falla si no existe")
    void updateProject() {
        var existing = project(5L, "Old");
        var req = new ProjectCreateRequest();
        when(projectRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(projectRepository.save(existing)).thenReturn(existing);
        when(projectMapper.toResponse(existing)).thenReturn(ProjectResponse.builder().title("Old").build());

        var dto = projectService.updateProject(5L, req);
        verify(projectMapper).updateEntity(existing, req);
        verify(projectRepository).save(existing);
        assertEquals("Old", dto.getTitle());

        when(projectRepository.findById(6L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> projectService.updateProject(6L, req));
    }

    @Test
    @DisplayName("deleteProject elimina si existe y lanza si no")
    void deleteProject() {
        when(projectRepository.existsById(1L)).thenReturn(true);
        projectService.deleteProject(1L);
        verify(projectRepository).deleteById(1L);

        when(projectRepository.existsById(2L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> projectService.deleteProject(2L));
    }
}


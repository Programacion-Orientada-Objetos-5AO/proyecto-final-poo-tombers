package ar.edu.huergo.tombers.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    @Test
    @DisplayName("Debería obtener un proyecto por ID exitosamente")
    void deberiaObtenerProyectoPorId() {
        // Given: Un ID de proyecto y un proyecto simulado en el repositorio
        Long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);
        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        // When: Se llama al método para obtener el proyecto
        ProjectResponse result = projectService.getProjectById(projectId);

        // Then: El resultado no debe ser nulo y el ID debe coincidir
        assertNotNull(result);
        assertEquals(projectId, result.getId());
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException si el proyecto no existe al buscar por ID")
    void deberiaLanzarExcepcionSiProyectoNoExiste() {
        // Given: Un ID de un proyecto que no existe
        Long projectId = 99L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then: Se espera que se lance una EntityNotFoundException
        assertThrows(EntityNotFoundException.class, () -> {
            projectService.getProjectById(projectId);
        });
    }

    @Test
    @DisplayName("Debería crear un nuevo proyecto correctamente")
    void deberiaCrearProyecto() {
        // Given: Una solicitud para crear un proyecto
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setTitle("Nuevo Proyecto");

        Project projectEntity = new Project();
        projectEntity.setTitle("Nuevo Proyecto");

        Project savedProject = new Project();
        savedProject.setId(1L);
        savedProject.setTitle("Nuevo Proyecto");

        ProjectResponse responseDto = new ProjectResponse();
        responseDto.setId(1L);
        responseDto.setTitle("Nuevo Proyecto");

        when(projectMapper.toEntity(request)).thenReturn(projectEntity);
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        when(projectMapper.toResponse(savedProject)).thenReturn(responseDto);

        // When: Se llama al método para crear el proyecto
        ProjectResponse result = projectService.createProject(request);

        // Then: El resultado no debe ser nulo y debe tener un ID asignado
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Nuevo Proyecto", result.getTitle());
    }
    
    @Test
    @DisplayName("Debería eliminar un proyecto existente")
    void deberiaEliminarProyecto() {
        // Given: Un ID de proyecto existente
        Long projectId = 1L;
        when(projectRepository.existsById(projectId)).thenReturn(true);
        doNothing().when(projectRepository).deleteById(projectId);

        // When: Se llama al método para eliminar el proyecto
        projectService.deleteProject(projectId);

        // Then: Se verifica que el método deleteById del repositorio haya sido llamado una vez
        verify(projectRepository, times(1)).deleteById(projectId);
    }
}
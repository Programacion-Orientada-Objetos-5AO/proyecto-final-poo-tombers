package ar.edu.huergo.tombers.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.huergo.tombers.dto.project.ProjectCreateRequest;
import ar.edu.huergo.tombers.dto.project.ProjectResponse;
import ar.edu.huergo.tombers.entity.Project;
import ar.edu.huergo.tombers.mapper.ProjectMapper;
import ar.edu.huergo.tombers.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    /**
     * Obtiene una lista de todos los proyectos disponibles en el sistema.
     *
     * @return una lista de objetos ProjectResponse que representan todos los proyectos
     */
    public List<ProjectResponse> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    /**
     * Obtiene un proyecto específico por su identificador.
     *
     * @param id el identificador del proyecto
     * @return un objeto ProjectResponse que representa el proyecto encontrado
     * @throws EntityNotFoundException si el proyecto no existe
     */
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));
        return projectMapper.toResponse(project);
    }

    /**
     * Crea un nuevo proyecto basado en la solicitud proporcionada.
     *
     * @param request la solicitud de creación del proyecto
     * @return un objeto ProjectResponse que representa el proyecto creado
     */
    public ProjectResponse createProject(ProjectCreateRequest request) {
        Project project = projectMapper.toEntity(request);
        project.setCreatedAt(LocalDate.now());
        project.setUpdatedAt(LocalDate.now());
        
        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponse(savedProject);
    }

    /**
     * Actualiza un proyecto existente con la información proporcionada.
     *
     * @param id el identificador del proyecto a actualizar
     * @param request la solicitud de actualización del proyecto
     * @return un objeto ProjectResponse que representa el proyecto actualizado
     * @throws EntityNotFoundException si el proyecto no existe
     */
    public ProjectResponse updateProject(Long id, ProjectCreateRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        projectMapper.updateEntity(project, request);
        project.setUpdatedAt(LocalDate.now());

        Project updatedProject = projectRepository.save(project);
        return projectMapper.toResponse(updatedProject);
    }

    /**
     * Elimina un proyecto por su identificador.
     *
     * @param id el identificador del proyecto a eliminar
     * @throws EntityNotFoundException si el proyecto no existe
     */
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Proyecto no encontrado");
        }
        projectRepository.deleteById(id);
    }

}



package ar.edu.huergo.tombers.service;

import ar.edu.huergo.tombers.dto.project.ProjectCreateRequest;
import ar.edu.huergo.tombers.dto.project.ProjectResponse;
import ar.edu.huergo.tombers.entity.Project;
import ar.edu.huergo.tombers.mapper.ProjectMapper;
import ar.edu.huergo.tombers.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public List<ProjectResponse> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));
        return projectMapper.toResponse(project);
    }

    public ProjectResponse createProject(ProjectCreateRequest request) {
        Project project = projectMapper.toEntity(request);
        project.setCreatedAt(LocalDate.now());
        project.setUpdatedAt(LocalDate.now());
        
        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponse(savedProject);
    }

    public ProjectResponse updateProject(Long id, ProjectCreateRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        projectMapper.updateEntity(project, request);
        project.setUpdatedAt(LocalDate.now());

        Project updatedProject = projectRepository.save(project);
        return projectMapper.toResponse(updatedProject);
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Proyecto no encontrado");
        }
        projectRepository.deleteById(id);
    }

    public List<ProjectResponse> searchProjects(String query) {
        List<Project> projects = projectRepository.searchProjects(query);
        return projects.stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    public List<ProjectResponse> getActiveProjects() {
        List<Project> projects = projectRepository.findActiveProjectsOrderByCreatedAt();
        return projects.stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    public List<ProjectResponse> getIncompleteProjects() {
        List<Project> projects = projectRepository.findIncompleteProjectsOrderByProgress();
        return projects.stream()
                .map(projectMapper::toResponse)
                .toList();
    }
}



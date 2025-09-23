package ar.edu.huergo.tombers.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ar.edu.huergo.tombers.dto.project.ProjectCreateRequest;
import ar.edu.huergo.tombers.dto.project.ProjectResponse;
import ar.edu.huergo.tombers.entity.Project;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.mapper.ProjectMapper;
import ar.edu.huergo.tombers.repository.ProjectRepository;
import ar.edu.huergo.tombers.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;

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

        // Agregar el ID del proyecto a la lista de proyectos creados del usuario autenticado
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        if (user.getCreatedProjectIds() == null) {
            user.setCreatedProjectIds(new ArrayList<>());
        }
        user.getCreatedProjectIds().add(savedProject.getId());
        userRepository.save(user);

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
        // Obtener el usuario autenticado
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verificar si el proyecto existe
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        // Verificar si el usuario es admin o dueño del proyecto
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getNombre().equalsIgnoreCase("ADMIN"));
        boolean isOwner = user.getCreatedProjectIds() != null && user.getCreatedProjectIds().contains(id);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("No tiene permisos para eliminar este proyecto");
        }

        projectRepository.deleteById(id);
    }

    /**
     * Permite a un usuario dar like a un proyecto.
     * Agrega el ID del usuario a la lista de likes del proyecto y
     * agrega el ID del proyecto a la lista de proyectos likeados del usuario.
     *
     * @param projectId el identificador del proyecto al que se le quiere dar like
     * @throws EntityNotFoundException si el proyecto no existe
     */
    public void likeProject(Long projectId) {
        // Obtener el usuario autenticado
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verificar si el proyecto existe
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        // Inicializar listas si son null
        if (user.getLikedProjectIds() == null) {
            user.setLikedProjectIds(new ArrayList<>());
        }
        if (project.getLikeIds() == null) {
            project.setLikeIds(new ArrayList<>());
        }

        // Verificar que el usuario no haya dado like ya
        if (user.getLikedProjectIds().contains(projectId)) {
            throw new IllegalArgumentException("El usuario ya le dio like a este proyecto");
        }

        // Verificar que el usuario no haya dado dislike (no puede tener ambos)
        if (user.getDislikedProjectIds() != null && user.getDislikedProjectIds().contains(projectId)) {
            user.getDislikedProjectIds().remove(projectId);
        }

        // Agregar like: usuario -> proyecto y proyecto -> usuario
        user.getLikedProjectIds().add(projectId);
        project.getLikeIds().add(user.getId());

        // Guardar cambios
        userRepository.save(user);
        projectRepository.save(project);
    }

    /**
     * Permite a un usuario quitar el like de un proyecto.
     * Remueve el ID del usuario de la lista de likes del proyecto y
     * remueve el ID del proyecto de la lista de proyectos likeados del usuario.
     *
     * @param projectId el identificador del proyecto del que se quiere quitar el like
     * @throws EntityNotFoundException si el proyecto no existe
     */
    public void unlikeProject(Long projectId) {
        // Obtener el usuario autenticado
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verificar si el proyecto existe
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        // Verificar que el usuario haya dado like previamente
        if (user.getLikedProjectIds() == null || !user.getLikedProjectIds().contains(projectId)) {
            throw new IllegalArgumentException("El usuario no le ha dado like a este proyecto");
        }

        // Remover like: usuario -> proyecto y proyecto -> usuario
        user.getLikedProjectIds().remove(projectId);
        if (project.getLikeIds() != null) {
            project.getLikeIds().remove(user.getId());
        }

        // Guardar cambios
        userRepository.save(user);
        projectRepository.save(project);
    }

    /**
     * Permite a un usuario dar dislike a un proyecto.
     * Agrega el ID del usuario a la lista de dislikes del proyecto y
     * agrega el ID del proyecto a la lista de proyectos dislikeados del usuario.
     *
     * @param projectId el identificador del proyecto al que se le quiere dar dislike
     * @throws EntityNotFoundException si el proyecto no existe
     */
    public void dislikeProject(Long projectId) {
        // Obtener el usuario autenticado
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verificar si el proyecto existe
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        // Inicializar listas si son null
        if (user.getDislikedProjectIds() == null) {
            user.setDislikedProjectIds(new ArrayList<>());
        }

        // Verificar que el usuario no haya dado dislike ya
        if (user.getDislikedProjectIds().contains(projectId)) {
            throw new IllegalArgumentException("El usuario ya le dio dislike a este proyecto");
        }

        // Verificar que el usuario no haya dado like (no puede tener ambos)
        if (user.getLikedProjectIds() != null && user.getLikedProjectIds().contains(projectId)) {
            user.getLikedProjectIds().remove(projectId);
            if (project.getLikeIds() != null) {
                project.getLikeIds().remove(user.getId());
            }
        }

        // Agregar dislike: usuario -> proyecto
        user.getDislikedProjectIds().add(projectId);

        // Guardar cambios
        userRepository.save(user);
        projectRepository.save(project);
    }

    /**
     * Permite a un usuario quitar el dislike de un proyecto.
     * Remueve el ID del proyecto de la lista de proyectos dislikeados del usuario.
     *
     * @param projectId el identificador del proyecto del que se quiere quitar el dislike
     * @throws EntityNotFoundException si el proyecto no existe
     */
    public void undislikeProject(Long projectId) {
        // Obtener el usuario autenticado
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verificar si el proyecto existe
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        // Verificar que el usuario haya dado dislike previamente
        if (user.getDislikedProjectIds() == null || !user.getDislikedProjectIds().contains(projectId)) {
            throw new IllegalArgumentException("El usuario no le ha dado dislike a este proyecto");
        }

        // Remover dislike: usuario -> proyecto
        user.getDislikedProjectIds().remove(projectId);

        // Guardar cambios
        userRepository.save(user);
        projectRepository.save(project);
    }

}



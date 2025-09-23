package ar.edu.huergo.tombers.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.huergo.tombers.dto.project.ProjectCreateRequest;
import ar.edu.huergo.tombers.dto.project.ProjectResponse;
import ar.edu.huergo.tombers.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de proyectos.
 * Proporciona endpoints para CRUD y búsquedas de proyectos.
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Obtiene la lista completa de proyectos.
     * @return Lista de proyectos en la respuesta HTTP.
     */
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Obtiene un proyecto por su ID.
     * @param id Identificador del proyecto.
     * @return Proyecto encontrado en la respuesta HTTP.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    /**
     * Crea un nuevo proyecto.
     * @param request Datos para crear el proyecto.
     * @return Proyecto creado en la respuesta HTTP con código 201.
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectCreateRequest request) {
        ProjectResponse project = projectService.createProject(request);
        return ResponseEntity.status(201).body(project);
    }

    /**
     * Actualiza un proyecto existente.
     * @param id Identificador del proyecto a actualizar.
     * @param request Datos actualizados del proyecto.
     * @return Proyecto actualizado en la respuesta HTTP.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectCreateRequest request) {
        ProjectResponse project = projectService.updateProject(id, request);
        return ResponseEntity.ok(project);
    }

    /**
     * Elimina un proyecto por su ID.
     * @param id Identificador del proyecto a eliminar.
     * @return Mensaje de confirmación en la respuesta HTTP.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);

        return ResponseEntity.ok(Map.of("message", "Proyecto eliminado correctamente"));
    }

    /**
     * Permite a un usuario dar like a un proyecto.
     * @param projectId ID del proyecto al que se le quiere dar like.
     * @return Mensaje de confirmación en la respuesta HTTP.
     */
    @PostMapping("/{projectId}/like")
    public ResponseEntity<Map<String, String>> likeProject(@PathVariable Long projectId) {
        projectService.likeProject(projectId);
        return ResponseEntity.ok(Map.of("message", "Like agregado correctamente al proyecto"));
    }

    /**
     * Permite a un usuario quitar el like de un proyecto.
     * @param projectId ID del proyecto del que se quiere quitar el like.
     * @return Mensaje de confirmación en la respuesta HTTP.
     */
    @DeleteMapping("/{projectId}/like")
    public ResponseEntity<Map<String, String>> unlikeProject(@PathVariable Long projectId) {
        projectService.unlikeProject(projectId);
        return ResponseEntity.ok(Map.of("message", "Like removido correctamente del proyecto"));
    }

    /**
     * Permite a un usuario dar dislike a un proyecto.
     * @param projectId ID del proyecto al que se le quiere dar dislike.
     * @return Mensaje de confirmación en la respuesta HTTP.
     */
    @PostMapping("/{projectId}/dislike")
    public ResponseEntity<Map<String, String>> dislikeProject(@PathVariable Long projectId) {
        projectService.dislikeProject(projectId);
        return ResponseEntity.ok(Map.of("message", "Dislike agregado correctamente al proyecto"));
    }

    /**
     * Permite a un usuario quitar el dislike de un proyecto.
     * @param projectId ID del proyecto del que se quiere quitar el dislike.
     * @return Mensaje de confirmación en la respuesta HTTP.
     */
    @DeleteMapping("/{projectId}/dislike")
    public ResponseEntity<Map<String, String>> undislikeProject(@PathVariable Long projectId) {
        projectService.undislikeProject(projectId);
        return ResponseEntity.ok(Map.of("message", "Dislike removido correctamente del proyecto"));
    }

}

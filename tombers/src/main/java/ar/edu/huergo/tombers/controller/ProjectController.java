package ar.edu.huergo.tombers.controller;

import ar.edu.huergo.tombers.dto.project.ProjectCreateRequest;
import ar.edu.huergo.tombers.dto.project.ProjectResponse;
import ar.edu.huergo.tombers.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectCreateRequest request) {
        ProjectResponse project = projectService.createProject(request);
        return ResponseEntity.status(201).body(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectCreateRequest request) {
        ProjectResponse project = projectService.updateProject(id, request);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProjectResponse>> searchProjects(@RequestParam String q) {
        List<ProjectResponse> projects = projectService.searchProjects(q);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProjectResponse>> getActiveProjects() {
        List<ProjectResponse> projects = projectService.getActiveProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/incomplete")
    public ResponseEntity<List<ProjectResponse>> getIncompleteProjects() {
        List<ProjectResponse> projects = projectService.getIncompleteProjects();
        return ResponseEntity.ok(projects);
    }
}



package ar.edu.huergo.tombers.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.huergo.tombers.dto.project.ProjectCreateRequest;
import ar.edu.huergo.tombers.dto.project.ProjectResponse;
import ar.edu.huergo.tombers.service.AuthService;
import ar.edu.huergo.tombers.service.ProjectService;
import ar.edu.huergo.tombers.service.UserService;
import ar.edu.huergo.tombers.security.JwtAuthenticationFilter;
import ar.edu.huergo.tombers.security.JwtService;

@WebMvcTest(ProjectController.class)
@DisplayName("Tests de Controlador - ProjectController")
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    // Beans de seguridad necesarios para que el contexto de @WebMvcTest cargue
    @MockBean private JwtService jwtService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private UserService userService;
    @MockBean private AuthService authService;

    @Test
    @WithMockUser // Simula un usuario autenticado para pasar el filtro de seguridad
    @DisplayName("Debería obtener todos los proyectos y devolver status 200 OK")
    void deberiaObtenerTodosLosProyectos() throws Exception {
        // Given: Una lista de proyectos simulada
        ProjectResponse project1 = ProjectResponse.builder().id(1L).title("Proyecto 1").build();
        ProjectResponse project2 = ProjectResponse.builder().id(2L).title("Proyecto 2").build();
        when(projectService.getAllProjects()).thenReturn(List.of(project1, project2));

        // When & Then: Se realiza un GET a /projects
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Proyecto 1"));
    }
    
    @Test
    @WithMockUser
    @DisplayName("Debería crear un proyecto y devolver status 201 Created")
    void deberiaCrearUnProyecto() throws Exception {
        // Given: Una solicitud de creación
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setTitle("Nuevo Proyecto");

        ProjectResponse response = ProjectResponse.builder().id(1L).title("Nuevo Proyecto").build();
        when(projectService.createProject(any(ProjectCreateRequest.class))).thenReturn(response);
        
        // When & Then: Se realiza un POST a /projects
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Nuevo Proyecto"));
    }
    
    @Test
    @WithMockUser
    @DisplayName("Debería actualizar un proyecto y devolver status 200 OK")
    void deberiaActualizarUnProyecto() throws Exception {
        // Given: Un ID y una solicitud de actualización
        Long projectId = 1L;
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setTitle("Proyecto Actualizado");

        ProjectResponse response = ProjectResponse.builder().id(projectId).title("Proyecto Actualizado").build();
        when(projectService.updateProject(eq(projectId), any(ProjectCreateRequest.class))).thenReturn(response);
        
        // When & Then: Se realiza un PUT a /projects/{id}
        mockMvc.perform(put("/projects/{id}", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Proyecto Actualizado"));
    }
}
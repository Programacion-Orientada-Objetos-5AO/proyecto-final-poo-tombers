package ar.edu.huergo.tombers.controller;

import ar.edu.huergo.tombers.dto.project.ProjectCreateRequest;
import ar.edu.huergo.tombers.dto.project.ProjectResponse;
import ar.edu.huergo.tombers.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectResponse projectResponse;
    private ProjectCreateRequest createRequest;

    @BeforeEach
    public void setup() {
        projectResponse = ProjectResponse.builder()
                .id(1L)
                .title("Test Project")
                .description("Test Description")
                .build();

        createRequest = new ProjectCreateRequest();
        createRequest.setTitle("Test Project");
        createRequest.setDescription("Test Description");
        createRequest.setStatus(ProjectCreateRequest.ProjectStatus.ACTIVE);
    }

    @Test
    public void testGetAllProjects() throws Exception {
        Mockito.when(projectService.getAllProjects()).thenReturn(List.of(projectResponse));

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetProjectById() throws Exception {
        Mockito.when(projectService.getProjectById(1L)).thenReturn(projectResponse);

        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateProject() throws Exception {
        Mockito.when(projectService.createProject(any(ProjectCreateRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateProject() throws Exception {
        Mockito.when(projectService.updateProject(eq(1L), any(ProjectCreateRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(put("/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteProject() throws Exception {
        Mockito.doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/projects/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testSearchProjects() throws Exception {
        Mockito.when(projectService.searchProjects("test")).thenReturn(List.of(projectResponse));

        mockMvc.perform(get("/projects/search")
                .param("q", "test"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetActiveProjects() throws Exception {
        Mockito.when(projectService.getActiveProjects()).thenReturn(List.of(projectResponse));

        mockMvc.perform(get("/projects/active"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetIncompleteProjects() throws Exception {
        Mockito.when(projectService.getIncompleteProjects()).thenReturn(List.of(projectResponse));

        mockMvc.perform(get("/projects/incomplete"))
                .andExpect(status().isOk());
    }
}

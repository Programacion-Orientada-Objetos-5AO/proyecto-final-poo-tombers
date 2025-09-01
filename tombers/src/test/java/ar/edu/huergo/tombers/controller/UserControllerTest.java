package ar.edu.huergo.tombers.controller;

import ar.edu.huergo.tombers.dto.user.UserResponse;
import ar.edu.huergo.tombers.dto.user.UserUpdateRequest;
import ar.edu.huergo.tombers.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse userResponse;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    public void setup() {
        userResponse = UserResponse.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .build();

        updateRequest = UserUpdateRequest.builder()
                .firstName("Updated")
                .lastName("User")
                .build();
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testGetUserProfile() throws Exception {
        Mockito.when(userService.getUserProfile("test@example.com")).thenReturn(userResponse);

        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testUpdateUserProfile() throws Exception {
        Mockito.when(userService.updateUserProfile(eq("test@example.com"), any(UserUpdateRequest.class))).thenReturn(userResponse);

        mockMvc.perform(put("/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testSearchUsers() throws Exception {
        Mockito.when(userService.searchUsers("test")).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/users/search")
                .param("q", "test"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testGetAvailableUsers() throws Exception {
        Mockito.when(userService.getAvailableUsers()).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/users/available"))
                .andExpect(status().isOk());
    }
}

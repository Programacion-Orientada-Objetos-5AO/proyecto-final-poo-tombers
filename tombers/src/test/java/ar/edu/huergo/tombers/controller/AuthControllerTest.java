package ar.edu.huergo.tombers.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.huergo.tombers.dto.auth.AuthResponse;
import ar.edu.huergo.tombers.dto.auth.LoginRequest;
import ar.edu.huergo.tombers.dto.auth.RegisterRequest;
import ar.edu.huergo.tombers.service.AuthService;
import ar.edu.huergo.tombers.service.ProjectService;
import ar.edu.huergo.tombers.service.UserService;
import ar.edu.huergo.tombers.security.JwtAuthenticationFilter;
import ar.edu.huergo.tombers.security.JwtService;

// Usamos @WebMvcTest para probar solo la capa web (el controlador)
@WebMvcTest(AuthController.class)
@DisplayName("Tests de Controlador - AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc; // Para simular peticiones HTTP

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    @MockBean
    private AuthService authService; // Mockeamos el servicio de autenticación

    // Mockeamos otros beans que son dependencias de la configuración de seguridad
    @MockBean
    private JwtService jwtService;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private UserService userService;
    @MockBean
    private ProjectService projectService;
    
    @Test
    @DisplayName("Debería registrar un usuario y devolver status 201 Created")
    void deberiaRegistrarUsuario() throws Exception {
        // Given: Datos de un nuevo usuario
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("Password123@");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        
        // Simulamos la respuesta del servicio
        AuthResponse authResponse = AuthResponse.builder().token("fake-jwt-token").build();
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // When & Then: Realizamos una petición POST a /auth/register
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated()) // Esperamos un status 201
                .andExpect(jsonPath("$.token").value("fake-jwt-token")); // Verificamos que el token esté en la respuesta
    }
    
    @Test
    @DisplayName("Debería hacer login y devolver status 200 OK")
    void deberiaHacerLogin() throws Exception {
        // Given: Credenciales de login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        // Simulamos la respuesta del servicio
        AuthResponse authResponse = AuthResponse.builder().token("fake-jwt-token-login").build();
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // When & Then: Realizamos una petición POST a /auth/login
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Esperamos un status 200
                .andExpect(jsonPath("$.token").value("fake-jwt-token-login"));
    }
}
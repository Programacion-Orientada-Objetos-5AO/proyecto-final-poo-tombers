package ar.edu.huergo.tombers.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.huergo.tombers.dto.user.UserResponse;
import ar.edu.huergo.tombers.dto.user.UserUpdateRequest;
import ar.edu.huergo.tombers.entity.Role;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.mapper.UserMapper;
import ar.edu.huergo.tombers.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Servicio - UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    // ... (tests existentes que ya tenías) ...
    @Test
    @DisplayName("Debería obtener el perfil de un usuario por email")
    void deberiaObtenerPerfilUsuario() {
        // Given: Un email y un usuario simulado
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponse);

        // When: Se llama al método para obtener el perfil
        UserResponse result = userService.getUserProfile(email);

        // Then: El resultado debe ser el esperado
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }
    
    @Test
    @DisplayName("Debería lanzar EntityNotFoundException si el usuario no se encuentra al obtener perfil")
    void deberiaLanzarExcepcionAlObtenerPerfilInexistente() {
        // Given: Un email que no corresponde a ningún usuario
        String email = "noexiste@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then: Se espera que se lance EntityNotFoundException
        assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserProfile(email);
        });
    }

    @Test
    @DisplayName("Debería actualizar el perfil de un usuario")
    void deberiaActualizarPerfilUsuario() {
        // Given: Un email, una solicitud de actualización y un usuario existente
        String email = "test@example.com";
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Juan Nuevo");
        
        User existingUser = new User();
        existingUser.setEmail(email);
        existingUser.setFirstName("Juan");
        
        User updatedUser = new User();
        updatedUser.setEmail(email);
        updatedUser.setFirstName("Juan Nuevo");
        
        UserResponse responseDto = new UserResponse();
        responseDto.setEmail(email);
        responseDto.setFirstName("Juan Nuevo");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(responseDto);

        // When: Se llama al método para actualizar el perfil
        UserResponse result = userService.updateUserProfile(email, request);

        // Then: El nombre del usuario en la respuesta debería estar actualizado
        assertNotNull(result);
        assertEquals("Juan Nuevo", result.getFirstName());
        verify(userMapper, times(1)).updateEntity(existingUser, request);
    }
    
    // Tests nuevos añadidos
    @Test
    @DisplayName("Debería crear un perfil de usuario exitosamente")
    void deberiaCrearPerfilExitosamente() {
        // Given
        String email = "nuevo@example.com";
        String username = "nuevo_user";
        String role = "USER";

        User newUser = User.builder()
                .email(email)
                .username(username)
                .roles(Set.of(Role.USER))
                .build();
        UserResponse responseDto = new UserResponse();
        responseDto.setEmail(email);
        responseDto.setUsername(username);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(userMapper.toDto(newUser)).thenReturn(responseDto);

        // When
        UserResponse result = userService.createProfile(email, username, role);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el email ya existe al crear perfil")
    void deberiaLanzarExcepcionAlCrearPerfilConEmailExistente() {
        // Given
        String email = "existente@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createProfile(email, "user", "USER");
        });
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el rol es inválido al crear perfil")
    void deberiaLanzarExcepcionAlCrearPerfilConRolInvalido() {
        // Given
        String email = "test@example.com";
        String rolInvalido = "ROL_INVALIDO";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createProfile(email, "user", rolInvalido);
        });
    }
    
    @Test
    @DisplayName("Debería devolver una lista de usuarios al buscar")
    void deberiaBuscarUsuarios() {
        // Given
        String query = "juan";
        User user = new User();
        user.setFirstName("Juan");
        List<User> users = Collections.singletonList(user);
        
        UserResponse response = new UserResponse();
        response.setFirstName("Juan");

        when(userRepository.searchUsers(query)).thenReturn(users);
        when(userMapper.toDto(user)).thenReturn(response);

        // When
        List<UserResponse> result = userService.searchUsers(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getFirstName());
    }
}
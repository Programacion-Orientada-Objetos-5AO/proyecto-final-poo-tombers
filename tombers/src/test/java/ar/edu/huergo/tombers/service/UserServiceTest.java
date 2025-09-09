package ar.edu.huergo.tombers.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @InjectMocks private UserService userService;

    private User sampleUser() {
        return User.builder()
                .id(1L)
                .firstName("Ana")
                .lastName("Lopez")
                .email("ana@test.com")
                .username("anita")
                .password("pwd")
                .roles(Set.of(Role.USER))
                .build();
    }

    @Test
    @DisplayName("getUserProfile devuelve DTO cuando existe usuario")
    void getUserProfileOk() {
        var user = sampleUser();
        when(userRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(UserResponse.builder().email("ana@test.com").username("anita").build());

        var dto = userService.getUserProfile("ana@test.com");
        assertEquals("ana@test.com", dto.getEmail());
        verify(userRepository).findByEmail("ana@test.com");
    }

    @Test
    @DisplayName("getUserProfile lanza EntityNotFound si no existe")
    void getUserProfileNotFound() {
        when(userRepository.findByEmail("x@x.com")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserProfile("x@x.com"));
    }

    @Test
    @DisplayName("updateUserProfile aplica mapper y guarda")
    void updateUserProfile() {
        var user = sampleUser();
        when(userRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(UserResponse.builder().email("ana@test.com").build());

        var req = new UserUpdateRequest();
        var dto = userService.updateUserProfile("ana@test.com", req);

        verify(userMapper).updateEntity(user, req);
        verify(userRepository).save(user);
        assertEquals("ana@test.com", dto.getEmail());
    }

    @Test
    @DisplayName("createProfile crea usuario cuando email no existe y rol válido")
    void createProfileOk() {
        when(userRepository.existsByEmail("nuevo@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(99L);
            return u;
        });
        when(userMapper.toDto(any(User.class))).thenReturn(UserResponse.builder().email("nuevo@test.com").username("nuevo").build());

        var dto = userService.createProfile("nuevo@test.com", "nuevo", "USER");

        assertEquals("nuevo@test.com", dto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("createProfile falla si email existe")
    void createProfileEmailExists() {
        when(userRepository.existsByEmail("existe@test.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.createProfile("existe@test.com", "u", "USER"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("createProfile falla si rol inválido")
    void createProfileInvalidRole() {
        when(userRepository.existsByEmail("nuevo@test.com")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> userService.createProfile("nuevo@test.com", "u", "rol_invalido"));
    }
}


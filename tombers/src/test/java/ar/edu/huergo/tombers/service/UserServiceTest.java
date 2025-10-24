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
import org.springframework.web.multipart.MultipartFile;

import ar.edu.huergo.tombers.dto.user.UserResponse;
import ar.edu.huergo.tombers.dto.user.UserUpdateRequest;
import ar.edu.huergo.tombers.entity.Rol;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.mapper.UserMapper;
import ar.edu.huergo.tombers.repository.UserRepository;
import ar.edu.huergo.tombers.repository.security.RolRepository;
import ar.edu.huergo.tombers.service.storage.FileStorageService;
import ar.edu.huergo.tombers.service.storage.StorageDirectory;
import ar.edu.huergo.tombers.service.storage.StoredFile;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Servicio - UserService")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private RolRepository rolRepository;
    @Mock private FileStorageService fileStorageService;
    @Mock private UserRatingService userRatingService;
    @InjectMocks private UserService userService;

    private User sampleUser() {
        Rol userRole = new Rol("USER");
        userRole.setId(1L);
        return User.builder()
                .id(1L)
                .firstName("Ana")
                .lastName("Lopez")
                .email("ana@test.com")
                .username("anita")
                .password("pwd")
                .roles(Set.of(userRole))
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
        var dto = userService.updateUserProfile("ana@test.com", req, null);

        verify(userMapper).updateEntity(user, req);
        verify(userRepository).save(user);
        verify(fileStorageService, never()).store(any(MultipartFile.class), any(StorageDirectory.class));
        assertEquals("ana@test.com", dto.getEmail());
    }

    @Test
    @DisplayName("updateUserProfile guarda foto y elimina anterior")
    void updateUserProfileWithPicture() {
        var user = sampleUser();
        user.setProfilePictureUrl("/uploads/users/old.jpg");

        var file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        var stored = new StoredFile("users/new.jpg", "/uploads/users/new.jpg");

        when(userRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(UserResponse.builder().email("ana@test.com").profilePictureUrl(stored.publicUrl()).build());
        when(fileStorageService.store(file, StorageDirectory.USER_PROFILE)).thenReturn(stored);

        var req = new UserUpdateRequest();
        var dto = userService.updateUserProfile("ana@test.com", req, file);

        verify(userMapper).updateEntity(user, req);
        verify(fileStorageService).store(file, StorageDirectory.USER_PROFILE);
        verify(fileStorageService).deleteByPublicUrl("/uploads/users/old.jpg");
        verify(userRepository).save(user);
        assertEquals(stored.publicUrl(), user.getProfilePictureUrl());
        assertEquals(stored.publicUrl(), dto.getProfilePictureUrl());
    }

    @Test
    @DisplayName("createProfile crea usuario cuando email no existe y rol valido")
    void createProfileOk() {
        Rol userRole = new Rol("USER");
        when(userRepository.existsByEmail("nuevo@test.com")).thenReturn(false);
        when(rolRepository.findByNombre("USER")).thenReturn(Optional.of(userRole));
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
    @DisplayName("createProfile falla si rol invalido")
    void createProfileInvalidRole() {
        when(userRepository.existsByEmail("nuevo@test.com")).thenReturn(false);
        when(rolRepository.findByNombre("ROL_INVALIDO")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.createProfile("nuevo@test.com", "u", "rol_invalido"));
    }
}

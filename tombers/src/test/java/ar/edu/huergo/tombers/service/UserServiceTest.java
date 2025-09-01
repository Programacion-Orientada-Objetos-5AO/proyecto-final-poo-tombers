package ar.edu.huergo.tombers.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ar.edu.huergo.tombers.dto.user.UserResponse;
import ar.edu.huergo.tombers.dto.user.UserUpdateRequest;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.mapper.UserMapper;
import ar.edu.huergo.tombers.repository.UserRepository;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .build();

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
    public void testGetUserProfile_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserProfile("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
        verify(userMapper).toDto(user);
    }

    @Test
    public void testGetUserProfile_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUserProfile("test@example.com");
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    public void testUpdateUserProfile_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponse);

        UserResponse result = userService.updateUserProfile("test@example.com", updateRequest);

        assertNotNull(result);
        verify(userRepository).findByEmail("test@example.com");
        verify(userMapper).updateEntity(user, updateRequest);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    public void testSearchUsers() {
        when(userRepository.searchUsers("test")).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponse);

        List<UserResponse> result = userService.searchUsers("test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).searchUsers("test");
        verify(userMapper).toDto(user);
    }

    @Test
    public void testGetAvailableUsers() {
        when(userRepository.findAvailableUsers()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponse);

        List<UserResponse> result = userService.getAvailableUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAvailableUsers();
        verify(userMapper).toDto(user);
    }
}

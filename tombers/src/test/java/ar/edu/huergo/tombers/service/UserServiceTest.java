package ar.edu.huergo.tombers.service;

import ar.edu.huergo.tombers.dto.user.UserResponse;
import ar.edu.huergo.tombers.dto.user.UserUpdateRequest;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.mapper.UserMapper;
import ar.edu.huergo.tombers.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserProfile() {
        String email = "test@example.com";
        User user = User.builder().email(email).build();
        UserResponse response = UserResponse.builder().email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(response);

        UserResponse result = userService.getUserProfile(email);

        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepository).findByEmail(email);
        verify(userMapper).toDto(user);
    }

    @Test
    void testGetUserProfileNotFound() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfile(email))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Usuario no encontrado: " + email);
    }

    @Test
    void testUpdateUserProfile() {
        String email = "test@example.com";
        User user = User.builder().email(email).build();
        UserUpdateRequest request = UserUpdateRequest.builder().firstName("Updated").build();
        User updatedUser = User.builder().email(email).firstName("Updated").build();
        UserResponse response = UserResponse.builder().email(email).firstName("Updated").build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(response);

        UserResponse result = userService.updateUserProfile(email, request);

        assertThat(result.getFirstName()).isEqualTo("Updated");
        verify(userMapper).updateEntity(user, request);
        verify(userRepository).save(user);
    }

    @Test
    void testSearchUsers() {
        String query = "Developer";
        User user = User.builder().email("dev@example.com").build();
        UserResponse response = UserResponse.builder().email("dev@example.com").build();

        when(userRepository.searchUsers(query)).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(response);

        List<UserResponse> results = userService.searchUsers(query);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEmail()).isEqualTo("dev@example.com");
    }

    @Test
    void testGetAvailableUsers() {
        User user = User.builder().email("available@example.com").status(User.UserStatus.DISPONIBLE).build();
        UserResponse response = UserResponse.builder().email("available@example.com").build();

        when(userRepository.findAvailableUsers()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(response);

        List<UserResponse> results = userService.getAvailableUsers();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEmail()).isEqualTo("available@example.com");
    }
}

package ar.edu.huergo.tombers.service;

import ar.edu.huergo.tombers.dto.user.UserResponse;
import ar.edu.huergo.tombers.dto.user.UserUpdateRequest;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.mapper.UserMapper;
import ar.edu.huergo.tombers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return userMapper.toDto(user);
    }

    public UserResponse updateUserProfile(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        userMapper.updateEntity(user, request);
        User updatedUser = userRepository.save(user);
        
        return userMapper.toDto(updatedUser);
    }

    public List<UserResponse> searchUsers(String query) {
        List<User> users = userRepository.searchUsers(query);
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserResponse> getAvailableUsers() {
        List<User> users = userRepository.findAvailableUsers();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }
}



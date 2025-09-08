package ar.edu.huergo.tombers.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import ar.edu.huergo.tombers.dto.user.UserResponse;
import ar.edu.huergo.tombers.dto.user.UserUpdateRequest;
import ar.edu.huergo.tombers.entity.Role;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.mapper.UserMapper;
import ar.edu.huergo.tombers.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Obtiene el perfil de un usuario por su email.
     *
     * @param email el email del usuario
     * @return un objeto UserResponse que representa el perfil del usuario
     * @throws EntityNotFoundException si el usuario no existe
     */
    public UserResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + email));
        return userMapper.toDto(user);
    }

    /**
     * Actualiza el perfil de un usuario con la información proporcionada.
     *
     * @param email el email del usuario
     * @param request la solicitud de actualización del perfil
     * @return un objeto UserResponse que representa el perfil actualizado
     * @throws EntityNotFoundException si el usuario no existe
     */
    public UserResponse updateUserProfile(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + email));

        userMapper.updateEntity(user, request);
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    /**
     * Busca usuarios que coincidan con la consulta proporcionada.
     *
     * @param query la cadena de búsqueda
     * @return una lista de objetos UserResponse que representan los usuarios encontrados
     */
    public List<UserResponse> searchUsers(String query) {
        List<User> users = userRepository.searchUsers(query);
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Obtiene una lista de usuarios disponibles.
     *
     * @return una lista de objetos UserResponse que representan los usuarios disponibles
     */
    public List<UserResponse> getAvailableUsers() {
        List<User> users = userRepository.findAvailableUsers();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Crea un perfil de usuario y le asigna un rol.
     *
     * @param email el email del usuario
     * @param username el nombre de usuario
     * @param role el rol a asignar
     * @return un objeto UserResponse que representa el perfil creado
     * @throws IllegalArgumentException si el usuario ya existe o el rol es inválido
     */
    public UserResponse createProfile(String email, String username, String role) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Usuario ya existe con ese email: " + email);
        }

        // Convertir role string a Role enum
        Role userRole;
        try {
            userRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol inválido: " + role);
        }

        // Crear nuevo usuario desde cero
        User newUser = User.builder()
                .firstName("") // Valor por defecto
                .lastName("") // Valor por defecto
                .email(email)
                .username(username)
                .password("default") // Contraseña por defecto, no codificada
                .roles(Set.of(userRole))
                .status(User.UserStatus.DISPONIBLE)
                .build();

        User savedUser = userRepository.save(newUser);
        return userMapper.toDto(savedUser);
    }
}



package ar.edu.huergo.tombers.repository;

import ar.edu.huergo.tombers.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveUser() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .username("johndoe")
                .password("password")
                .status(User.UserStatus.DISPONIBLE)
                .build();

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testFindByEmail() {
        User user = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .username("janesmith")
                .password("password")
                .status(User.UserStatus.DISPONIBLE)
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("jane.smith@example.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void testExistsByEmail() {
        User user = User.builder()
                .firstName("Alice")
                .lastName("Wonder")
                .email("alice@example.com")
                .username("alice")
                .password("password")
                .status(User.UserStatus.DISPONIBLE)
                .build();
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("alice@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void testFindAvailableUsers() {
        User user1 = User.builder()
                .firstName("Bob")
                .lastName("Builder")
                .email("bob@example.com")
                .username("bob")
                .password("password")
                .status(User.UserStatus.DISPONIBLE)
                .build();
        User user2 = User.builder()
                .firstName("Charlie")
                .lastName("Chaplin")
                .email("charlie@example.com")
                .username("charlie")
                .password("password")
                .status(User.UserStatus.OCUPADO)
                .build();
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> availableUsers = userRepository.findAvailableUsers();

        assertThat(availableUsers).hasSize(1);
        assertThat(availableUsers.get(0).getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    void testSearchUsers() {
        User user = User.builder()
                .firstName("David")
                .lastName("Goliath")
                .email("david@example.com")
                .username("david")
                .password("password")
                .specialization("Developer")
                .status(User.UserStatus.DISPONIBLE)
                .build();
        userRepository.save(user);

        List<User> searchResults = userRepository.searchUsers("Developer");

        assertThat(searchResults).hasSize(1);
        assertThat(searchResults.get(0).getEmail()).isEqualTo("david@example.com");
    }
}

package ar.edu.huergo.tombers.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.tombers.entity.User;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .status(User.UserStatus.DISPONIBLE)
                .build();
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    public void testFindByEmail() {
        Optional<User> found = userRepository.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    public void testFindByUsername() {
        Optional<User> found = userRepository.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    public void testExistsByEmail() {
        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    public void testExistsByUsername() {
        assertTrue(userRepository.existsByUsername("testuser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    public void testFindAvailableUsers() {
        List<User> available = userRepository.findAvailableUsers();
        assertNotNull(available);
        assertEquals(1, available.size());
        assertEquals(User.UserStatus.DISPONIBLE, available.get(0).getStatus());
    }

    @Test
    public void testSearchUsers() {
        List<User> results = userRepository.searchUsers("Test");
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Test", results.get(0).getFirstName());
    }
}

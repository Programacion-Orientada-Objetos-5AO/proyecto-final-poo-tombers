package ar.edu.huergo.tombers.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import ar.edu.huergo.tombers.entity.User;

public class JwtServiceTest {

    private JwtService jwtService;

    private User user;

    @BeforeEach
    public void setup() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLXNwcmluZy1ib290LWFwcGxpY2F0aW9u");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L); // 1 hour

        user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .build();
    }

    @Test
    public void testGenerateToken() {
        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void testExtractUsername() {
        String token = jwtService.generateToken(user);
        String username = jwtService.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    public void testIsTokenValid() {
        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, user);

        assertTrue(isValid);
    }

    @Test
    public void testIsTokenValid_InvalidUser() {
        String token = jwtService.generateToken(user);
        User invalidUser = User.builder()
                .email("invalid@example.com")
                .username("invaliduser")
                .build();

        boolean isValid = jwtService.isTokenValid(token, invalidUser);

        assertFalse(isValid);
    }
}

package ar.edu.huergo.tombers.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

@DisplayName("Tests de Entidad - User")
class UserEntityTest {

    @Test
    @DisplayName("Debería mapear roles a authorities con prefijo ROLE_")
    void deberiaMapearRolesAAuthorities() {
        // Given
        User user = User.builder()
                .email("user@test.com")
                .username("user1")
                .roles(Set.of(new ar.edu.huergo.tombers.entity.Rol("ADMIN"), new ar.edu.huergo.tombers.entity.Rol("USER")))
                .build();

        // When
        List<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();

        // Then
        assertEquals(List.of("ROLE_ADMIN", "ROLE_USER"), authorities);
    }

    @Test
    @DisplayName("Debería devolver email como username para autenticación")
    void deberiaDevolverEmailComoUsername() {
        // Given
        User user = User.builder()
                .email("user@test.com")
                .username("usuarioVisible")
                .build();

        // When - getUsername() es usado por Spring Security
        // Then
        assertEquals("user@test.com", user.getUsername());
        assertEquals("usuarioVisible", user.getUsernameField());
    }

    @Test
    @DisplayName("Debería tener flags de cuenta habilitados por defecto")
    void deberiaTenerFlagsCuentaHabilitados() {
        // Given
        User user = new User();

        // Then
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }
}


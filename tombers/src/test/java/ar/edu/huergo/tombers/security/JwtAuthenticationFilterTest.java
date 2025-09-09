package ar.edu.huergo.tombers.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Seguridad - JwtAuthenticationFilter")
class JwtAuthenticationFilterTest {

    @Mock private JwtTokenService jwtTokenService;
    @Mock private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    @Mock private FilterChain filterChain;

    @InjectMocks private JwtAuthenticationFilter filter;

    @AfterEach
    void cleanupContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Sin header Authorization, continúa la cadena")
    void noAuthorizationHeader() throws Exception {
        var req = new MockHttpServletRequest();
        var res = new MockHttpServletResponse();

        filter.doFilter(req, res, filterChain);

        verify(filterChain).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Token inválido devuelve 401 con problem+json")
    void invalidToken() throws Exception {
        var req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer abc.def.ghi");
        var res = new MockHttpServletResponse();

        when(jwtTokenService.extraerUsername("abc.def.ghi")).thenThrow(new RuntimeException("firma inválida"));

        filter.doFilter(req, res, filterChain);

        assertEquals(401, res.getStatus());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON_VALUE, res.getContentType());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Token válido autentica en el contexto")
    void validTokenAuthenticates() throws Exception {
        var req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer good.token");
        var res = new MockHttpServletResponse();

        when(jwtTokenService.extraerUsername("good.token")).thenReturn("user@test.com");
        UserDetails ud = User.withUsername("user@test.com").password("x").roles("USER").build();
        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(ud);
        when(jwtTokenService.esTokenValido("good.token", ud)).thenReturn(true);

        filter.doFilter(req, res, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user@test.com", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    @DisplayName("Usuario no encontrado devuelve 404")
    void userNotFound() throws Exception {
        var req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer token");
        var res = new MockHttpServletResponse();

        when(jwtTokenService.extraerUsername("token")).thenReturn("no@test.com");
        when(userDetailsService.loadUserByUsername("no@test.com")).thenThrow(new EntityNotFoundException("no existe"));

        filter.doFilter(req, res, filterChain);

        assertEquals(404, res.getStatus());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON_VALUE, res.getContentType());
        verify(filterChain, never()).doFilter(any(), any());
    }
}


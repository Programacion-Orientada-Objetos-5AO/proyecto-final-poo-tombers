package ar.edu.huergo.tombers.security;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        try {
            userEmail = jwtService.extraerUsername(jwt);
        } catch (Exception e) {
            // Error al extraer username del JWT - return 401
            ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
            problem.setTitle("Error de autenticación");
            problem.setDetail(e.getMessage());
            problem.setType(URI.create("https://http.dev/problems/unauthorized"));

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(problem));
            return;
        }
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.esTokenValido(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (EntityNotFoundException e) {
                // Usuario no encontrado - return 404
                ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
                problem.setTitle("Recurso no encontrado");
                problem.setDetail(e.getMessage());
                problem.setType(URI.create("https://http.dev/problems/not-found"));

                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                response.getWriter().write(new ObjectMapper().writeValueAsString(problem));
                return;
            } catch (Exception e) {
                // Otros errores de autentificacion - return 401
                ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
                problem.setTitle("Error de autenticación");
                problem.setDetail(e.getMessage());
                problem.setType(URI.create("https://http.dev/problems/unauthorized"));

                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                response.getWriter().write(new ObjectMapper().writeValueAsString(problem));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}



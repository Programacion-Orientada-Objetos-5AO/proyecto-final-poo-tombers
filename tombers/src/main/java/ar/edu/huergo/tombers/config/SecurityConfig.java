package ar.edu.huergo.tombers.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import ar.edu.huergo.tombers.security.JwtAuthenticationEntryPoint;
import ar.edu.huergo.tombers.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

/**
 * ConfiguraciÃ³n de seguridad para la aplicaciÃ³n utilizando Spring Security.
 * Define la cadena de filtros de seguridad, CORS y autorizaciones de endpoints.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * Configura la cadena de filtros de seguridad para la aplicaciÃ³n.
     * Deshabilita CSRF, configura CORS, define autorizaciones de endpoints,
     * establece polÃ­tica de sesiones sin estado y agrega filtros JWT.
     * @param http El objeto HttpSecurity para configurar la seguridad.
     * @return La SecurityFilterChain configurada.
     * @throws Exception Si ocurre un error durante la configuraciÃ³n.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/projects/**").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers("/api/users/**").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers("/api/users/CreateProfile").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            );

        return http.build();
    }

    /**
     * Configura la fuente de configuraciÃ³n CORS para permitir solicitudes desde cualquier origen.
     * Permite mÃ©todos HTTP comunes y credenciales.
     * @return Una CorsConfigurationSource configurada.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


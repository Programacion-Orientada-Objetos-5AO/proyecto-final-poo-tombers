package ar.edu.huergo.tombers.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de login.
 * Contiene el email y contraseña del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * Correo electrónico del usuario para login.
     */
    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es válido")
    private String email;

    /**
     * Contraseña del usuario para login.
     */
    @NotBlank(message = "La contraseña es requerida")
    private String password;
}

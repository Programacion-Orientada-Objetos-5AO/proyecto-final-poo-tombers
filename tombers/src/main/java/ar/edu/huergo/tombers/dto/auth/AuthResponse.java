package ar.edu.huergo.tombers.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de autenticación.
 * Contiene el mensaje, token y información del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * Mensaje de respuesta.
     */
    private String message;

    /**
     * Token de autenticación JWT.
     */
    private String token;

    /**
     * Información del usuario autenticado.
     */
    private UserInfo user;

    /**
     * Clase interna para la información del usuario.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        /**
         * Identificador del usuario.
         */
        private Long id;

        /**
         * Nombre de usuario.
         */
        private String username;

        /**
         * Nombre del usuario.
         */
        private String firstName;

        /**
         * Apellido del usuario.
         */
        private String lastName;

        /**
         * Correo electrónico del usuario.
         */
        private String email;
    }
}

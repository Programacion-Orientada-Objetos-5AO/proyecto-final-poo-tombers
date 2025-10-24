package ar.edu.huergo.tombers.dto.auth;

import java.util.List;

import ar.edu.huergo.tombers.entity.Skill;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de registro de usuario.
 * Contiene todos los datos necesarios para crear una cuenta de usuario.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    /**
     * Nombre del usuario.
     */
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String firstName;

    /**
     * Apellido del usuario.
     */
    @NotBlank(message = "El apellido es requerido")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String lastName;

    /**
     * Correo electrónico del usuario.
     */
    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es válido")
    private String email;

    /**
     * Nombre de usuario único.
     */
    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 3, max = 30, message = "El nombre de usuario debe tener entre 3 y 30 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "El nombre de usuario solo puede contener letras, números y guiones bajos")
    private String username;

    /**
     * Contraseña del usuario con requisitos de seguridad.
     */
    @NotBlank(message = "La contraseña es requerida")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{16,}$", message = "La contraseña debe tener al menos 16 caracteres, una mayúscula, una minúscula, un número y un carácter especial")
    private String password;

    /**
     * Lista de habilidades del usuario.
     */
    private List<Skill> skills;

    /**
     * Edad del usuario.
     */
    private Integer age;

    /**
     * Fecha de nacimiento del usuario.
     */
    private String birthDate;

    /**
     * Idiomas que habla el usuario.
     */
    private String languages;

    /**
     * Especialización del usuario.
     */
    private String specialization;

    /**
     * Número de teléfono del usuario.
     */
    private String phone;

    /**
     * Enlace a LinkedIn del usuario.
     */
    private String linkedin;

    /**
     * Enlace a GitHub del usuario.
     */
    private String github;

    /**
     * Enlace al portafolio del usuario.
     */
    private String portfolio;

    /**
     * Biografía del usuario.
     */
    private String bio;

    /**
     * Lista de certificaciones del usuario.
     */
    private List<String> certifications;

    /**
     * Lista de intereses del usuario.
     */
    private List<String> interests;
}

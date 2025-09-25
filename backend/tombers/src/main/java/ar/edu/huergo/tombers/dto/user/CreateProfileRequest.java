package ar.edu.huergo.tombers.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProfileRequest {
    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es válido")
    private String email;

    @NotBlank(message = "El nombre de usuario es requerido")
    private String username;

    @NotBlank(message = "El rol es requerido")
    private String role;
}

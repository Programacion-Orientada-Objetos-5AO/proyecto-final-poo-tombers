package ar.edu.huergo.tombers.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para manejar requests de aceptar o rechazar usuarios interesados en un proyecto.
 * Contiene el ID del usuario y la acción a realizar (aceptar o rechazar).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManageInterestedRequest {

    /**
     * ID del usuario que mostró interés en el proyecto.
     * Este campo es obligatorio.
     */
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    /**
     * Acción a realizar con el usuario interesado.
     * Puede ser "ACCEPT" para aceptar o "REJECT" para rechazar.
     * Este campo es obligatorio.
     */
    @NotNull(message = "La acción es obligatoria")
    private Action action;

    /**
     * Enum que define las posibles acciones para manejar usuarios interesados.
     */
    public enum Action {
        ACCEPT,  // Aceptar al usuario como miembro del proyecto
        REJECT   // Rechazar al usuario (remover de interesados)
    }
}

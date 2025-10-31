package ar.edu.huergo.tombers.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para manejar requests de like a proyectos.
 * Contiene el ID del proyecto al que se le quiere dar like.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequest {

    /**
     * ID del proyecto al que se le quiere dar like.
     * Este campo es obligatorio.
     */
    @NotNull(message = "El ID del proyecto es obligatorio")
    private Long projectId;
}

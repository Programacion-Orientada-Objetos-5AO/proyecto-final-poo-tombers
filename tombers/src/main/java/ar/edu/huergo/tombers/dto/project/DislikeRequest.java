package ar.edu.huergo.tombers.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para manejar requests de dislike a proyectos.
 * Contiene el ID del proyecto al que se le quiere dar dislike.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DislikeRequest {

    /**
     * ID del proyecto al que se le quiere dar dislike.
     * Este campo es obligatorio.
     */
    @NotNull(message = "El ID del proyecto es obligatorio")
    private Long projectId;
}

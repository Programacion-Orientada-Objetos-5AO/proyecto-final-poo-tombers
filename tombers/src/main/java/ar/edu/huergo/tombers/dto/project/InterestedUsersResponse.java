package ar.edu.huergo.tombers.dto.project;

import java.util.List;

import ar.edu.huergo.tombers.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de usuarios interesados en un proyecto.
 * Contiene la lista de usuarios que han mostrado interés (dado like)
 * pero aún no son miembros del proyecto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestedUsersResponse {

    /**
     * ID del proyecto.
     */
    private Long projectId;

    /**
     * Título del proyecto.
     */
    private String projectTitle;

    /**
     * Lista de usuarios interesados en el proyecto.
     */
    private List<UserResponse> interestedUsers;

    /**
     * Cantidad total de usuarios interesados.
     */
    private Integer totalInterested;
}

package ar.edu.huergo.tombers.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import ar.edu.huergo.tombers.dto.user.UserResponse;
import ar.edu.huergo.tombers.dto.user.UserUpdateRequest;
import ar.edu.huergo.tombers.entity.User;

/**
 * Mapper para convertir entre entidades User y DTOs relacionados.
 * Utiliza MapStruct para generar implementaciones automáticamente.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Convierte una entidad User a un DTO UserResponse.
     * Mapea el campo usernameField de la entidad al campo username del DTO.
     * Mapea los campos de proyectos de la entidad a los campos correspondientes del DTO.
     *
     * @param user la entidad User
     * @return el DTO UserResponse correspondiente
     */
    @Mapping(target = "username", source = "usernameField")
    @Mapping(target = "createdProjectsIds", source = "createdProjectIds")
    @Mapping(target = "likedProjectsIds", source = "likedProjectIds")
    @Mapping(target = "dislikedProjectsIds", source = "dislikedProjectIds")
    @Mapping(target = "participatingProjectsIds", source = "participatingProjectIds")
    @Mapping(target = "averageRating", ignore = true) // Se calcula en el servicio
    UserResponse toDto(User user);

    /**
     * Convierte un DTO UserResponse a una entidad User.
     * Ignora campos sensibles como id, createdAt, updatedAt, password, email, username, status.
     *
     * @param dto el DTO UserResponse
     * @return la entidad User correspondiente
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toEntity(UserResponse dto);

    /**
     * Actualiza una entidad User con los valores de un UserUpdateRequest.
     * Ignora campos sensibles para evitar sobrescrituras no deseadas.
     *
     * @param user la entidad User a actualizar
     * @param dto el DTO con los nuevos valores
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntity(@MappingTarget User user, UserUpdateRequest dto);
}

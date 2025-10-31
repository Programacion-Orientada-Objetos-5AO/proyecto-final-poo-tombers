package ar.edu.huergo.tombers.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import ar.edu.huergo.tombers.dto.project.ProjectCreateRequest;
import ar.edu.huergo.tombers.dto.project.ProjectResponse;
import ar.edu.huergo.tombers.entity.Project;

/**
 * Mapper para convertir entre entidades Project y DTOs relacionados.
 * Utiliza MapStruct para generar implementaciones automáticamente.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    /**
     * Convierte un ProjectCreateRequest a una entidad Project.
     * Ignora campos id, createdAt, updatedAt, memberIds, likeIds y establece status como ACTIVE.
     *
     * @param request el DTO de creación de proyecto
     * @return la entidad Project correspondiente
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "memberIds", ignore = true)
    @Mapping(target = "likeIds", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    Project toEntity(ProjectCreateRequest request);

    /**
     * Convierte una entidad Project a un DTO ProjectResponse.
     *
     * @param project la entidad Project
     * @return el DTO ProjectResponse correspondiente
     */
    @Mapping(target = "memberIds", source = "memberIds")
    @Mapping(target = "likeIds", source = "likeIds")
    @Mapping(target = "members", ignore = true)
    ProjectResponse toResponse(Project project);

    /**
     * Actualiza una entidad Project con los valores de un ProjectCreateRequest.
     * Ignora valores nulos en el DTO para no sobrescribir campos existentes.
     * También ignora memberIds y likeIds ya que no están en el request.
     *
     * @param project la entidad Project a actualizar
     * @param request el DTO con los nuevos valores
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "memberIds", ignore = true)
    @Mapping(target = "likeIds", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    void updateEntity(@MappingTarget Project project, ProjectCreateRequest request);
}



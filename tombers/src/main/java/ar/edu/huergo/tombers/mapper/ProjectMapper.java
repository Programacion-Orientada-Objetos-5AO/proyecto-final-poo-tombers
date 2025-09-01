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

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    // Ignorar propiedades que no existen en ProjectCreateRequest para evitar errores de compilacion
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    Project toEntity(ProjectCreateRequest request);

    ProjectResponse toResponse(Project project);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Project project, ProjectCreateRequest request);
}



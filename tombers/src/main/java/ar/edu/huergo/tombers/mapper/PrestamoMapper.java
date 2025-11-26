package ar.edu.huergo.tombers.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import ar.edu.huergo.tombers.dto.prestamo.PrestamoRequest;
import ar.edu.huergo.tombers.dto.prestamo.PrestamoResponse;
import ar.edu.huergo.tombers.entity.Prestamo;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PrestamoMapper {

    /**
     * @param request el DTO de creación de Prestamo
     */
    @Mapping(target = "prestamoId", source = "prestamoId")
    @Mapping(target = "libroId", source = "libroId")
    @Mapping(target = "diasPrestamo", ignore = true)
    Prestamo toEntity(PrestamoRequest request);

    /**
     * @param prestamo la entidad Prestamo
     */
    @Mapping(target = "prestamoId", source = "prestamoId")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "libroId", source = "libroId")
    @Mapping(target = "fechaPrestamo", source = "fechaPrestamo")
    @Mapping(target = "fechaDevolucion", ignore = true)
    @Mapping(target = "devuelto", source = "devuelto")
    PrestamoResponse toResponse(Prestamo prestamo);

    /**
     * @param prestamo la entidad Prestamo a actualizar
     * @param request el DTO con los nuevos valores
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "prestamoId", source = "prestamoId")
    @Mapping(target = "libroId", source = "libroId")
    @Mapping(target = "fechaPrestamo", source = "fechaPrestamo")
    void updateEntity(@MappingTarget Prestamo prestamo, PrestamoRequest request);
}




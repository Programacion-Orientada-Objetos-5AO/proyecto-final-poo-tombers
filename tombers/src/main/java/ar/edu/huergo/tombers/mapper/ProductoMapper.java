package ar.edu.huergo.tombers.mapper;


import org.springframework.stereotype.Component;


import ar.edu.huergo.tombers.dto.DTOPResponse;
import ar.edu.huergo.tombers.entity.Producto;
import java.util.List;

@Component
public class ProductoMapper {

    public DTOPResponse toDto(Producto producto){
        return DTOPResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .categoria(producto.getCategoria())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .build();
    }


    public Producto toEntity(DTOPResponse dtoPResponse){
        return Producto.builder()
                .id(dtoPResponse.getId())
                .nombre(dtoPResponse.getNombre())
                .categoria(dtoPResponse.getCategoria())
                .precio(dtoPResponse.getPrecio())
                .stock(dtoPResponse.getStock())
                .build();
    }

    public List<DTOPResponse> toDtoList(List<Producto> productos) {
    return productos.stream()
            .map(this::toDto)
            .toList();
    }

}

package ar.edu.huergo.tombers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;

@Builder
@Data
@AllArgsConstructor
public class DTOPResumenCategoria {

    private String nombreCategoria;
    private Integer totalProductos;
    private Double valorTotalInventario;
    private String productoMasCaro;
    private String productoMasBarato;
    private Double promedioPrecios;

}

package ar.edu.huergo.tombers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;

@Builder
@Data
@AllArgsConstructor
public class DTOPRequest {
    
    private String nombre;
    private String categoria;
    private Double precio;

}

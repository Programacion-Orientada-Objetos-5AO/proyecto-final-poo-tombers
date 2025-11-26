package ar.edu.huergo.tombers.dto.prestamo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;



@Data
@Builder
@AllArgsConstructor

public class PrestamoResponse {
    private Long prestamoId;
    private Long libroId;
    private String nombre;
    private LocalDate fechaPrestamo;
    private LocalDate fechadevolucion;
    private Boolean devuelto;

}

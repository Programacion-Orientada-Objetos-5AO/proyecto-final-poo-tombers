package ar.edu.huergo.tombers.dto.prestamo;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PrestamoRequest {
    @NotNull(message = "El ID del libro es obligatorio")
    private Long libroId;
    @NotNull(message = "El nombre obligatorio")
    private String nombre;
    @NotNull(message = "Rellenar el campo de dias es obligatorio")
    private Integer diasPrestamo;

}

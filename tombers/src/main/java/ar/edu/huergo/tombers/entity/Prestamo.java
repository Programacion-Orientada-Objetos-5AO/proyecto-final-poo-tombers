package ar.edu.huergo.tombers.entity;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long prestamoId;
    private Long libroId;
    private String nombre;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
}

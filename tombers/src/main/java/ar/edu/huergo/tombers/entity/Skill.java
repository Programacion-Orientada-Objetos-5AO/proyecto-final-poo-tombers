package ar.edu.huergo.tombers.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// Clase embebida que representa una habilidad o competencia que un usuario puede poseer.
// Incluye atributos como el nombre de la habilidad y el nivel de expertiz (Principiante, intermedio, avanzado).

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Skill {

    @Column(nullable = false, length = 100)
    private String nombre;

    // Campo de nivel, solo acepta ciertos valores predefinidos
    @Column(nullable = false, length = 50)
    private String nivel; // Ejemplos: "Principiante", "Intermedio", "Avanzado"

}
// Falta implementar logica para agregar a perfil y validad datos

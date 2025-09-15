package ar.edu.huergo.tombers.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// Entidad que representa una habilidad o competencia que un usuario puede poseer.
// Incluye atributos como el nombre de la habilidad y el nivel de expertiz (Principiante, intermedio, avanzado).

@Entity
@Table(name = "skills")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Skill {
    
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    // Campo de nivel, solo acepta ciertos valores predefinidos
    @Column(nullable = false, length = 50)
    private String nivel; // Ejemplos: "Principiante", "Intermedio", "Avanzado"
    
}
// Falta implementar logica para agregar a perfil y validad datos
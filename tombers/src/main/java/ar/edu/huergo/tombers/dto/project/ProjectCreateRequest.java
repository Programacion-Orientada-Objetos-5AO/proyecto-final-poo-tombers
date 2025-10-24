package ar.edu.huergo.tombers.dto.project;

import java.util.List;

import ar.edu.huergo.tombers.entity.Skill;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectCreateRequest {
    
    @NotBlank(message = "El título es requerido")
    @Size(max = 255, message = "El título no puede exceder 255 caracteres")
    private String title;
    
    @NotBlank(message = "La descripción es requerida")
    private String description;
    
    @Size(max = 100, message = "El tipo no puede exceder 100 caracteres")
    private String type;
    
    @Size(max = 100, message = "El lenguaje no puede exceder 100 caracteres")
    private String language;
    
    @Size(max = 100, message = "La duración no puede exceder 100 caracteres")
    private String duration;
    
    @NotNull(message = "El estado es requerido")
    private ProjectStatus status;
    
    private Integer teamMax;
    
    private List<String> technologies;
    
    private List<String> objectives;
    
    private List<Skill> skillsNeeded;
    
    @Min(value = 0, message = "El avance no puede ser negativo")
    @Max(value = 100, message = "El avance no puede superar el 100%")
    private Integer progress;
    
    public enum ProjectStatus {
        ACTIVE, INACTIVE, COMPLETED, ON_HOLD
    }
    
    public enum ObjectiveStatus {
        COMPLETED, IN_PROGRESS, PENDING
    }
}

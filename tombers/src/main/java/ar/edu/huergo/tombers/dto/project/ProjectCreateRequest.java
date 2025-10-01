package ar.edu.huergo.tombers.dto.project;

import java.util.List;

import ar.edu.huergo.tombers.entity.Skill;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectCreateRequest {
    
    @NotBlank(message = "El tÃ­tulo es requerido")
    @Size(max = 255, message = "El tÃ­tulo no puede exceder 255 caracteres")
    private String title;
    
    @NotBlank(message = "La descripciÃ³n es requerida")
    private String description;
    
    @Size(max = 100, message = "El tipo no puede exceder 100 caracteres")
    private String type;
    
    @Size(max = 100, message = "El lenguaje no puede exceder 100 caracteres")
    private String language;
    
    @Size(max = 100, message = "La duraciÃ³n no puede exceder 100 caracteres")
    private String duration;
    
    
    @NotNull(message = "El estado es requerido")
    private ProjectStatus status;
    
    private Integer teamMax;
    
    private List<String> technologies;
    
    private List<String> objectives;
    
    private List<Skill> skillsNeeded;
    
    public enum ProjectStatus {
        ACTIVE, INACTIVE, COMPLETED, ON_HOLD
    }
    
    public enum ObjectiveStatus {
        COMPLETED, IN_PROGRESS, PENDING
    }
}

package ar.edu.huergo.tombers.dto.project;

import java.util.List;

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
    
    private String imageUrl;
    
    @NotNull(message = "El estado es requerido")
    private ProjectStatus status;
    
    private Integer teamMax;
    
    private List<TechnologyRequest> technologies;
    
    private List<ObjectiveRequest> objectives;
    
    private List<String> skillsNeeded;
    
    @Data
    public static class TechnologyRequest {
        private String name;
        private String icon;
    }
    
    @Data
    public static class ObjectiveRequest {
        private String text;
        private String icon;
        private ObjectiveStatus status;
    }
    
    public enum ProjectStatus {
        ACTIVE, INACTIVE, COMPLETED, ON_HOLD
    }
    
    public enum ObjectiveStatus {
        COMPLETED, IN_PROGRESS, PENDING
    }
}

package ar.edu.huergo.tombers.dto.project;

import java.time.LocalDate;
import java.util.List;

import ar.edu.huergo.tombers.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private Integer teamCurrent;
    private Integer teamMax;
    private String duration;
    private String language;
    private String type;
    private List<String> technologies;
    private List<String> objectives;
    private List<String> skillsNeeded;
    private Integer progress;
    private Project.ProjectStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;

}



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
    private ProjectStats stats;
    private List<Technology> technologies;
    private List<Objective> objectives;
    private List<String> skillsNeeded;
    private Integer progress;
    private Project.ProjectStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectStats {
        private Integer teamCurrent;
        private Integer teamMax;
        private String duration;
        private String language;
        private String type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Technology {
        private Long id;
        private String name;
        private String icon;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Objective {
        private Long id;
        private String text;
        private Project.Objective.ObjectiveStatus status;
        private String icon;
    }
}



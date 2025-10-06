package ar.edu.huergo.tombers.dto.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ar.edu.huergo.tombers.dto.project.ProjectMemberSummary;
import ar.edu.huergo.tombers.entity.Project;
import ar.edu.huergo.tombers.entity.Skill;
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
    private String bannerUrl;
    private Integer teamCurrent;
    private Integer teamMax;
    private String duration;
    private String language;
    private String type;
    private List<String> technologies;
    private List<String> objectives;
    private List<Skill> skillsNeeded;
    private List<Long> memberIds;
    private Long creatorId;
    private List<Long> likeIds;
    @Builder.Default
    private List<ProjectMemberSummary> members = new ArrayList<>();
    private Integer progress;
    private Project.ProjectStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;

}

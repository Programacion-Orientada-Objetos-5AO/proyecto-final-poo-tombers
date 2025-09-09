package ar.edu.huergo.tombers.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Embedded
    private ProjectStats stats;

    @ElementCollection
    @CollectionTable(name = "project_technologies", joinColumns = @JoinColumn(name = "project_id"))
    private List<Technology> technologies;

    @ElementCollection
    @CollectionTable(name = "project_objectives", joinColumns = @JoinColumn(name = "project_id"))
    private List<Objective> objectives;

    @ElementCollection
    @CollectionTable(name = "project_skills_needed", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "skill")
    private List<String> skillsNeeded;

    private Integer progress;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    public enum ProjectStatus {
        ACTIVE, INACTIVE, COMPLETED, ON_HOLD
    }

    @Embeddable
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectStats {
        @Column(name = "team_current")
        private Integer teamCurrent;

        @Column(name = "team_max")
        private Integer teamMax;

        private String duration;
        private String language;
        private String type;
    }

    @Embeddable
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Technology {
        private String name;
        private String icon;
    }

    @Embeddable
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Objective {
        @Column(columnDefinition = "TEXT")
        private String text;

        @Enumerated(EnumType.STRING)
        private ObjectiveStatus status;

        private String icon;

        public enum ObjectiveStatus {
            COMPLETED, IN_PROGRESS, PENDING
        }
    }
}

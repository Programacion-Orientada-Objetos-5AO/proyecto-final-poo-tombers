package ar.edu.huergo.tombers.dto.user;

import java.time.LocalDateTime;
import java.util.List;

import ar.edu.huergo.tombers.entity.Skill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private List<Skill> skills;
    private Integer age;
    private String status;
    private String languages;
    private String specialization;
    private String phone;
    private String linkedin;
    private String github;
    private String profilePictureUrl;
    private String portfolio;
    private String bio;
    private List<String> certifications;
    private List<Long> projectsIds;
    private List<String> interests;
    private List<Long> createdProjectsIds;
    private List<Long> likedProjectsIds;
    private List<Long> dislikedProjectsIds;
    private List<Long> participatingProjectsIds;
    private Double averageRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

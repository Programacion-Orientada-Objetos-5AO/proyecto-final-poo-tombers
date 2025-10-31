package ar.edu.huergo.tombers.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberSummary {

    private Long id;
    private String fullName;
    private String email;
    private String profilePictureUrl;
    private boolean creator;
}

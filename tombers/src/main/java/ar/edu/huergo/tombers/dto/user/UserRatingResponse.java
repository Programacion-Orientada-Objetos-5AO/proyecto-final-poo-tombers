package ar.edu.huergo.tombers.dto.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRatingResponse {

    private Long id;
    private Long raterId;
    private Long ratedUserId;
    private Integer rating;
    private String comment;
    private Long projectId;
    private LocalDateTime createdAt;
}

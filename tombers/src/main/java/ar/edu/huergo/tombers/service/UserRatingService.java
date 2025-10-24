package ar.edu.huergo.tombers.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ar.edu.huergo.tombers.dto.user.UserRatingRequest;
import ar.edu.huergo.tombers.dto.user.UserRatingResponse;
import ar.edu.huergo.tombers.entity.Project;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.entity.UserRating;
import ar.edu.huergo.tombers.repository.ProjectRepository;
import ar.edu.huergo.tombers.repository.UserRatingRepository;
import ar.edu.huergo.tombers.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRatingService {

    private final UserRatingRepository userRatingRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public UserRatingResponse createRating(UserRatingRequest request) {
        String raterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User rater = userRepository.findByEmail(raterEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verificar que el proyecto existe
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        // Verificar que el rater es el creador del proyecto
        if (!project.getCreatorId().equals(rater.getId())) {
            throw new AccessDeniedException("Solo el creador del proyecto puede calificar a los participantes");
        }

        // Verificar que el usuario calificado participó en el proyecto
        if (project.getMemberIds() == null || !project.getMemberIds().contains(request.getRatedUserId())) {
            throw new AccessDeniedException("El usuario no participó en este proyecto");
        }

        // Verificar que no haya una calificación previa
        if (userRatingRepository.findByRaterIdAndRatedUserIdAndProjectId(rater.getId(), request.getRatedUserId(), request.getProjectId()).isPresent()) {
            throw new IllegalArgumentException("Ya has calificado a este usuario en este proyecto");
        }

        UserRating rating = UserRating.builder()
                .raterId(rater.getId())
                .ratedUserId(request.getRatedUserId())
                .rating(request.getRating())
                .comment(request.getComment())
                .projectId(request.getProjectId())
                .build();

        UserRating savedRating = userRatingRepository.save(rating);

        return UserRatingResponse.builder()
                .id(savedRating.getId())
                .raterId(savedRating.getRaterId())
                .ratedUserId(savedRating.getRatedUserId())
                .rating(savedRating.getRating())
                .comment(savedRating.getComment())
                .projectId(savedRating.getProjectId())
                .createdAt(savedRating.getCreatedAt())
                .build();
    }

    public List<UserRatingResponse> getRatingsForUser(Long userId) {
        return userRatingRepository.findByRatedUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Double getAverageRatingForUser(Long userId) {
        return userRatingRepository.findAverageRatingByUserId(userId).orElse(0.0);
    }

    private UserRatingResponse toResponse(UserRating rating) {
        return UserRatingResponse.builder()
                .id(rating.getId())
                .raterId(rating.getRaterId())
                .ratedUserId(rating.getRatedUserId())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .projectId(rating.getProjectId())
                .createdAt(rating.getCreatedAt())
                .build();
    }
}

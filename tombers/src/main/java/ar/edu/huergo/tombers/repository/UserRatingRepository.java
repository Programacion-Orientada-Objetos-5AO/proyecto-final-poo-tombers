package ar.edu.huergo.tombers.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.tombers.entity.UserRating;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {

    List<UserRating> findByRatedUserId(Long ratedUserId);

    Optional<UserRating> findByRaterIdAndRatedUserIdAndProjectId(Long raterId, Long ratedUserId, Long projectId);

    @Query("SELECT AVG(ur.rating) FROM UserRating ur WHERE ur.ratedUserId = :userId")
    Optional<Double> findAverageRatingByUserId(@Param("userId") Long userId);

    List<UserRating> findByProjectId(Long projectId);
}

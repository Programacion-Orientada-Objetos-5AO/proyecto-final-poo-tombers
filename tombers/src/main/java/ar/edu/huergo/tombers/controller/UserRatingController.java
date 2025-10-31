package ar.edu.huergo.tombers.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.huergo.tombers.dto.user.UserRatingRequest;
import ar.edu.huergo.tombers.dto.user.UserRatingResponse;
import ar.edu.huergo.tombers.service.UserRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-ratings")
@RequiredArgsConstructor
public class UserRatingController {

    private final UserRatingService userRatingService;

    @PostMapping
    public ResponseEntity<UserRatingResponse> createRating(@Valid @RequestBody UserRatingRequest request) {
        UserRatingResponse response = userRatingService.createRating(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserRatingResponse>> getRatingsForUser(@PathVariable Long userId) {
        List<UserRatingResponse> ratings = userRatingService.getRatingsForUser(userId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/user/{userId}/average")
    public ResponseEntity<Double> getAverageRatingForUser(@PathVariable Long userId) {
        Double average = userRatingService.getAverageRatingForUser(userId);
        return ResponseEntity.ok(average);
    }
}

package ar.edu.huergo.tombers.controller;

import ar.edu.huergo.tombers.dto.user.UserResponse;
import ar.edu.huergo.tombers.dto.user.UserUpdateRequest;
import ar.edu.huergo.tombers.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        UserResponse response = userService.getUserProfile(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateUserProfile(
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequest request) {
        String email = authentication.getName();
        UserResponse response = userService.updateUserProfile(email, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String q) {
        List<UserResponse> users = userService.searchUsers(q);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/available")
    public ResponseEntity<List<UserResponse>> getAvailableUsers() {
        List<UserResponse> users = userService.getAvailableUsers();
        return ResponseEntity.ok(users);
    }
}



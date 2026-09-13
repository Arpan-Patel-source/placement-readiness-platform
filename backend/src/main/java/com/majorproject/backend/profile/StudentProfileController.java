package com.majorproject.backend.profile;

import com.majorproject.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for the student placement profile.
 *
 * Protected endpoints (require "Authorization: Bearer <token>" header):
 *   GET  /api/profile  — retrieve authenticated user's profile
 *   PUT  /api/profile  — update authenticated user's profile
 *
 * The @AuthenticationPrincipal annotation injects the User entity
 * directly from the SecurityContext (set by JwtAuthenticationFilter).
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService profileService;

    @GetMapping
    public ResponseEntity<StudentProfileResponse> getProfile(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(profileService.getProfile(user));
    }

    @PutMapping
    public ResponseEntity<StudentProfileResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody StudentProfileRequest request
    ) {
        return ResponseEntity.ok(profileService.updateProfile(user, request));
    }
}

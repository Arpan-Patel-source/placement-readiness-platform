package com.majorproject.backend.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication.
 *
 * Public endpoints (no JWT required):
 *   POST /api/auth/register  — create a new student account
 *   POST /api/auth/login     — authenticate and receive a JWT
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    /**
     * POST /api/auth/google
     * Accepts a Google ID token from the frontend, verifies it, and either
     * returns a JWT (existing user) or signals that a new account needs to be created.
     */
    @PostMapping("/google")
    public ResponseEntity<GoogleAuthResponse> googleAuth(@Valid @RequestBody GoogleAuthRequest request) {
        return ResponseEntity.ok(authService.googleAuth(request));
    }

    /**
     * POST /api/auth/google/register
     * Completes registration for a new Google user after they confirm their details.
     */
    @PostMapping("/google/register")
    public ResponseEntity<AuthResponse> googleRegister(@Valid @RequestBody GoogleRegisterRequest request) {
        return ResponseEntity.ok(authService.googleRegister(request));
    }
}


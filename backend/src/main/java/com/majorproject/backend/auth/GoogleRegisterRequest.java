package com.majorproject.backend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for completing Google registration.
 * The frontend passes the original Google ID token so the backend can
 * re-verify it and extract the googleId (subject) securely.
 */
@Data
public class GoogleRegisterRequest {

    /** The original Google ID token — backend re-verifies to extract googleId. */
    @NotBlank(message = "Google ID token is required")
    private String idToken;

    @NotBlank(message = "Name is required")
    private String name;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    /** Optional – user's college name */
    private String college;
}

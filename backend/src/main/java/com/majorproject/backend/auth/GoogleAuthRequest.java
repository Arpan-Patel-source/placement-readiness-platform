package com.majorproject.backend.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for Google OAuth sign-in.
 * The frontend sends the ID token obtained from Google's JS SDK.
 */
@Data
public class GoogleAuthRequest {

    @NotBlank(message = "Google ID token is required")
    private String idToken;
}

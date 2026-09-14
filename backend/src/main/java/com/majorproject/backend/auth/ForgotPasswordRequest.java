package com.majorproject.backend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for the POST /api/auth/forgot-password endpoint.
 */
@Data
public class ForgotPasswordRequest {

    @Email(message = "A valid email address is required")
    @NotBlank(message = "Email is required")
    private String email;
}

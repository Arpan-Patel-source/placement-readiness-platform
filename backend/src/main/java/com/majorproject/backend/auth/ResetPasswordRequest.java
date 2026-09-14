package com.majorproject.backend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for the POST /api/auth/reset-password endpoint.
 */
@Data
public class ResetPasswordRequest {

    @Email(message = "A valid email address is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Reset token is required")
    private String token;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}

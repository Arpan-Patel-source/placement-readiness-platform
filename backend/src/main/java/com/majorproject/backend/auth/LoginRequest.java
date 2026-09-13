package com.majorproject.backend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for the POST /api/auth/login endpoint.
 */
@Data
public class LoginRequest {

    @Email(message = "A valid email address is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}

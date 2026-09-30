package com.majorproject.backend.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Extended response DTO for Google OAuth sign-in.
 * Adds flags so the frontend knows whether this was a new or existing user,
 * and carries the user's Google display name for pre-filling the register form.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthResponse {

    /** The app's own JWT (set after login or register). Null when isNewUser=true. */
    private String token;

    private String email;
    private String role;

    /**
     * True when the Google account is not yet linked to any app account.
     * The frontend should redirect to the register page and pre-fill name/email.
     */
    private boolean isNewUser;

    /** Google display name — useful for pre-filling the register form. */
    private String name;
}

package com.majorproject.backend.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.majorproject.backend.jwt.JwtService;
import com.majorproject.backend.profile.StudentProfile;
import com.majorproject.backend.profile.StudentProfileRepository;
import com.majorproject.backend.user.Role;
import com.majorproject.backend.user.User;
import com.majorproject.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles registration and login business logic.
 *
 * On registration:
 * 1. Checks that the email is not already taken.
 * 2. Saves the User with a BCrypt-hashed password and STUDENT role.
 * 3. Auto-creates an empty StudentProfile (name pre-filled from request).
 * 4. Returns a JWT + user info.
 *
 * On login:
 * 1. Delegates credential check to AuthenticationManager.
 * 2. Loads the user and generates a JWT.
 */

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${google.client-id}")
    private String googleClientId;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
        }

        // Save user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .build();
        userRepository.save(user);

        // Auto-create an empty profile with the provided name
        StudentProfile profile = StudentProfile.builder()
                .user(user)
                .fullName(request.getName())
                .build();
        profileRepository.save(profile);

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // AuthenticationManager throws BadCredentialsException if credentials are wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No account found with this email: " + request.getEmail()));

        // Generate a 6-character random alphanumeric reset code
        String resetToken = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordExpiresAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        return ForgotPasswordResponse.builder()
                .message("Password reset token generated successfully. Valid for 15 minutes.")
                .resetToken(resetToken)
                .expiresInMinutes(15)
                .build();
    }

    @Transactional
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No account found with this email: " + request.getEmail()));

        if (user.getResetPasswordToken() == null || !user.getResetPasswordToken().equalsIgnoreCase(request.getToken().trim())) {
            throw new IllegalArgumentException("Invalid password reset token.");
        }

        if (user.getResetPasswordExpiresAt() == null || user.getResetPasswordExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token has expired. Please request a new one.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiresAt(null);
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Verifies a Google ID token and either:
     *  - Logs in an existing user (by googleId or email) and returns a JWT, or
     *  - Returns isNewUser=true with the Google name+email so the frontend can
     *    show a pre-filled register form (the user will confirm and submit normally).
     */
    @Transactional
    public GoogleAuthResponse googleAuth(GoogleAuthRequest request) {
        // 1. Verify the ID token with Google's public keys
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(request.getIdToken());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to verify Google ID token: " + e.getMessage());
        }

        if (idToken == null) {
            throw new IllegalArgumentException("Invalid or expired Google ID token.");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String googleId = payload.getSubject();
        String email    = payload.getEmail();
        String name     = (String) payload.get("name");

        // 2. Check if user already linked by googleId
        Optional<User> byGoogleId = userRepository.findByGoogleId(googleId);
        if (byGoogleId.isPresent()) {
            User user = byGoogleId.get();
            String jwtToken = jwtService.generateToken(user);
            String displayName = profileRepository.findByUser(user)
                    .map(StudentProfile::getFullName)
                    .filter(n -> n != null && !n.isBlank())
                    .orElse(name != null && !name.isBlank() ? name : user.getEmail().split("@")[0]);
            return GoogleAuthResponse.builder()
                    .token(jwtToken)
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .isNewUser(false)
                    .name(displayName)
                    .build();
        }

        // 3. Check if an existing email/password account uses the same email → link it
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User user = byEmail.get();
            user.setGoogleId(googleId);
            userRepository.save(user);
            String jwtToken = jwtService.generateToken(user);
            String displayName = profileRepository.findByUser(user)
                    .map(StudentProfile::getFullName)
                    .filter(n -> n != null && !n.isBlank())
                    .orElse(name != null && !name.isBlank() ? name : user.getEmail().split("@")[0]);
            return GoogleAuthResponse.builder()
                    .token(jwtToken)
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .isNewUser(false)
                    .name(displayName)
                    .build();
        }

        // 4. Brand-new Google user — do NOT auto-create; signal frontend to show register form
        String displayName = (name != null && !name.isBlank()) ? name : email.split("@")[0];
        return GoogleAuthResponse.builder()
                .token(null)
                .email(email)
                .role(null)
                .isNewUser(true)
                .name(displayName)
                .build();
    }

    /**
     * Completes Google registration: creates the account and links the googleId.
     * Called after the user confirms their details in the register form.
     */
    @Transactional
    public AuthResponse googleRegister(GoogleRegisterRequest request) {
        // Re-verify the Google ID token to extract the googleId (subject) securely
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(request.getIdToken());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to verify Google ID token: " + e.getMessage());
        }
        if (idToken == null) {
            throw new IllegalArgumentException("Invalid or expired Google ID token.");
        }

        String googleId = idToken.getPayload().getSubject();
        String email    = idToken.getPayload().getEmail();

        // Guard: if email already registered, just log them in
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already registered: " + email);
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .googleId(googleId)
                .role(Role.STUDENT)
                .build();
        userRepository.save(user);

        StudentProfile profile = StudentProfile.builder()
                .user(user)
                .fullName(request.getName())
                .collegeName(request.getCollege())
                .build();
        profileRepository.save(profile);

        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}



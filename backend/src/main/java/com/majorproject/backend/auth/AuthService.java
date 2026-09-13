package com.majorproject.backend.auth;

import com.majorproject.backend.jwt.JwtService;
import com.majorproject.backend.profile.StudentProfile;
import com.majorproject.backend.profile.StudentProfileRepository;
import com.majorproject.backend.user.Role;
import com.majorproject.backend.user.User;
import com.majorproject.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}

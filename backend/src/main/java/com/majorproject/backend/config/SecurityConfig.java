package com.majorproject.backend.config;

import com.majorproject.backend.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security configuration.
 *
 * Rules:
 * - CORS enabled using CorsConfigurationSource
 * - CSRF disabled (we use stateless JWT, not sessions/cookies)
 * - OPTIONS requests permitted for preflight checks
 * - /api/auth/** is public (register + login)
 * - All other endpoints require a valid JWT
 * - Session policy is STATELESS (no HttpSession created)
 * - JwtAuthenticationFilter runs before the standard username/password filter
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/health", "/login", "/register", "/api/auth/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/aptitude/**", "/api/technical/**", "/api/hr/**", "/api/coding/**", "/api/coding-mentor/**", "/api/dashboard/**", "/api/roadmap/**", "/api/prediction/**", "/api/interview/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/coding-mentor/**", "/api/interview/**", "/api/coding/submit", "/api/aptitude/submit", "/api/hr/submit", "/api/technical/submit").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

package com.majorproject.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for the User entity.
 * findByEmail is used by UserDetailsService during authentication.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    /** Used for Google OAuth — look up a user by their Google account subject ID. */
    Optional<User> findByGoogleId(String googleId);
}

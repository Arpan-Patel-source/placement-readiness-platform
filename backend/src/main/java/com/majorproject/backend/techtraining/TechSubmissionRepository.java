package com.majorproject.backend.techtraining;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TechSubmissionRepository extends JpaRepository<TechSubmission, UUID> {
    List<TechSubmission> findByUserIdOrderByCreatedAtDesc(UUID userId);
    @org.springframework.data.jpa.repository.Query("SELECT t FROM TechSubmission t JOIN com.majorproject.backend.user.User u ON t.userId = u.id WHERE u.email = :email ORDER BY t.createdAt DESC")
    List<TechSubmission> findByUserEmailOrderByCreatedAtDesc(@org.springframework.data.repository.query.Param("email") String email);
}

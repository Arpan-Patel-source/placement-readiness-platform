package com.majorproject.backend.hrtraining;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HrSubmissionRepository extends JpaRepository<HrSubmission, UUID> {
    List<HrSubmission> findByUserIdOrderByCreatedAtDesc(UUID userId);
    @org.springframework.data.jpa.repository.Query("SELECT h FROM HrSubmission h JOIN com.majorproject.backend.user.User u ON h.userId = u.id WHERE u.email = :email ORDER BY h.createdAt DESC")
    List<HrSubmission> findByUserEmailOrderByCreatedAtDesc(@org.springframework.data.repository.query.Param("email") String email);
}

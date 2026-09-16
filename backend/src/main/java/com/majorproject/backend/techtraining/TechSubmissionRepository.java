package com.majorproject.backend.techtraining;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TechSubmissionRepository extends JpaRepository<TechSubmission, UUID> {
    List<TechSubmission> findByUserIdOrderByCreatedAtDesc(UUID userId);
}

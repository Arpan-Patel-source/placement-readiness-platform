package com.majorproject.backend.hrtraining;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HrSubmissionRepository extends JpaRepository<HrSubmission, UUID> {
    List<HrSubmission> findByUserIdOrderByCreatedAtDesc(UUID userId);
}

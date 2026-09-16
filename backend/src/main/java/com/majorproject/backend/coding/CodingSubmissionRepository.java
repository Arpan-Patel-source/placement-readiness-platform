package com.majorproject.backend.coding;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CodingSubmissionRepository extends JpaRepository<CodingSubmission, UUID> {
    List<CodingSubmission> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    List<CodingSubmission> findByUserEmailAndProblemIdOrderByCreatedAtDesc(String userEmail, String problemId);
    long countByUserEmailAndStatus(String userEmail, String status);
}

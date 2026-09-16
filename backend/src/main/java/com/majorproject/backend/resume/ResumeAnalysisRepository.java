package com.majorproject.backend.resume;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, UUID> {

    List<ResumeAnalysis> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<ResumeAnalysis> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<ResumeAnalysis> findByIdAndUserId(UUID id, UUID userId);

    List<ResumeAnalysis> findByUserEmailOrderByCreatedAtDesc(String email);
}

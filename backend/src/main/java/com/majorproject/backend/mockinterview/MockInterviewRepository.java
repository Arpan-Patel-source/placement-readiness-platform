package com.majorproject.backend.mockinterview;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MockInterviewRepository extends JpaRepository<MockInterviewSession, UUID> {
    List<MockInterviewSession> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    List<MockInterviewSession> findByUserEmailAndRoundTypeOrderByCreatedAtDesc(String userEmail, InterviewRoundType roundType);
}

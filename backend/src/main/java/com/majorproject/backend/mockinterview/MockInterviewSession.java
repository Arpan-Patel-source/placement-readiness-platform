package com.majorproject.backend.mockinterview;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mock_interview_sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MockInterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String userEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewRoundType roundType;

    @Column(columnDefinition = "TEXT")
    private String questionsJson; // JSON array of questions

    @Column(columnDefinition = "TEXT")
    private String answersJson; // JSON array of answers

    @Column(columnDefinition = "TEXT")
    private String scoresJson; // JSON array of per-question scores

    private double overallScore;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    private String verdict;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

package com.majorproject.backend.techtraining;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tech_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String testId;

    @Enumerated(EnumType.STRING)
    private TechCategory category;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private int correctCount;

    @Column(nullable = false)
    private int incorrectCount;

    @Column(nullable = false)
    private int unattemptedCount;

    @Column(nullable = false)
    private double scorePercentage;

    private int totalTimeSpentSeconds;

    @Column(length = 255)
    private String verdict;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

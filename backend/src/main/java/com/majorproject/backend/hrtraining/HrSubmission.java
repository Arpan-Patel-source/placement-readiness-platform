package com.majorproject.backend.hrtraining;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "hr_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String promptId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HrCategory category;

    @Column(nullable = false, length = 1000)
    private String questionText;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String userResponse;

    @Column(nullable = false)
    private int overallScore;

    @Column(nullable = false)
    private int situationScore;

    @Column(nullable = false)
    private int taskScore;

    @Column(nullable = false)
    private int actionScore;

    @Column(nullable = false)
    private int resultScore;

    @Column(nullable = false)
    private int clarityScore;

    @Column(length = 255)
    private String verdict;

    @Column(columnDefinition = "TEXT")
    private String strengthsText;

    @Column(columnDefinition = "TEXT")
    private String improvementsText;

    @Column(columnDefinition = "TEXT")
    private String barRaiserModelAnswer;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

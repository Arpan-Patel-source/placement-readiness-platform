package com.majorproject.backend.resume;

import com.majorproject.backend.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity representing an analyzed resume scan.
 * Stores calculated ATS score, strength score, readiness score,
 * extracted skills, missing keywords, and actionable suggestions.
 */
@Entity
@Table(name = "resume_analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String fileName;
    private String fileType;
    private Long fileSizeBytes;

    /** Target role evaluated, e.g. "Java Backend Developer" */
    private String targetRole;

    private Integer atsScore;
    private Integer strengthScore;
    private Integer readinessScore;

    // Detailed Section Breakdown
    private Integer sectionContactScore;
    private Integer sectionStructureScore;
    private Integer sectionSkillsScore;
    private Integer sectionImpactScore;

    @ElementCollection
    @CollectionTable(name = "resume_skills_found", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "skill")
    @Builder.Default
    private List<String> skillsFound = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "resume_missing_skills", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "skill")
    @Builder.Default
    private List<String> missingSkills = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "resume_critical_keywords", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "keyword")
    @Builder.Default
    private List<String> criticalKeywords = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "resume_suggestions", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "suggestion", length = 1000)
    @Builder.Default
    private List<String> actionableSuggestions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "resume_grammar_suggestions", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "issue", length = 1000)
    @Builder.Default
    private List<String> grammarSuggestions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "resume_strengths", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "strength", length = 1000)
    @Builder.Default
    private List<String> strengths = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String executiveSummary;

    @Column(columnDefinition = "TEXT")
    private String rawTextSnippet;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}

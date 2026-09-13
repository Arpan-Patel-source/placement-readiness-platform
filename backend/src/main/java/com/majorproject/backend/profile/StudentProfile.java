package com.majorproject.backend.profile;

import com.majorproject.backend.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity for a student's placement preparation profile.
 * Created automatically (empty) when a user registers.
 * Maps to the "student_profiles" table. Skills are stored
 * in a separate "student_skills" collection table.
 */
@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String fullName;
    private String collegeName;
    private String branch;

    @Column(name = "year_of_study")
    private Integer yearOfStudy;

    /** Target role the student is preparing for, e.g. "Java Backend Developer". */
    private String targetRole;

    @ElementCollection
    @CollectionTable(
            name = "student_skills",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "skill")
    @Builder.Default
    private List<String> skills = new ArrayList<>();

    private String linkedinUrl;
    private String githubUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

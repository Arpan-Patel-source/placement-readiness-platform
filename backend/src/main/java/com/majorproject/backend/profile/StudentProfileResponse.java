package com.majorproject.backend.profile;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO returned by GET and PUT /api/profile.
 */
@Data
@Builder
public class StudentProfileResponse {
    private UUID profileId;
    private String email;
    private String fullName;
    private String collegeName;
    private String branch;
    private Integer yearOfStudy;
    private String targetRole;
    private List<String> skills;
    private String linkedinUrl;
    private String githubUrl;
    private String bio;
    private LocalDateTime updatedAt;
}

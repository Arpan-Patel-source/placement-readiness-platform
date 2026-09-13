package com.majorproject.backend.profile;

import lombok.Data;

import java.util.List;

/**
 * DTO for PUT /api/profile — updates the authenticated student's profile.
 */
@Data
public class StudentProfileRequest {
    private String fullName;
    private String collegeName;
    private String branch;
    private Integer yearOfStudy;
    private String targetRole;
    private List<String> skills;
    private String linkedinUrl;
    private String githubUrl;
    private String bio;
}

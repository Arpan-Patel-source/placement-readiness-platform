package com.majorproject.backend.profile;

import com.majorproject.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing the authenticated student's placement profile.
 *
 * If no profile exists yet (edge case — normally created on register),
 * it is auto-created before returning.
 */
@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository profileRepository;

    /** Fetch the profile for the authenticated user. */
    @Transactional(readOnly = true)
    public StudentProfileResponse getProfile(User user) {
        StudentProfile profile = profileRepository.findByUser(user)
                .orElseGet(() -> createEmptyProfile(user));
        return toResponse(profile);
    }

    /** Update profile fields for the authenticated user. */
    @Transactional
    public StudentProfileResponse updateProfile(User user, StudentProfileRequest request) {
        StudentProfile profile = profileRepository.findByUser(user)
                .orElseGet(() -> createEmptyProfile(user));

        if (request.getFullName() != null)     profile.setFullName(request.getFullName());
        if (request.getCollegeName() != null)  profile.setCollegeName(request.getCollegeName());
        if (request.getBranch() != null)       profile.setBranch(request.getBranch());
        if (request.getYearOfStudy() != null)  profile.setYearOfStudy(request.getYearOfStudy());
        if (request.getTargetRole() != null)   profile.setTargetRole(request.getTargetRole());
        if (request.getSkills() != null)       profile.setSkills(request.getSkills());
        if (request.getLinkedinUrl() != null)  profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null)    profile.setGithubUrl(request.getGithubUrl());
        if (request.getBio() != null)          profile.setBio(request.getBio());

        return toResponse(profileRepository.save(profile));
    }

    // ── Private helpers ─────────────────────────────────────────────────────

    private StudentProfile createEmptyProfile(User user) {
        return profileRepository.save(
                StudentProfile.builder().user(user).build()
        );
    }

    private StudentProfileResponse toResponse(StudentProfile profile) {
        return StudentProfileResponse.builder()
                .profileId(profile.getId())
                .email(profile.getUser().getEmail())
                .fullName(profile.getFullName())
                .collegeName(profile.getCollegeName())
                .branch(profile.getBranch())
                .yearOfStudy(profile.getYearOfStudy())
                .targetRole(profile.getTargetRole())
                .skills(profile.getSkills())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .bio(profile.getBio())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}

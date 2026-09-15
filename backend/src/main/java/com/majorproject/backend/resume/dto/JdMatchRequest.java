package com.majorproject.backend.resume.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdMatchRequest {

    private UUID resumeId; // optional if analyze latest or pass text directly

    private String resumeText; // optional if resumeId provided

    @NotBlank(message = "Job description text is required")
    private String jobDescriptionText;

    private String targetRole; // e.g. "Java Backend Developer"

    private String companyName; // e.g. "Bajaj Finserv", "Amazon", "TCS"
}

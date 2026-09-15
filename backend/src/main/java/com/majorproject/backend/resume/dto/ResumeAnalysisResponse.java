package com.majorproject.backend.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAnalysisResponse {
    private UUID id;
    private String fileName;
    private String fileType;
    private Long fileSizeBytes;
    private String targetRole;
    private Integer atsScore;
    private Integer strengthScore;
    private Integer readinessScore;
    private SectionScoreDto sections;
    private List<String> skillsFound;
    private List<String> missingSkills;
    private List<String> criticalKeywords;
    private List<String> actionableSuggestions;
    private List<String> grammarSuggestions;
    private List<String> strengths;
    private String executiveSummary;
    private LocalDateTime createdAt;
}

package com.majorproject.backend.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtsParsedTreeDto {

    private String candidateName;

    private String detectedEmail;

    private String detectedPhone;

    private String detectedLinkedIn;

    private String detectedGitHub;

    private String detectedLocation;

    private ParsedEducation educationSummary;

    private List<ParsedProject> parsedProjects;

    private List<SkillCategory> skillCategories;

    private ParserHealth health;

    private String rawTextSample;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParsedEducation {
        private String degree;
        private String institution;
        private String gradYear;
        private String grade;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParsedProject {
        private String title;
        private String role;
        private String duration;
        private List<String> bulletPoints;
        private boolean hasLiveUrl;
        private boolean hasMetrics;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillCategory {
        private String categoryName; // "Languages", "Frameworks & Backend", "Databases", "DevOps & Tools"
        private List<String> skills;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParserHealth {
        private String status; // "EXCELLENT", "GOOD", "WARNING", "POOR"
        private int totalWordCount;
        private boolean singlePageFit;
        private boolean hasUnparseableCharacters;
        private boolean hasLegacyBiodataClutter;
        private List<String> warnings;
    }
}

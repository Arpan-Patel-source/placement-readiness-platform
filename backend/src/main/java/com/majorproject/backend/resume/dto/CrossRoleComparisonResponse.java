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
public class CrossRoleComparisonResponse {

    private String candidateName;

    private String primaryRoleAnalyzed;

    private List<RoleScoreItem> roleScores;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleScoreItem {
        private String roleTitle;
        private int atsScore;
        private int readinessScore;
        private int matchedSkillCount;
        private int totalPrimarySkillCount;
        private List<String> matchedSkills;
        private List<String> topMissingSkills;
        private String suitabilityBadge; // "Strong Fit", "Good Fit", "Needs Upskilling", "Pivot Required"
        private String transitionAdvice;
    }
}

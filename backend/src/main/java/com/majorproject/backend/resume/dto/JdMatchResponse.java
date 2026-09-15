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
public class JdMatchResponse {

    private int matchScore; // 0 - 100%

    private String matchVerdict; // "Strong Match", "Competitive", "Moderate Gap", "High Skill Gap"

    private String companyName;

    private String targetRole;

    private int totalJdKeywordsFound;

    private int totalJdKeywordsExtracted;

    private List<String> matchedSkills;

    private List<String> missingMustHaveSkills;

    private List<String> missingGoodToHaveKeywords;

    private List<String> tailoringTips;
}

package com.majorproject.backend.codingmentor;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MentorAnalysisResponse {
    private String problemId;
    private String problemTitle;
    private String detectedApproach;
    private String timeComplexity;
    private String spaceComplexity;
    private String optimalTimeComplexity;
    private String optimalSpaceComplexity;
    private boolean isOptimal;
    private String betterApproach;
    private String betterApproachExplanation;
    private List<String> codeQualitySuggestions;
    private List<String> optimizationTips;
    private String overallVerdict;
    private int codeQualityScore; // 0-100
}

package com.majorproject.backend.prediction;

import lombok.*;
import java.util.List;

/**
 * Company profile with placement requirements and weights.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CompanyProfile {
    private String name;
    private double aptitudeWeight;
    private double codingWeight;
    private double hrWeight;
    private double technicalWeight;
    private double resumeWeight;
    private double aptitudeCutoff;   // minimum aptitude score needed
    private double codingCutoff;     // minimum coding score needed
    private List<String> commonTopics;
    private String aptitudePattern;
    private String codingDifficulty;
    private String interviewStyle;
}

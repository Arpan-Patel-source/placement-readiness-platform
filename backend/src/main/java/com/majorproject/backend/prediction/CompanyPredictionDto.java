package com.majorproject.backend.prediction;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CompanyPredictionDto {
    private String companyName;
    private double placementProbability;
    private double interviewSuccessRate;
    private String prepAdvice;
    private List<String> focusAreas;
    private List<String> previousQuestionTopics;
    private String aptitudePattern;
    private String codingDifficulty;
    private String interviewStyle;
}

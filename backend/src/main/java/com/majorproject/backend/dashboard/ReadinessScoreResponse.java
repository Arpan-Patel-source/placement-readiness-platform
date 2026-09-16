package com.majorproject.backend.dashboard;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReadinessScoreResponse {
    private double resumeScore;
    private double codingScore;
    private double aptitudeScore;
    private double hrScore;
    private double technicalScore;
    private double interviewScore;
    private double overallScore;
    private List<String> weakAreas;
    private List<String> strongAreas;
    private int totalCodingSubmissions;
    private int solvedProblems;
    private String readinessVerdict;
}

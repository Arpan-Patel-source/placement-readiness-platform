package com.majorproject.backend.hrtraining.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrEvaluationResponse {
    private String id;
    private int overallScore;
    private int situationScore;
    private int taskScore;
    private int actionScore;
    private int resultScore;
    private int clarityScore;
    private String verdict;
    private String executiveSummary;
    private StarBreakdown starBreakdown;
    private List<String> strengths;
    private List<String> areasForImprovement;
    private String barRaiserModelAnswer;
    private int wordCount;
    private List<String> metricsDetected;
    private List<String> actionVerbsDetected;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StarBreakdown {
        private ComponentScore situation;
        private ComponentScore task;
        private ComponentScore action;
        private ComponentScore result;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentScore {
        private int score;
        private boolean detected;
        private String status;
        private String feedback;
    }
}

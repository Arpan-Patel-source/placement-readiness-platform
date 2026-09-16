package com.majorproject.backend.mockinterview.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InterviewResultResponse {
    private String sessionId;
    private String roundType;
    private double overallScore;
    private String verdict;
    private String executiveSummary;
    private List<QuestionResult> questionResults;
    private List<String> strengths;
    private List<String> areasForImprovement;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class QuestionResult {
        private String questionId;
        private String question;
        private String answer;
        private int score;
        private String verdict;
        private List<String> feedback;
    }
}

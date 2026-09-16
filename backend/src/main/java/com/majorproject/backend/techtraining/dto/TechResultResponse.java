package com.majorproject.backend.techtraining.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechResultResponse {
    private String testId;
    private int totalQuestions;
    private int correctCount;
    private int incorrectCount;
    private int unattemptedCount;
    private double scorePercentage;
    private int totalTimeSpentSeconds;
    private String performanceVerdict;
    private String performanceFeedback;
    private List<TopicBreakdown> topicBreakdowns;
    private List<QuestionReview> questionReviews;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicBreakdown {
        private String topic;
        private int total;
        private int correct;
        private double accuracy;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionReview {
        private String questionId;
        private String topic;
        private String question;
        private List<String> options;
        private Integer selectedOptionIndex;
        private int correctOptionIndex;
        private boolean isCorrect;
        private boolean isAttempted;
        private String explanation;
        private String conceptTip;
    }
}

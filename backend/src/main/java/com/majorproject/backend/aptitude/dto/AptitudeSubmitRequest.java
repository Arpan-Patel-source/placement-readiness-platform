package com.majorproject.backend.aptitude.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AptitudeSubmitRequest {
    private String testId;
    private String category;
    private String topic;
    private int totalTimeSpentSeconds;
    private List<AnswerSubmission> answers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerSubmission {
        private String questionId;
        private Integer selectedOptionIndex; // null if skipped
        private Integer timeSpentSeconds;
    }
}

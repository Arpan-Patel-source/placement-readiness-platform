package com.majorproject.backend.mockinterview.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InterviewHistoryItem {
    private String sessionId;
    private String roundType;
    private double overallScore;
    private String verdict;
    private int totalQuestions;
    private String createdAt;
}

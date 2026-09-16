package com.majorproject.backend.mockinterview.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StartInterviewResponse {
    private String sessionId;
    private String roundType;
    private List<Map<String, String>> questions;
    private int totalQuestions;
}

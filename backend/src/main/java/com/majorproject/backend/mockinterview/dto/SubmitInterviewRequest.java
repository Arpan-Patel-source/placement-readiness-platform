package com.majorproject.backend.mockinterview.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubmitInterviewRequest {
    private String sessionId;
    private List<AnswerEntry> answers;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AnswerEntry {
        private String questionId;
        private String question;
        private String answer;
    }
}

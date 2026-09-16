package com.majorproject.backend.mockinterview.dto;

import com.majorproject.backend.mockinterview.InterviewRoundType;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StartInterviewRequest {
    private InterviewRoundType roundType;
    private List<String> skills; // for technical round — skills from resume
    private int questionCount;   // default 5
}

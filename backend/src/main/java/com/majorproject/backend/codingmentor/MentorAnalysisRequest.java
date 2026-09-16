package com.majorproject.backend.codingmentor;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MentorAnalysisRequest {
    private String problemId;
    private String code;
    private String language; // JAVA, PYTHON, CPP, C
}

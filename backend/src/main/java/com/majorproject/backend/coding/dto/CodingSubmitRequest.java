package com.majorproject.backend.coding.dto;

import com.majorproject.backend.coding.CodingLanguage;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CodingSubmitRequest {
    private String problemId;
    private CodingLanguage language;
    private String code;
    private boolean runSampleOnly; // true = only run sample test cases, false = run all
}

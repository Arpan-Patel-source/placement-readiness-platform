package com.majorproject.backend.coding.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CodingHistoryItem {
    private String submissionId;
    private String problemId;
    private String problemTitle;
    private String category;
    private String difficulty;
    private String language;
    private String status;
    private int passedTestCases;
    private int totalTestCases;
    private long runtimeMs;
    private String createdAt;
}

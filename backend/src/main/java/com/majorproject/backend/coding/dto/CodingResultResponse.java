package com.majorproject.backend.coding.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CodingResultResponse {
    private String submissionId;
    private String problemId;
    private String status; // ACCEPTED, WRONG_ANSWER, TIME_LIMIT_EXCEEDED, RUNTIME_ERROR, COMPILATION_ERROR
    private int passedTestCases;
    private int totalTestCases;
    private long runtimeMs;
    private long memoryKb;
    private List<TestCaseResult> testCaseResults;
    private String verdict;
    private String feedback;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TestCaseResult {
        private int testCaseIndex;
        private boolean passed;
        private String input;
        private String expectedOutput;
        private String actualOutput;
        private boolean isHidden;
    }
}

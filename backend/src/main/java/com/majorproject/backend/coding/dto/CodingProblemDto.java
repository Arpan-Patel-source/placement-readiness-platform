package com.majorproject.backend.coding.dto;

import com.majorproject.backend.coding.*;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CodingProblemDto {
    private String id;
    private String title;
    private String description;
    private CodingCategory category;
    private CodingDifficulty difficulty;
    private String constraints;
    private String inputFormat;
    private String outputFormat;
    private List<TestCaseDto> sampleTestCases;
    private int totalTestCases;
    private String starterCodeJava;
    private String starterCodePython;
    private String starterCodeCpp;
    private String starterCodeC;
    private List<String> hints;
    private String timeComplexity;
    private String spaceComplexity;
    private List<String> companiesAsked;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TestCaseDto {
        private String input;
        private String expectedOutput;
    }
}

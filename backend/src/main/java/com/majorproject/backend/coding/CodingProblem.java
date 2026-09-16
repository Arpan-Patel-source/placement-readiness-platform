package com.majorproject.backend.coding;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodingProblem {
    private String id;
    private String title;
    private String description;
    private CodingCategory category;
    private CodingDifficulty difficulty;
    private String constraints;
    private String inputFormat;
    private String outputFormat;
    private List<TestCase> testCases;
    private String starterCodeJava;
    private String starterCodePython;
    private String starterCodeCpp;
    private String starterCodeC;
    private List<String> hints;
    private String optimalApproach;
    private String timeComplexity;
    private String spaceComplexity;
    private List<String> companiesAsked;
}

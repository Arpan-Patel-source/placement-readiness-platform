package com.majorproject.backend.techtraining;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechQuestion {
    private String id;
    private TechCategory category;
    private String topic;
    private TechDifficulty difficulty;
    private String question;
    private List<String> options;
    private int correctOptionIndex;
    private String explanation;
    private String conceptTip;
    private List<String> companiesAsked;
}

package com.majorproject.backend.hrtraining.dto;

import com.majorproject.backend.hrtraining.HrCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrPromptDto {
    private String id;
    private HrCategory category;
    private String categoryTitle;
    private String question;
    private String recruiterIntent;
    private List<String> keyPointsToInclude;
    private List<String> commonPitfalls;
    private String sampleModelAnswer;
    private List<String> companyTags;
}

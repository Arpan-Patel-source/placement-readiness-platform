package com.majorproject.backend.aptitude.dto;

import com.majorproject.backend.aptitude.AptitudeCategory;
import com.majorproject.backend.aptitude.AptitudeDifficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AptitudeQuestionDto {
    private String id;
    private AptitudeCategory category;
    private String topic;
    private AptitudeDifficulty difficulty;
    private String question;
    private List<String> options;
    private Integer correctOptionIndex;
    private String explanation;
    private String formulaTip;
    private List<String> companiesAsked;
}

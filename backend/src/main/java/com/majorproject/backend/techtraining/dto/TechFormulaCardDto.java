package com.majorproject.backend.techtraining.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechFormulaCardDto {
    private String category;
    private String topic;
    private String title;
    private String formula;
    private String tip;
    private String example;
}

package com.majorproject.backend.techtraining.dto;

import com.majorproject.backend.techtraining.TechCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechHistoryItemDto {
    private String id;
    private String testId;
    private TechCategory category;
    private String categoryTitle;
    private int totalQuestions;
    private int correctCount;
    private double scorePercentage;
    private String verdict;
    private LocalDateTime createdAt;
}

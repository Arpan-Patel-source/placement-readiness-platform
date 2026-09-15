package com.majorproject.backend.hrtraining.dto;

import com.majorproject.backend.hrtraining.HrCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrHistoryItemDto {
    private String id;
    private String promptId;
    private HrCategory category;
    private String categoryTitle;
    private String questionText;
    private String userResponseSnippet;
    private int overallScore;
    private String verdict;
    private LocalDateTime createdAt;
}

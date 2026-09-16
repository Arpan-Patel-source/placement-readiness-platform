package com.majorproject.backend.coding.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CodingCategorySummaryDto {
    private String category;
    private String displayName;
    private int totalProblems;
    private int easyCount;
    private int mediumCount;
    private int hardCount;
}

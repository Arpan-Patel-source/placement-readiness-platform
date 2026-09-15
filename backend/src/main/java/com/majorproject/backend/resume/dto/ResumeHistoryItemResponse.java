package com.majorproject.backend.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeHistoryItemResponse {
    private UUID id;
    private String fileName;
    private String targetRole;
    private Integer atsScore;
    private Integer strengthScore;
    private Integer readinessScore;
    private LocalDateTime createdAt;
}

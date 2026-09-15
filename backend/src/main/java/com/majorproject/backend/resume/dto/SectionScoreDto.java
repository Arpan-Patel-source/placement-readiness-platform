package com.majorproject.backend.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionScoreDto {
    private Integer contactScore;
    private Integer structureScore;
    private Integer skillsScore;
    private Integer impactScore;
}

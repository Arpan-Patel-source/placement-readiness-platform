package com.majorproject.backend.aptitude.dto;

import com.majorproject.backend.aptitude.AptitudeCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AptitudeCategorySummaryDto {
    private AptitudeCategory category;
    private String title;
    private String description;
    private int totalQuestions;
    private List<TopicSummary> topics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicSummary {
        private String topicId;
        private String topicName;
        private int questionCount;
        private String keyConcept;
    }
}

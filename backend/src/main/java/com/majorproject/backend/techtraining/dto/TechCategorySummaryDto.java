package com.majorproject.backend.techtraining.dto;

import com.majorproject.backend.techtraining.TechCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechCategorySummaryDto {
    private TechCategory category;
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

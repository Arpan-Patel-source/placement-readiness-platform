package com.majorproject.backend.roadmap;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoadmapResponse {
    private String studentName;
    private double currentReadiness;
    private List<WeekPlan> weeks;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class WeekPlan {
        private String week;
        private String focus;
        private String rationale;
        private List<String> tasks;
    }
}

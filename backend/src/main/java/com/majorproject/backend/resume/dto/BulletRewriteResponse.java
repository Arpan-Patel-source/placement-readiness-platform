package com.majorproject.backend.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulletRewriteResponse {

    private String originalBullet;

    /**
     * Variation 1: Quantified Impact (Google XYZ formula: Accomplished [X] measured by [Y] by doing [Z])
     */
    private RewriteOption quantifiedXyz;

    /**
     * Variation 2: Enterprise Architecture & Stack Depth (Focused on scalability, caching, indexing)
     */
    private RewriteOption enterpriseStack;

    /**
     * Variation 3: Active Leadership & End-to-End Delivery (Ownership, problem solving, impact)
     */
    private RewriteOption leadershipImpact;

    private List<String> improvementsApplied;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RewriteOption {
        private String title;
        private String text;
        private String formula; // e.g. "Google XYZ Method", "STAR Method", "Action-Impact"
        private String highlightMetric;
        private List<String> keywordsEmbedded;
    }
}

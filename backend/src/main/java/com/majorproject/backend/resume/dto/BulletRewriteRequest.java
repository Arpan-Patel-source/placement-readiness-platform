package com.majorproject.backend.resume.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulletRewriteRequest {

    @NotBlank(message = "Bullet point text is required")
    private String bulletText;

    private String roleTitle;

    private String targetMetric; // e.g. "latency", "scale", "performance", "cost"

    private String projectContext; // e.g. "MindBridge AI platform"
}

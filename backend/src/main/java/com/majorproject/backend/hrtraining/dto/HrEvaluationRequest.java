package com.majorproject.backend.hrtraining.dto;

import com.majorproject.backend.hrtraining.HrCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrEvaluationRequest {
    private String promptId;
    private HrCategory category;
    private String questionText;

    @NotBlank(message = "Response cannot be blank")
    private String userResponse;
}

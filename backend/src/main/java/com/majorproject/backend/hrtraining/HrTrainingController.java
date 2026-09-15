package com.majorproject.backend.hrtraining;

import com.majorproject.backend.hrtraining.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor
public class HrTrainingController {

    private final HrTrainingService hrTrainingService;

    @GetMapping("/prompts")
    public ResponseEntity<List<HrPromptDto>> getPrompts(@RequestParam(required = false) HrCategory category) {
        return ResponseEntity.ok(hrTrainingService.getPrompts(category));
    }

    @GetMapping("/prompts/{id}")
    public ResponseEntity<HrPromptDto> getPromptById(@PathVariable String id) {
        return hrTrainingService.getPromptById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/evaluate")
    public ResponseEntity<HrEvaluationResponse> evaluateResponse(
            Principal principal,
            @Valid @RequestBody HrEvaluationRequest request
    ) {
        return ResponseEntity.ok(hrTrainingService.evaluateResponse(principal.getName(), request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<HrHistoryItemDto>> getHistory(Principal principal) {
        return ResponseEntity.ok(hrTrainingService.getUserHistory(principal.getName()));
    }
}

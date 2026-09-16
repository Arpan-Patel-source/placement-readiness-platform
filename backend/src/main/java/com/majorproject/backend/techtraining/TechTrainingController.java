package com.majorproject.backend.techtraining;

import com.majorproject.backend.techtraining.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/technical")
@RequiredArgsConstructor
public class TechTrainingController {

    private final TechTrainingService techTrainingService;

    @GetMapping("/categories")
    public ResponseEntity<List<TechCategorySummaryDto>> getCategories() {
        return ResponseEntity.ok(techTrainingService.getCategorySummaries());
    }

    @GetMapping("/questions")
    public ResponseEntity<List<TechQuestionDto>> getQuestions(
            @RequestParam(required = false) TechCategory category,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) TechDifficulty difficulty,
            @RequestParam(required = false, defaultValue = "50") Integer limit
    ) {
        return ResponseEntity.ok(techTrainingService.getQuestions(category, topic, difficulty, limit));
    }

    @GetMapping("/mock-test")
    public ResponseEntity<List<TechQuestionDto>> getMockTest(
            @RequestParam(required = false) TechCategory category,
            @RequestParam(required = false, defaultValue = "10") Integer count
    ) {
        return ResponseEntity.ok(techTrainingService.generateMockTest(category, count));
    }

    @PostMapping("/submit")
    public ResponseEntity<TechResultResponse> submitTest(
            Principal principal,
            @RequestBody TechSubmitRequest request
    ) {
        return ResponseEntity.ok(techTrainingService.evaluateTest(principal.getName(), request));
    }

    @GetMapping("/cheatsheet")
    public ResponseEntity<List<TechFormulaCardDto>> getCheatsheet() {
        return ResponseEntity.ok(techTrainingService.getConceptCheatsheet());
    }

    @GetMapping("/history")
    public ResponseEntity<List<TechHistoryItemDto>> getHistory(Principal principal) {
        return ResponseEntity.ok(techTrainingService.getUserHistory(principal.getName()));
    }
}

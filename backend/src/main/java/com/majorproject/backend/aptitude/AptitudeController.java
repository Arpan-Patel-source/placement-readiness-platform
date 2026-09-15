package com.majorproject.backend.aptitude;

import com.majorproject.backend.aptitude.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aptitude")
@RequiredArgsConstructor
public class AptitudeController {

    private final AptitudeService aptitudeService;

    @GetMapping("/categories")
    public ResponseEntity<List<AptitudeCategorySummaryDto>> getCategories() {
        return ResponseEntity.ok(aptitudeService.getCategorySummaries());
    }

    @GetMapping("/questions")
    public ResponseEntity<List<AptitudeQuestionDto>> getQuestions(
            @RequestParam(required = false) AptitudeCategory category,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) AptitudeDifficulty difficulty,
            @RequestParam(required = false, defaultValue = "50") Integer limit
    ) {
        return ResponseEntity.ok(aptitudeService.getQuestions(category, topic, difficulty, limit));
    }

    @GetMapping("/mock-test")
    public ResponseEntity<List<AptitudeQuestionDto>> getMockTest(
            @RequestParam(required = false) AptitudeCategory category,
            @RequestParam(required = false, defaultValue = "10") Integer count
    ) {
        return ResponseEntity.ok(aptitudeService.generateMockTest(category, count));
    }

    @PostMapping("/submit")
    public ResponseEntity<AptitudeResultResponse> submitTest(@RequestBody AptitudeSubmitRequest request) {
        return ResponseEntity.ok(aptitudeService.evaluateTest(request));
    }

    @GetMapping("/cheatsheet")
    public ResponseEntity<List<FormulaCardDto>> getCheatsheet() {
        return ResponseEntity.ok(aptitudeService.getFormulaCheatsheet());
    }
}

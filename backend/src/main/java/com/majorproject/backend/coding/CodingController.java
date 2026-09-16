package com.majorproject.backend.coding;

import com.majorproject.backend.coding.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coding")
@RequiredArgsConstructor
public class CodingController {

    private final CodingService codingService;

    @GetMapping("/categories")
    public ResponseEntity<List<CodingCategorySummaryDto>> getCategories() {
        return ResponseEntity.ok(codingService.getCategorySummaries());
    }

    @GetMapping("/problems")
    public ResponseEntity<List<CodingProblemDto>> getProblems(
            @RequestParam(required = false) CodingCategory category,
            @RequestParam(required = false) CodingDifficulty difficulty,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(codingService.getProblems(category, difficulty, search));
    }

    @GetMapping("/problems/{id}")
    public ResponseEntity<CodingProblemDto> getProblemById(@PathVariable String id) {
        return codingService.getProblemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/submit")
    public ResponseEntity<CodingResultResponse> submitSolution(
            Principal principal,
            @RequestBody CodingSubmitRequest request
    ) {
        String email = principal != null ? principal.getName() : "guest@placementai.internal";
        return ResponseEntity.ok(codingService.submitSolution(email, request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<CodingHistoryItem>> getHistory(Principal principal) {
        String email = principal != null ? principal.getName() : "guest@placementai.internal";
        return ResponseEntity.ok(codingService.getUserHistory(email));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(Principal principal) {
        String email = principal != null ? principal.getName() : "guest@placementai.internal";
        return ResponseEntity.ok(codingService.getUserStats(email));
    }
}

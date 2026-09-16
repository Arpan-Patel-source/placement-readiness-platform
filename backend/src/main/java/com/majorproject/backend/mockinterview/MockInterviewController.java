package com.majorproject.backend.mockinterview;

import com.majorproject.backend.mockinterview.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class MockInterviewController {

    private final MockInterviewService interviewService;

    @PostMapping("/start")
    public ResponseEntity<StartInterviewResponse> startInterview(
            Principal principal,
            @RequestBody StartInterviewRequest request
    ) {
        String email = principal != null ? principal.getName() : "guest@placementai.internal";
        return ResponseEntity.ok(interviewService.startInterview(email, request));
    }

    @PostMapping("/submit")
    public ResponseEntity<InterviewResultResponse> submitAnswers(
            Principal principal,
            @RequestBody SubmitInterviewRequest request
    ) {
        String email = principal != null ? principal.getName() : "guest@placementai.internal";
        return ResponseEntity.ok(interviewService.submitAnswers(email, request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<InterviewHistoryItem>> getHistory(Principal principal) {
        String email = principal != null ? principal.getName() : "guest@placementai.internal";
        return ResponseEntity.ok(interviewService.getHistory(email));
    }
}

package com.majorproject.backend.codingmentor;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coding-mentor")
@RequiredArgsConstructor
public class CodingMentorController {

    private final CodingMentorService mentorService;

    @PostMapping("/analyze")
    public ResponseEntity<MentorAnalysisResponse> analyze(@RequestBody MentorAnalysisRequest request) {
        return ResponseEntity.ok(mentorService.analyze(request));
    }
}

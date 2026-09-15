package com.majorproject.backend.resume;

import com.majorproject.backend.resume.dto.ResumeAnalysisResponse;
import com.majorproject.backend.resume.dto.ResumeHistoryItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeAnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;

    /**
     * Upload and analyze a student's resume (PDF or DOCX).
     * Calculates ATS score, strength score, skill gaps, keywords, and actionable recommendations.
     */
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeAnalysisResponse> analyzeResume(
            Principal principal,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "targetRole", required = false) String targetRole
    ) {
        ResumeAnalysisResponse response = resumeAnalysisService.analyzeResume(principal.getName(), file, targetRole);
        return ResponseEntity.ok(response);
    }

    /**
     * Fetch the most recent resume analysis for the authenticated student.
     */
    @GetMapping("/latest")
    public ResponseEntity<ResumeAnalysisResponse> getLatestAnalysis(Principal principal) {
        ResumeAnalysisResponse response = resumeAnalysisService.getLatestAnalysis(principal.getName());
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Get the history of previous resume scans for the authenticated student.
     */
    @GetMapping("/history")
    public ResponseEntity<List<ResumeHistoryItemResponse>> getHistory(Principal principal) {
        List<ResumeHistoryItemResponse> history = resumeAnalysisService.getHistory(principal.getName());
        return ResponseEntity.ok(history);
    }

    /**
     * Get full details of a specific resume scan.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResumeAnalysisResponse> getAnalysisById(
            Principal principal,
            @PathVariable("id") UUID id
    ) {
        ResumeAnalysisResponse response = resumeAnalysisService.getAnalysisById(principal.getName(), id);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a specific resume scan.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnalysis(
            Principal principal,
            @PathVariable("id") UUID id
    ) {
        resumeAnalysisService.deleteAnalysis(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}

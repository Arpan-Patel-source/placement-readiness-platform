package com.majorproject.backend.resume;

import com.majorproject.backend.resume.dto.*;
import jakarta.validation.Valid;
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

    /**
     * ENHANCEMENT 1: AI Bullet Point Rewriter Studio
     * Rewrites project bullet points into Google XYZ and STAR high-impact formats.
     */
    @PostMapping("/rewrite-bullet")
    public ResponseEntity<BulletRewriteResponse> rewriteBullet(
            @Valid @RequestBody BulletRewriteRequest request
    ) {
        BulletRewriteResponse response = resumeAnalysisService.rewriteBullet(request);
        return ResponseEntity.ok(response);
    }

    /**
     * ENHANCEMENT 2: Custom Job Description (JD) Matcher
     * Compares resume against a pasted recruiter job posting.
     */
    @PostMapping("/match-jd")
    public ResponseEntity<JdMatchResponse> matchJobDescription(
            Principal principal,
            @Valid @RequestBody JdMatchRequest request
    ) {
        JdMatchResponse response = resumeAnalysisService.matchJobDescription(principal.getName(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * ENHANCEMENT 3: Cross-Role Readiness Benchmark
     * Compares resume suitability across all 6 engineering tracks.
     */
    @GetMapping("/cross-role")
    public ResponseEntity<CrossRoleComparisonResponse> getCrossRoleComparison(
            Principal principal,
            @RequestParam(value = "resumeId", required = false) UUID resumeId
    ) {
        CrossRoleComparisonResponse response = resumeAnalysisService.getCrossRoleComparison(principal.getName(), resumeId);
        return ResponseEntity.ok(response);
    }

    /**
     * ENHANCEMENT 4: ATS Parser Inspector (Recruiter Bot View)
     * Returns structured parsed ATS tree (contact, education, projects, health warnings).
     */
    @GetMapping("/parser-tree")
    public ResponseEntity<AtsParsedTreeDto> getAtsParsedTree(
            Principal principal,
            @RequestParam(value = "resumeId", required = false) UUID resumeId
    ) {
        AtsParsedTreeDto response = resumeAnalysisService.getAtsParsedTree(principal.getName(), resumeId);
        return ResponseEntity.ok(response);
    }
}

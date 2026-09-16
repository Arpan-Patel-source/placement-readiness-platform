package com.majorproject.backend.dashboard;

import com.majorproject.backend.resume.ResumeAnalysis;
import com.majorproject.backend.resume.ResumeAnalysisRepository;
import com.majorproject.backend.coding.CodingSubmission;
import com.majorproject.backend.coding.CodingSubmissionRepository;
import com.majorproject.backend.hrtraining.HrSubmission;
import com.majorproject.backend.hrtraining.HrSubmissionRepository;
import com.majorproject.backend.techtraining.TechSubmission;
import com.majorproject.backend.techtraining.TechSubmissionRepository;
import com.majorproject.backend.mockinterview.MockInterviewSession;
import com.majorproject.backend.mockinterview.MockInterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ResumeAnalysisRepository resumeRepo;
    private final CodingSubmissionRepository codingRepo;
    private final HrSubmissionRepository hrRepo;
    private final TechSubmissionRepository techRepo;
    private final MockInterviewRepository interviewRepo;

    public ReadinessScoreResponse getReadinessScore(String userEmail) {
        // ─── Resume Score ────────────────────────────────────────────────
        double resumeScore = 0;
        List<ResumeAnalysis> resumes = resumeRepo.findByUserEmailOrderByCreatedAtDesc(userEmail);
        if (!resumes.isEmpty()) {
            resumeScore = resumes.get(0).getAtsScore();
        }

        // ─── Coding Score ────────────────────────────────────────────────
        double codingScore = 0;
        List<CodingSubmission> codingSubs = codingRepo.findByUserEmailOrderByCreatedAtDesc(userEmail);
        int totalCodingSubs = codingSubs.size();
        long acceptedCount = codingSubs.stream().filter(s -> "ACCEPTED".equals(s.getStatus())).count();
        Set<String> solvedIds = codingSubs.stream()
                .filter(s -> "ACCEPTED".equals(s.getStatus()))
                .map(CodingSubmission::getProblemId)
                .collect(Collectors.toSet());
        int solvedProblems = solvedIds.size();
        if (totalCodingSubs > 0) {
            codingScore = Math.min(100, (double) acceptedCount / totalCodingSubs * 100.0);
        }

        // ─── HR Score ────────────────────────────────────────────────────
        double hrScore = 0;
        List<HrSubmission> hrSubs = hrRepo.findByUserEmailOrderByCreatedAtDesc(userEmail);
        if (!hrSubs.isEmpty()) {
            hrScore = hrSubs.stream().mapToDouble(HrSubmission::getOverallScore).average().orElse(0);
        }

        // ─── Technical Score ─────────────────────────────────────────────
        double techScore = 0;
        List<TechSubmission> techSubs = techRepo.findByUserEmailOrderByCreatedAtDesc(userEmail);
        if (!techSubs.isEmpty()) {
            techScore = techSubs.stream().mapToDouble(TechSubmission::getScorePercentage).average().orElse(0);
        }

        // ─── Interview Score ─────────────────────────────────────────────
        double interviewScore = 0;
        List<MockInterviewSession> interviews = interviewRepo.findByUserEmailOrderByCreatedAtDesc(userEmail);
        if (!interviews.isEmpty()) {
            interviewScore = interviews.stream()
                    .filter(i -> i.getOverallScore() > 0)
                    .mapToDouble(MockInterviewSession::getOverallScore)
                    .average().orElse(0);
        }

        // ─── Overall Score (weighted average) ────────────────────────────
        // Weights: Resume 20%, Coding 25%, Aptitude (tech) 15%, HR 15%, Interview 25%
        double overall = (resumeScore * 0.20) + (codingScore * 0.25) + (techScore * 0.15) + (hrScore * 0.15) + (interviewScore * 0.25);
        overall = Math.round(overall * 10.0) / 10.0;

        // ─── Identify weak and strong areas ──────────────────────────────
        Map<String, Double> scoreMap = new LinkedHashMap<>();
        scoreMap.put("Resume", resumeScore);
        scoreMap.put("Coding", codingScore);
        scoreMap.put("Technical", techScore);
        scoreMap.put("HR", hrScore);
        scoreMap.put("Interview", interviewScore);

        List<String> weakAreas = new ArrayList<>();
        List<String> strongAreas = new ArrayList<>();
        for (Map.Entry<String, Double> entry : scoreMap.entrySet()) {
            if (entry.getValue() < 50) weakAreas.add(entry.getKey());
            else if (entry.getValue() >= 70) strongAreas.add(entry.getKey());
        }

        String verdict;
        if (overall >= 80) verdict = "Interview Ready (High Placement Probability)";
        else if (overall >= 60) verdict = "Competitive — Keep Practicing";
        else if (overall >= 40) verdict = "Developing — Focus on Weak Areas";
        else verdict = "Foundational Work Required";

        return ReadinessScoreResponse.builder()
                .resumeScore(Math.round(resumeScore * 10.0) / 10.0)
                .codingScore(Math.round(codingScore * 10.0) / 10.0)
                .aptitudeScore(Math.round(techScore * 10.0) / 10.0) // aptitude treated as tech for now
                .hrScore(Math.round(hrScore * 10.0) / 10.0)
                .technicalScore(Math.round(techScore * 10.0) / 10.0)
                .interviewScore(Math.round(interviewScore * 10.0) / 10.0)
                .overallScore(overall)
                .weakAreas(weakAreas)
                .strongAreas(strongAreas)
                .totalCodingSubmissions(totalCodingSubs)
                .solvedProblems(solvedProblems)
                .readinessVerdict(verdict)
                .build();
    }
}

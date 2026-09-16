package com.majorproject.backend.roadmap;

import com.majorproject.backend.dashboard.DashboardService;
import com.majorproject.backend.dashboard.ReadinessScoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final DashboardService dashboardService;

    public RoadmapResponse generateRoadmap(String userEmail) {
        ReadinessScoreResponse readiness = dashboardService.getReadinessScore(userEmail);

        List<String> weakAreas = readiness.getWeakAreas();
        if (weakAreas == null || weakAreas.isEmpty()) {
            weakAreas = List.of("General Practice");
        }

        Map<String, Double> scores = new LinkedHashMap<>();
        scores.put("Resume", readiness.getResumeScore());
        scores.put("Coding", readiness.getCodingScore());
        scores.put("Technical", readiness.getTechnicalScore());
        scores.put("HR", readiness.getHrScore());
        scores.put("Interview", readiness.getInterviewScore());

        // Sort by score ascending (weakest first)
        List<Map.Entry<String, Double>> sortedAreas = new ArrayList<>(scores.entrySet());
        sortedAreas.sort(Map.Entry.comparingByValue());

        List<RoadmapResponse.WeekPlan> weeks = new ArrayList<>();

        // Week 1: Focus on weakest area
        if (sortedAreas.size() > 0) {
            weeks.add(createWeekPlan("Week 1", sortedAreas.get(0).getKey(), sortedAreas.get(0).getValue()));
        }

        // Week 2: Second weakest
        if (sortedAreas.size() > 1) {
            weeks.add(createWeekPlan("Week 2", sortedAreas.get(1).getKey(), sortedAreas.get(1).getValue()));
        }

        // Week 3: Third weakest
        if (sortedAreas.size() > 2) {
            weeks.add(createWeekPlan("Week 3", sortedAreas.get(2).getKey(), sortedAreas.get(2).getValue()));
        }

        // Week 4: Company prep sprint
        weeks.add(RoadmapResponse.WeekPlan.builder()
                .week("Week 4")
                .focus("Company Sprint & Review")
                .rationale("Final week: consolidate all areas and do company-specific preparation")
                .tasks(List.of(
                        "Resume ATS re-check and final polish",
                        "TCS + Infosys pattern papers practice",
                        "Full mock interview (HR + Technical)",
                        "Review all weak-area notes and formulas",
                        "Timed coding contest simulation"
                ))
                .build());

        return RoadmapResponse.builder()
                .studentName(userEmail.split("@")[0])
                .currentReadiness(readiness.getOverallScore())
                .weeks(weeks)
                .build();
    }

    private RoadmapResponse.WeekPlan createWeekPlan(String week, String area, double score) {
        String focus = area + " Improvement";
        String rationale = String.format("%s is at %.0f%% — needs focused attention", area, score);
        List<String> tasks = generateTasksForArea(area, score);

        return RoadmapResponse.WeekPlan.builder()
                .week(week)
                .focus(focus)
                .rationale(rationale)
                .tasks(tasks)
                .build();
    }

    private List<String> generateTasksForArea(String area, double score) {
        return switch (area) {
            case "Resume" -> List.of(
                    "Update resume with quantified impact bullets (Google XYZ formula)",
                    "Add 2 backend-focused projects with tech stack details",
                    "Run ATS compatibility check and fix missing keywords",
                    "Include GitHub/LinkedIn profile links",
                    "Get resume reviewed by 1 peer"
            );
            case "Coding" -> List.of(
                    score < 30 ? "Solve 10 Easy-level array and string problems" : "Solve 15 Medium-level DP and graph problems",
                    "Complete 1 timed coding contest (45 min, 3 problems)",
                    "Review time/space complexity for all submitted solutions",
                    "Study 2 new DSA patterns (e.g., sliding window, two pointers)",
                    "Get AI Mentor feedback on your last 3 submissions"
            );
            case "Technical" -> List.of(
                    "Complete OOP concepts revision (inheritance, polymorphism, abstraction)",
                    "Practice 20 DBMS and SQL query questions",
                    "Study OS concepts: process scheduling, deadlocks, paging",
                    "Complete Computer Networks basics: OSI, TCP/IP, HTTP",
                    "Take a technical mock test and review mistakes"
            );
            case "HR" -> List.of(
                    "Prepare STAR-format answers for top 10 HR questions",
                    "Practice 'Tell me about yourself' — record and review",
                    "Prepare strengths/weaknesses answers with real examples",
                    "Practice 3 behavioral questions with AI evaluator",
                    "Study company-specific HR interview patterns"
            );
            case "Interview" -> List.of(
                    "Complete 2 full mock interviews (HR + Technical rounds)",
                    "Practice voice-based interview for fluency",
                    "Time yourself: aim for 2-3 minutes per HR answer",
                    "Record one mock interview and self-evaluate",
                    "Focus on confidence: reduce filler words (um, uh, like)"
            );
            default -> List.of(
                    "Review placement preparation fundamentals",
                    "Take a practice test in each module",
                    "Identify your top 3 improvement areas",
                    "Set daily practice targets (1 hour minimum)",
                    "Track progress on the dashboard"
            );
        };
    }
}

package com.majorproject.backend.hrtraining;

import com.majorproject.backend.hrtraining.dto.HrEvaluationResponse;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HrEvaluationEngine {

    private static final List<String> ACTION_VERBS = List.of(
            "engineered", "architected", "developed", "implemented", "designed",
            "orchestrated", "refactored", "automated", "spearheaded", "negotiated",
            "resolved", "facilitated", "profiled", "investigated", "streamlined",
            "coordinated", "executed", "benchmarked", "diagnosed", "optimized",
            "built", "collaborated", "aligned", "initiated", "analyzed"
    );

    private static final List<String> SITUATION_MARKERS = List.of(
            "when i was", "during my", "at the time", "in our semester", "in my role",
            "in our project", "we faced", "the challenge was", "our team was", "there was an issue",
            "problem occurred", "faced with", "deadline", "client needed", "system was"
    );

    private static final List<String> TASK_MARKERS = List.of(
            "my responsibility was", "i was tasked with", "my role was", "my goal was",
            "i needed to", "i had to", "was assigned to me", "my objective was",
            "i took ownership", "target was", "deliverable was"
    );

    private static final List<String> RESULT_MARKERS = List.of(
            "resulted in", "improved by", "reduced by", "increased by", "saved",
            "boosted", "achieved", "successfully delivered", "secured", "prevented",
            "the outcome was", "feedback was", "learned that", "enabled us to", "won"
    );

    private static final Pattern METRIC_PATTERN = Pattern.compile("\\b(\\d+(\\.\\d+)?%|\\d+\\s*(seconds|sec|ms|days|weeks|hours|users|req|requests|teams|stars|first|second|3rd|2nd|1st|top))\\b", Pattern.CASE_INSENSITIVE);

    public HrEvaluationResponse evaluate(String promptId, HrPrompt prompt, String userResponse) {
        String clean = userResponse == null ? "" : userResponse.trim();
        String lower = clean.toLowerCase();

        String[] words = clean.split("\\s+");
        int wordCount = clean.isEmpty() ? 0 : words.length;

        // 1. Detect Action Verbs
        List<String> matchedVerbs = new ArrayList<>();
        for (String verb : ACTION_VERBS) {
            if (Pattern.compile("\\b" + verb + "\\b", Pattern.CASE_INSENSITIVE).matcher(clean).find()) {
                matchedVerbs.add(verb);
            }
        }

        // 2. Detect Metrics
        List<String> matchedMetrics = new ArrayList<>();
        Matcher metricMatcher = METRIC_PATTERN.matcher(clean);
        while (metricMatcher.find() && matchedMetrics.size() < 6) {
            matchedMetrics.add(metricMatcher.group(0));
        }

        // 3. Evaluate STAR Components
        // Situation
        boolean situationDetected = SITUATION_MARKERS.stream().anyMatch(lower::contains);
        int situationScore = calculateSituationScore(wordCount, situationDetected, lower);

        // Task
        boolean taskDetected = TASK_MARKERS.stream().anyMatch(lower::contains) || (lower.contains(" i ") && lower.contains("role"));
        int taskScore = calculateTaskScore(wordCount, taskDetected, lower);

        // Action
        boolean actionDetected = !matchedVerbs.isEmpty();
        int actionScore = calculateActionScore(wordCount, matchedVerbs.size(), lower);

        // Result
        boolean resultDetected = RESULT_MARKERS.stream().anyMatch(lower::contains) || !matchedMetrics.isEmpty();
        int resultScore = calculateResultScore(wordCount, resultDetected, matchedMetrics.size(), lower);

        // Clarity Score
        int clarityScore = calculateClarityScore(wordCount, lower);

        // Overall Score (Weighted STAR + Clarity)
        int overallScore = (int) Math.round(
                (situationScore * 0.20) +
                (taskScore * 0.20) +
                (actionScore * 0.35) +
                (resultScore * 0.25)
        );

        // Adjust for brevity
        if (wordCount < 20) {
            overallScore = Math.min(overallScore, 40);
        } else if (wordCount < 38) {
            overallScore = Math.min(overallScore, 65);
        }

        overallScore = Math.max(15, Math.min(98, overallScore));

        // Generate Strengths & Improvements
        List<String> strengths = new ArrayList<>();
        List<String> improvements = new ArrayList<>();

        if (actionScore >= 75) {
            strengths.add("Strong agency and ownership: Used high-impact engineering verbs (" + String.join(", ", matchedVerbs.subList(0, Math.min(3, matchedVerbs.size()))) + ").");
        }
        if (!matchedMetrics.isEmpty()) {
            strengths.add("Quantified business/technical outcome: Highlighted measurable achievements (" + String.join(", ", matchedMetrics) + ").");
        }
        if (situationScore >= 70 && taskScore >= 70) {
            strengths.add("Clean context calibration: Effectively framed the problem stakes before jumping into execution.");
        }
        if (clarityScore >= 80) {
            strengths.add("Professional pacing: Concise delivery without excessive filler phrases or digressions.");
        }

        if (strengths.isEmpty()) {
            strengths.add("Good baseline prompt comprehension and willingness to tackle behavioral challenges.");
        }

        // Targeted Improvements
        if (!situationDetected || situationScore < 60) {
            improvements.add("Explicitly frame the 'Situation': Open with who was involved, the project timeline, and what was at stake.");
        }
        if (!taskDetected || taskScore < 60) {
            improvements.add("Differentiate 'Task' from team effort: Clearly specify what was YOUR individual ownership rather than saying 'we'.");
        }
        if (matchedVerbs.isEmpty() || actionScore < 65) {
            improvements.add("Elevate 'Action' verbs: Swap passive phrases ('was done', 'helped with') for active leadership verbs ('engineered', 'orchestrated', 'profiled').");
        }
        if (matchedMetrics.isEmpty() || resultScore < 65) {
            improvements.add("Back up 'Result' with numbers: Mention percentage improvements, latency drops, bug reduction, or grade outcomes.");
        }
        if (wordCount < 60) {
            improvements.add("Expand depth: Recruiters look for 90-180 words in behavioral responses to assess thoroughness.");
        }

        String verdict;
        String executiveSummary;
        if (overallScore >= 85) {
            verdict = "Excellent Response";
            executiveSummary = "Strong, well-structured answer. You clearly balanced context, personal ownership, concrete action, and measurable outcomes.";
        } else if (overallScore >= 70) {
            verdict = "Good Response";
            executiveSummary = "Solid response with clear problem resolution. Adding more specific details and measurable outcomes will make it even stronger.";
        } else if (overallScore >= 50) {
            verdict = "Developing Response";
            executiveSummary = "Good start, but lacks distinction between team effort and your individual actions. Highlight specific steps you took and the end results.";
        } else {
            verdict = "Needs More Detail";
            executiveSummary = "The response is quite brief. For interview success, describe the problem, your personal role, the actions you took, and what you achieved.";
        }

        // Build STAR breakdown
        HrEvaluationResponse.StarBreakdown breakdown = HrEvaluationResponse.StarBreakdown.builder()
                .situation(HrEvaluationResponse.ComponentScore.builder()
                        .score(situationScore)
                        .detected(situationDetected)
                        .status(situationScore >= 70 ? "Strong" : (situationScore >= 50 ? "Moderate" : "Needs Detail"))
                        .feedback(situationScore >= 70 ? "Context and stakes are well established." : "Add a clear opening sentence detailing the challenge context.")
                        .build())
                .task(HrEvaluationResponse.ComponentScore.builder()
                        .score(taskScore)
                        .detected(taskDetected)
                        .status(taskScore >= 70 ? "Clear Ownership" : (taskScore >= 50 ? "Moderate" : "Vague Role"))
                        .feedback(taskScore >= 70 ? "Your specific mandate is clearly distinguished." : "Highlight your personal responsibility ('My task was...') vs the team.")
                        .build())
                .action(HrEvaluationResponse.ComponentScore.builder()
                        .score(actionScore)
                        .detected(actionDetected)
                        .status(actionScore >= 70 ? "High Agency" : (actionScore >= 50 ? "Moderate" : "Passive"))
                        .feedback(actionScore >= 70 ? "Active problem-solving steps and leadership verbs detected." : "Use stronger technical verbs ('designed', 'refactored', 'benchmarked').")
                        .build())
                .result(HrEvaluationResponse.ComponentScore.builder()
                        .score(resultScore)
                        .detected(resultDetected)
                        .status(resultScore >= 70 ? "High Impact" : (resultScore >= 50 ? "Acceptable" : "Unquantified"))
                        .feedback(resultScore >= 70 ? "Concluded with tangible metrics or key learnings." : "Quantify the outcome (e.g., % improvement, saved hours, team awards).")
                        .build())
                .build();

        String modelAnswer = prompt != null && prompt.getSampleModelAnswer() != null
                ? prompt.getSampleModelAnswer()
                : "During my capstone project, our backend experienced high query latency under peak load (Situation). My objective was to optimize the database query execution and connection pooling (Task). I profiled the slow queries using EXPLAIN ANALYZE, added compound B-tree indexes, and tuned HikariCP pool parameters (Action). This reduced response latency by 45% and enabled the service to sustain 1,200 concurrent requests during demonstration (Result).";

        return HrEvaluationResponse.builder()
                .id(UUID.randomUUID().toString())
                .overallScore(overallScore)
                .situationScore(situationScore)
                .taskScore(taskScore)
                .actionScore(actionScore)
                .resultScore(resultScore)
                .clarityScore(clarityScore)
                .verdict(verdict)
                .executiveSummary(executiveSummary)
                .starBreakdown(breakdown)
                .strengths(strengths)
                .areasForImprovement(improvements)
                .barRaiserModelAnswer(modelAnswer)
                .wordCount(wordCount)
                .metricsDetected(matchedMetrics)
                .actionVerbsDetected(matchedVerbs)
                .build();
    }

    private int calculateSituationScore(int wordCount, boolean detected, String lower) {
        int score = 40;
        if (detected) score += 35;
        if (lower.contains("project") || lower.contains("team") || lower.contains("company")) score += 10;
        if (wordCount >= 60) score += 10;
        return Math.min(95, score);
    }

    private int calculateTaskScore(int wordCount, boolean detected, String lower) {
        int score = 40;
        if (detected) score += 35;
        if (lower.contains("my role") || lower.contains("i was responsible") || lower.contains("my task")) score += 15;
        if (wordCount >= 50) score += 5;
        return Math.min(95, score);
    }

    private int calculateActionScore(int wordCount, int verbCount, String lower) {
        int score = 45;
        score += Math.min(35, verbCount * 12);
        if (lower.contains("because") || lower.contains("so that") || lower.contains("in order to")) score += 10;
        if (wordCount >= 70) score += 8;
        return Math.min(96, score);
    }

    private int calculateResultScore(int wordCount, boolean detected, int metricCount, String lower) {
        int score = 35;
        if (detected) score += 25;
        score += Math.min(30, metricCount * 15);
        if (lower.contains("learned") || lower.contains("improved") || lower.contains("succeeded")) score += 10;
        return Math.min(95, score);
    }

    private int calculateClarityScore(int wordCount, String lower) {
        int score = 75;
        if (wordCount >= 70 && wordCount <= 250) score += 15;
        if (lower.contains("um ") || lower.contains("like,") || lower.contains("basically")) score -= 10;
        return Math.min(95, Math.max(50, score));
    }
}

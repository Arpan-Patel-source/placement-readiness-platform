package com.majorproject.backend.mockinterview;

import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Evaluates interview answers using keyword matching, STAR detection,
 * and technical accuracy scoring.
 */
@Component
public class InterviewEvaluationEngine {

    // ─── STAR detection keywords ─────────────────────────────────────────
    private static final List<String> SITUATION_KEYWORDS = List.of("when", "during", "while working", "at my", "in my", "last year", "once", "there was");
    private static final List<String> TASK_KEYWORDS = List.of("responsible", "tasked", "needed to", "had to", "my role", "goal was", "objective");
    private static final List<String> ACTION_KEYWORDS = List.of("i implemented", "i designed", "i created", "i built", "i led", "i organized", "i developed", "i wrote", "i used");
    private static final List<String> RESULT_KEYWORDS = List.of("resulted in", "improved", "increased", "reduced", "achieved", "successfully", "outcome", "impact", "saved");

    public Map<String, Object> evaluateHrAnswer(String question, String answer) {
        Map<String, Object> result = new LinkedHashMap<>();
        String ansLower = answer.toLowerCase();

        int situationScore = detectKeywords(ansLower, SITUATION_KEYWORDS) > 0 ? 25 : 5;
        int taskScore = detectKeywords(ansLower, TASK_KEYWORDS) > 0 ? 25 : 5;
        int actionScore = detectKeywords(ansLower, ACTION_KEYWORDS) > 0 ? 25 : 5;
        int resultScore = detectKeywords(ansLower, RESULT_KEYWORDS) > 0 ? 25 : 5;

        int wordCount = answer.split("\\s+").length;
        int clarityBonus = (wordCount >= 50 && wordCount <= 300) ? 10 : 0;

        int total = Math.min(100, situationScore + taskScore + actionScore + resultScore + clarityBonus);

        result.put("score", total);
        result.put("situationDetected", situationScore > 5);
        result.put("taskDetected", taskScore > 5);
        result.put("actionDetected", actionScore > 5);
        result.put("resultDetected", resultScore > 5);
        result.put("wordCount", wordCount);

        List<String> feedback = new ArrayList<>();
        if (situationScore <= 5) feedback.add("Set the scene: describe the context or situation clearly.");
        if (taskScore <= 5) feedback.add("Define your specific role or responsibility in the situation.");
        if (actionScore <= 5) feedback.add("Describe the concrete actions YOU took (use 'I' statements).");
        if (resultScore <= 5) feedback.add("Quantify the outcome: what was the measurable result?");
        if (wordCount < 50) feedback.add("Your answer is too brief. Aim for 80-200 words.");
        if (wordCount > 300) feedback.add("Your answer is quite long. Try to be more concise.");
        if (feedback.isEmpty()) feedback.add("Strong STAR-format answer with good structure.");

        result.put("feedback", feedback);
        result.put("verdict", total >= 80 ? "Strong" : total >= 50 ? "Adequate" : "Needs Improvement");

        return result;
    }

    public Map<String, Object> evaluateTechnicalAnswer(String question, String answer) {
        Map<String, Object> result = new LinkedHashMap<>();
        String ansLower = answer.toLowerCase();
        String qLower = question.toLowerCase();

        int score = 30; // base for attempting
        List<String> feedback = new ArrayList<>();

        int wordCount = answer.split("\\s+").length;
        if (wordCount < 10) {
            score = 15;
            feedback.add("Answer is too short. Provide a detailed technical explanation.");
        } else if (wordCount >= 30) {
            score += 15;
        }

        // Check for technical depth indicators
        if (ansLower.contains("example") || ansLower.contains("for instance") || ansLower.contains("e.g.")) {
            score += 15;
        } else {
            feedback.add("Include concrete examples to strengthen your answer.");
        }

        if (ansLower.contains("because") || ansLower.contains("reason") || ansLower.contains("advantage") || ansLower.contains("disadvantage")) {
            score += 10;
        }

        if (ansLower.contains("complexity") || ansLower.contains("performance") || ansLower.contains("trade-off") || ansLower.contains("tradeoff")) {
            score += 10;
        }

        // Check if answer relates to the question topic
        String[] questionWords = qLower.split("\\s+");
        int relevantWordCount = 0;
        for (String w : questionWords) {
            if (w.length() > 3 && ansLower.contains(w)) relevantWordCount++;
        }
        if (relevantWordCount >= 3) {
            score += 10;
        } else {
            feedback.add("Your answer may not directly address the question. Stay on topic.");
        }

        score = Math.min(100, score);
        if (feedback.isEmpty()) feedback.add("Good technical depth with relevant examples.");

        result.put("score", score);
        result.put("wordCount", wordCount);
        result.put("feedback", feedback);
        result.put("verdict", score >= 75 ? "Strong" : score >= 50 ? "Adequate" : "Needs Improvement");

        return result;
    }

    public Map<String, Object> evaluateCodingAnswer(String question, String answer) {
        Map<String, Object> result = new LinkedHashMap<>();
        String ansLower = answer.toLowerCase();

        int score = 20;
        List<String> feedback = new ArrayList<>();

        // Check for code structure
        if (ansLower.contains("def ") || ansLower.contains("function") || ansLower.contains("public") || ansLower.contains("class ") || ansLower.contains("int main")) {
            score += 25;
        } else {
            feedback.add("Include actual code in your solution.");
        }

        // Check for complexity discussion
        if (ansLower.contains("o(n)") || ansLower.contains("o(1)") || ansLower.contains("time complexity") || ansLower.contains("space complexity") || ansLower.contains("o(n²)") || ansLower.contains("o(log")) {
            score += 20;
        } else {
            feedback.add("Discuss time and space complexity of your approach.");
        }

        // Check for approach explanation
        if (ansLower.contains("approach") || ansLower.contains("algorithm") || ansLower.contains("idea") || ansLower.contains("strategy")) {
            score += 15;
        } else {
            feedback.add("Explain your approach before diving into code.");
        }

        // Edge cases
        if (ansLower.contains("edge case") || ansLower.contains("empty") || ansLower.contains("null") || ansLower.contains("base case")) {
            score += 10;
        } else {
            feedback.add("Consider and handle edge cases.");
        }

        score = Math.min(100, score);
        if (feedback.isEmpty()) feedback.add("Well-structured coding answer with complexity analysis.");

        result.put("score", score);
        result.put("feedback", feedback);
        result.put("verdict", score >= 75 ? "Strong" : score >= 50 ? "Adequate" : "Needs Improvement");

        return result;
    }

    private int detectKeywords(String text, List<String> keywords) {
        int count = 0;
        for (String kw : keywords) {
            if (text.contains(kw)) count++;
        }
        return count;
    }
}

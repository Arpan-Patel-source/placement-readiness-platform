package com.majorproject.backend.codingmentor;

import com.majorproject.backend.coding.CodingProblem;
import com.majorproject.backend.coding.CodingProblemBank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AI Coding Mentor that analyzes submitted solutions using pattern-matching rules.
 * Detects common approaches (brute force, two-pointer, sliding window, etc.)
 * and suggests optimal alternatives based on the problem category.
 */
@Service
@RequiredArgsConstructor
public class CodingMentorService {

    private final CodingProblemBank problemBank;

    public MentorAnalysisResponse analyze(MentorAnalysisRequest request) {
        Optional<CodingProblem> optProblem = problemBank.getProblemById(request.getProblemId());

        String problemTitle = optProblem.map(CodingProblem::getTitle).orElse("Unknown Problem");
        String optimalTime = optProblem.map(CodingProblem::getTimeComplexity).orElse("Unknown");
        String optimalSpace = optProblem.map(CodingProblem::getSpaceComplexity).orElse("Unknown");
        String optimalApproach = optProblem.map(CodingProblem::getOptimalApproach).orElse("Unknown");

        String code = request.getCode() != null ? request.getCode() : "";
        String codeLower = code.toLowerCase();

        // Detect approach from code patterns
        String detectedApproach = detectApproach(codeLower);
        String detectedTime = estimateTimeComplexity(codeLower);
        String detectedSpace = estimateSpaceComplexity(codeLower);

        boolean isOptimal = detectedTime.equals(optimalTime) ||
                detectedApproach.toLowerCase().contains(optimalApproach.toLowerCase());

        // Generate suggestions
        List<String> codeQualitySuggestions = analyzeCodeQuality(code, request.getLanguage());
        List<String> optimizationTips = generateOptimizationTips(detectedApproach, optimalApproach, optProblem.orElse(null));

        String betterApproach = isOptimal ? "Your approach appears optimal!" : optimalApproach;
        String betterExplanation = isOptimal
                ? "Great job! Your solution uses an efficient approach."
                : generateApproachExplanation(optimalApproach, optProblem.orElse(null));

        int qualityScore = calculateQualityScore(code, isOptimal, codeQualitySuggestions.size());

        String verdict;
        if (qualityScore >= 85) verdict = "Excellent — Clean, efficient solution";
        else if (qualityScore >= 70) verdict = "Good — Minor improvements possible";
        else if (qualityScore >= 50) verdict = "Average — Consider optimization and code quality";
        else verdict = "Needs Improvement — Review approach and coding style";

        return MentorAnalysisResponse.builder()
                .problemId(request.getProblemId())
                .problemTitle(problemTitle)
                .detectedApproach(detectedApproach)
                .timeComplexity(detectedTime)
                .spaceComplexity(detectedSpace)
                .optimalTimeComplexity(optimalTime)
                .optimalSpaceComplexity(optimalSpace)
                .isOptimal(isOptimal)
                .betterApproach(betterApproach)
                .betterApproachExplanation(betterExplanation)
                .codeQualitySuggestions(codeQualitySuggestions)
                .optimizationTips(optimizationTips)
                .overallVerdict(verdict)
                .codeQualityScore(qualityScore)
                .build();
    }

    private String detectApproach(String code) {
        if (code.contains("left") && code.contains("right") && (code.contains("while") || code.contains("for")))
            return "Two Pointer";
        if (code.contains("hashmap") || code.contains("hashset") || code.contains("dictionary") || code.contains("dict(") || code.contains("new HashMap") || code.contains("unordered_map"))
            return "Hash Map / Hash Set";
        if (code.contains("queue") || code.contains("bfs") || code.contains("deque"))
            return "BFS (Breadth-First Search)";
        if ((code.contains("dfs") || (code.contains("void") && code.contains("visited"))) && code.contains("recursive"))
            return "DFS (Depth-First Search)";
        if (code.contains("dp[") || code.contains("memo") || code.contains("tabulation"))
            return "Dynamic Programming";
        if (code.contains("sort") && (code.contains("greedy") || !code.contains("dp[")))
            return "Sorting + Greedy";
        if (code.contains("binary") && code.contains("search") || code.contains("lo") && code.contains("hi") && code.contains("mid"))
            return "Binary Search";
        if (code.contains("stack") || code.contains("push") && code.contains("pop"))
            return "Stack-Based";
        if (code.contains("window") || (code.contains("start") && code.contains("end") && code.contains("sum")))
            return "Sliding Window";
        if (code.contains("recursion") || code.contains("recursive") || countOccurrences(code, "return") > 2)
            return "Recursion";

        // Check for nested loops (brute force indicator)
        int forCount = countOccurrences(code, "for");
        int whileCount = countOccurrences(code, "while");
        if (forCount >= 2 || whileCount >= 2 || (forCount + whileCount) >= 2)
            return "Brute Force (Nested Loops)";
        if (forCount == 1 || whileCount == 1)
            return "Linear Scan";

        return "Custom / Unrecognized Approach";
    }

    private String estimateTimeComplexity(String code) {
        int loops = countOccurrences(code, "for") + countOccurrences(code, "while");
        boolean hasBinaryPattern = code.contains("mid") && (code.contains("/2") || code.contains(">> 1") || code.contains("//2"));
        boolean hasSort = code.contains(".sort") || code.contains("arrays.sort") || code.contains("collections.sort");

        if (loops == 0) return "O(1)";
        if (hasBinaryPattern) return "O(log n)";
        if (hasSort && loops <= 2) return "O(n log n)";
        if (loops == 1) return "O(n)";
        if (loops == 2) return "O(n²)";
        if (loops >= 3) return "O(n³)";
        return "O(n)";
    }

    private String estimateSpaceComplexity(String code) {
        boolean hasArray = code.contains("new int[") || code.contains("new string[") || code.contains("vector<") || code.contains("[]");
        boolean hasMap = code.contains("hashmap") || code.contains("hashset") || code.contains("dict(") || code.contains("unordered_map") || code.contains("set(");
        boolean has2DArray = code.contains("[][]") || countOccurrences(code, "new int[") >= 2;
        boolean hasDpTable = code.contains("dp[") && (code.contains("][") || countOccurrences(code, "dp[") >= 3);

        if (hasDpTable) return "O(n × m)";
        if (has2DArray) return "O(n²)";
        if (hasMap || hasArray) return "O(n)";
        return "O(1)";
    }

    private List<String> analyzeCodeQuality(String code, String language) {
        List<String> suggestions = new ArrayList<>();

        if (code.length() < 20) {
            suggestions.add("Code appears too short — make sure your solution handles all cases.");
            return suggestions;
        }

        // Variable naming
        if (code.contains("int a ") || code.contains("int b ") || code.contains("int x ") || code.contains("int y ")) {
            suggestions.add("Use descriptive variable names instead of single letters (a, b, x, y) for better readability.");
        }

        // Missing edge case handling
        if (!code.contains("if") && !code.contains("null") && !code.contains("empty") && !code.contains("len") && !code.contains(".length")) {
            suggestions.add("Consider adding edge case handling (empty input, single element, null checks).");
        }

        // Magic numbers
        if (code.matches(".*\\b\\d{3,}\\b.*") && !code.contains("10^") && !code.contains("1000") && !code.contains("Integer.MAX")) {
            suggestions.add("Avoid magic numbers — use named constants for clarity.");
        }

        // Comments
        if (!code.contains("//") && !code.contains("#") && !code.contains("/*") && code.length() > 200) {
            suggestions.add("Add comments to explain your approach and key logic sections.");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("Good code quality! Clear structure and readable logic.");
        }

        return suggestions;
    }

    private List<String> generateOptimizationTips(String detected, String optimal, CodingProblem problem) {
        List<String> tips = new ArrayList<>();

        if (detected.contains("Brute Force")) {
            tips.add("Your brute force approach works but may TLE on large inputs. Consider a more efficient algorithm.");
            if (optimal != null) {
                tips.add("The optimal approach for this problem is: " + optimal);
            }
        }

        if (detected.contains("Linear") && optimal != null && optimal.contains("Binary")) {
            tips.add("Consider using Binary Search for O(log n) instead of O(n) linear scan — the input is sorted.");
        }

        if (problem != null && problem.getHints() != null) {
            tips.add("Hint: " + problem.getHints().get(0));
        }

        if (tips.isEmpty()) {
            tips.add("Your approach looks efficient. Focus on edge cases and code clarity.");
        }

        return tips;
    }

    private String generateApproachExplanation(String approach, CodingProblem problem) {
        return switch (approach.toLowerCase()) {
            case "hash map — single pass" -> "Use a HashMap to store each number's complement. As you iterate, check if the current number exists as a key. This gives O(n) time instead of O(n²).";
            case "kadane's algorithm" -> "Maintain a running sum. At each element, decide: start a new subarray or extend the current one. Track the maximum sum seen so far.";
            case "two pointer" -> "Place one pointer at the start and one at the end. Move them toward each other based on the problem's comparison logic.";
            case "two pointer merge" -> "Use two pointers, one for each sorted input. Compare the pointed elements and advance the pointer with the smaller value.";
            case "expand around center" -> "For each character (and between each pair), expand outward while characters match. Track the longest palindrome found.";
            case "dfs flood fill" -> "Start DFS/BFS from each unvisited land cell. Mark all connected land cells as visited. Each DFS start = one island.";
            case "bfs level order" -> "Use a queue for BFS. Process nodes level by level, tracking distance from the source.";
            case "dp + binary search (patience sort)" -> "Maintain a tails array. For each element, use binary search to find its position. This gives O(n log n) complexity.";
            case "bottom-up dp (fibonacci)" -> "Recognize that ways(n) = ways(n-1) + ways(n-2). Use two variables to build up from base cases.";
            case "2d dp table" -> "Create a 2D table where dp[i][j] represents the subproblem solution. Fill it bottom-up using the recurrence relation.";
            case "sort by finish time + greedy" -> "Sort activities by finish time. Greedily select the next activity that starts after the current one finishes.";
            case "sort by value/weight ratio" -> "Sort items by value/weight ratio in descending order. Take as much as possible of each item, starting from the highest ratio.";
            default -> "Consider the optimal approach: " + approach + ". It typically provides better time/space complexity for this problem type.";
        };
    }

    private int calculateQualityScore(String code, boolean isOptimal, int issueCount) {
        int score = 60; // base
        if (isOptimal) score += 25;
        if (code.length() > 50) score += 5;
        if (code.contains("//") || code.contains("#")) score += 5;
        score -= issueCount * 5;
        return Math.max(10, Math.min(100, score));
    }

    private int countOccurrences(String text, String word) {
        int count = 0, idx = 0;
        while ((idx = text.indexOf(word, idx)) != -1) { count++; idx += word.length(); }
        return count;
    }
}

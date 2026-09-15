package com.majorproject.backend.aptitude;

import com.majorproject.backend.aptitude.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AptitudeService {

    private final AptitudeQuestionBank questionBank;

    public List<AptitudeCategorySummaryDto> getCategorySummaries() {
        List<AptitudeQuestion> all = questionBank.getAllQuestions();
        List<AptitudeCategorySummaryDto> summaries = new ArrayList<>();

        for (AptitudeCategory category : AptitudeCategory.values()) {
            List<AptitudeQuestion> catQuestions = all.stream()
                    .filter(q -> q.getCategory() == category)
                    .toList();

            Map<String, List<AptitudeQuestion>> byTopic = catQuestions.stream()
                    .collect(Collectors.groupingBy(AptitudeQuestion::getTopic));

            List<AptitudeCategorySummaryDto.TopicSummary> topicSummaries = byTopic.entrySet().stream()
                    .map(e -> AptitudeCategorySummaryDto.TopicSummary.builder()
                            .topicId(e.getKey().toLowerCase().replace(" ", "-").replace("&", "and"))
                            .topicName(e.getKey())
                            .questionCount(e.getValue().size())
                            .keyConcept(getKeyConceptForTopic(e.getKey()))
                            .build())
                    .sorted(Comparator.comparing(AptitudeCategorySummaryDto.TopicSummary::getTopicName))
                    .toList();

            summaries.add(AptitudeCategorySummaryDto.builder()
                    .category(category)
                    .title(category.getDisplayName())
                    .description(category.getDescription())
                    .totalQuestions(catQuestions.size())
                    .topics(topicSummaries)
                    .build());
        }

        return summaries;
    }

    public List<AptitudeQuestionDto> getQuestions(AptitudeCategory category, String topic, AptitudeDifficulty difficulty, Integer limit) {
        List<AptitudeQuestion> stream = questionBank.getAllQuestions();

        if (category != null) {
            stream = stream.stream().filter(q -> q.getCategory() == category).toList();
        }

        if (topic != null && !topic.trim().isEmpty()) {
            String cleanTopic = topic.trim().toLowerCase();
            stream = stream.stream()
                    .filter(q -> q.getTopic().toLowerCase().contains(cleanTopic)
                            || q.getTopic().toLowerCase().replace(" ", "-").replace("&", "and").equals(cleanTopic))
                    .toList();
        }

        if (difficulty != null) {
            stream = stream.stream().filter(q -> q.getDifficulty() == difficulty).toList();
        }

        if (limit != null && limit > 0 && limit < stream.size()) {
            stream = stream.subList(0, limit);
        }

        return stream.stream().map(this::toDto).toList();
    }

    public List<AptitudeQuestionDto> generateMockTest(AptitudeCategory category, int count) {
        List<AptitudeQuestion> pool = new ArrayList<>(questionBank.getAllQuestions());
        if (category != null) {
            pool = pool.stream().filter(q -> q.getCategory() == category).collect(Collectors.toList());
        }

        Collections.shuffle(pool, new Random());
        int targetSize = Math.min(count <= 0 ? 10 : count, pool.size());
        return pool.subList(0, targetSize).stream().map(this::toDto).toList();
    }

    public AptitudeResultResponse evaluateTest(AptitudeSubmitRequest request) {
        List<AptitudeQuestion> all = questionBank.getAllQuestions();
        Map<String, AptitudeQuestion> questionMap = all.stream()
                .collect(Collectors.toMap(AptitudeQuestion::getId, q -> q));

        int correctCount = 0;
        int incorrectCount = 0;
        int unattemptedCount = 0;

        Map<String, int[]> topicScores = new HashMap<>(); // topic -> [correct, total]
        List<AptitudeResultResponse.QuestionReview> reviews = new ArrayList<>();

        if (request.getAnswers() != null) {
            for (AptitudeSubmitRequest.AnswerSubmission sub : request.getAnswers()) {
                AptitudeQuestion q = questionMap.get(sub.getQuestionId());
                if (q == null) continue;

                topicScores.putIfAbsent(q.getTopic(), new int[]{0, 0});
                topicScores.get(q.getTopic())[1]++;

                boolean attempted = sub.getSelectedOptionIndex() != null;
                boolean correct = attempted && sub.getSelectedOptionIndex() == q.getCorrectOptionIndex();

                if (!attempted) {
                    unattemptedCount++;
                } else if (correct) {
                    correctCount++;
                    topicScores.get(q.getTopic())[0]++;
                } else {
                    incorrectCount++;
                }

                reviews.add(AptitudeResultResponse.QuestionReview.builder()
                        .questionId(q.getId())
                        .topic(q.getTopic())
                        .question(q.getQuestion())
                        .options(q.getOptions())
                        .selectedOptionIndex(sub.getSelectedOptionIndex())
                        .correctOptionIndex(q.getCorrectOptionIndex())
                        .isCorrect(correct)
                        .isAttempted(attempted)
                        .explanation(q.getExplanation())
                        .formulaTip(q.getFormulaTip())
                        .build());
            }
        }

        int totalQuestions = reviews.size();
        double scorePercentage = totalQuestions > 0 ? ((double) correctCount / totalQuestions) * 100.0 : 0.0;
        scorePercentage = Math.round(scorePercentage * 10.0) / 10.0;

        List<AptitudeResultResponse.TopicBreakdown> breakdowns = topicScores.entrySet().stream()
                .map(e -> {
                    int corr = e.getValue()[0];
                    int tot = e.getValue()[1];
                    double acc = tot > 0 ? ((double) corr / tot) * 100.0 : 0.0;
                    return AptitudeResultResponse.TopicBreakdown.builder()
                            .topic(e.getKey())
                            .total(tot)
                            .correct(corr)
                            .accuracy(Math.round(acc * 10.0) / 10.0)
                            .build();
                })
                .sorted((a, b) -> Double.compare(b.getAccuracy(), a.getAccuracy()))
                .toList();

        String verdict;
        String feedback;
        if (scorePercentage >= 80.0) {
            verdict = "Interview Ready (High Placement Probability)";
            feedback = "Exceptional analytical speed and precision! You are operating well above the typical 70% cutoff required by tier-1 product & tech services companies.";
        } else if (scorePercentage >= 60.0) {
            verdict = "Competitive Competency";
            feedback = "Solid baseline understanding. Focus on time allocation and shortcut formulas for speed in high-calculation topics.";
        } else {
            verdict = "Foundational Practice Required";
            feedback = "Review the step-by-step mathematical derivations and formula cheatsheets before attempting the next timed mock test.";
        }

        return AptitudeResultResponse.builder()
                .testId(request.getTestId() != null ? request.getTestId() : UUID.randomUUID().toString())
                .totalQuestions(totalQuestions)
                .correctCount(correctCount)
                .incorrectCount(incorrectCount)
                .unattemptedCount(unattemptedCount)
                .scorePercentage(scorePercentage)
                .totalTimeSpentSeconds(request.getTotalTimeSpentSeconds())
                .performanceVerdict(verdict)
                .performanceFeedback(feedback)
                .topicBreakdowns(breakdowns)
                .questionReviews(reviews)
                .build();
    }

    public List<FormulaCardDto> getFormulaCheatsheet() {
        return questionBank.getFormulaCheatsheet();
    }

    private AptitudeQuestionDto toDto(AptitudeQuestion q) {
        return AptitudeQuestionDto.builder()
                .id(q.getId())
                .category(q.getCategory())
                .topic(q.getTopic())
                .difficulty(q.getDifficulty())
                .question(q.getQuestion())
                .options(q.getOptions())
                .correctOptionIndex(q.getCorrectOptionIndex())
                .explanation(q.getExplanation())
                .formulaTip(q.getFormulaTip())
                .companiesAsked(q.getCompaniesAsked())
                .build();
    }

    private String getKeyConceptForTopic(String topic) {
        return switch (topic.toLowerCase()) {
            case "percentages" -> "Base-shifting, net change rules, price-consumption relations.";
            case "profit and loss" -> "Cost vs Selling price, successive discounts, faulty balances.";
            case "time and work" -> "Efficiency units, work equivalence, alternate-day workflows.";
            case "probability" -> "Sample space enumeration, combinations, conditional likelihood.";
            case "permutation & combination" -> "Bundling techniques, circular permutations, selection rules.";
            case "speed, distance & time" -> "Relative speeds, train length crossing, unit conversions.";
            case "blood relations" -> "Reverse generation tracing, coded tree analysis.";
            case "coding-decoding" -> "Positional shifts, reverse mirroring, EJOTY alphabet mapping.";
            case "seating arrangement" -> "Circular clockwise/counter-clockwise orientation, linear bounds.";
            case "puzzles & deduction" -> "Syllogism Venn boundaries, multi-variable matrix mapping.";
            case "vocabulary" -> "Etymology, root words, context clues for synonyms & antonyms.";
            case "error detection" -> "Subject-verb agreement, proximity rule, modifier placement.";
            case "sentence correction" -> "Correlative conjunctions, parallel construction, tense consistency.";
            case "reading comprehension" -> "Authorial intent inference, tone analysis, context extraction.";
            default -> "Core foundational principles and shortcuts.";
        };
    }
}

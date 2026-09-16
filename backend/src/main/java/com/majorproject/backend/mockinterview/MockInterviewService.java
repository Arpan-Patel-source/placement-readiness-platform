package com.majorproject.backend.mockinterview;

import com.majorproject.backend.mockinterview.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MockInterviewService {

    private final InterviewQuestionGenerator questionGenerator;
    private final InterviewEvaluationEngine evaluationEngine;
    private final MockInterviewRepository repository;

    public StartInterviewResponse startInterview(String userEmail, StartInterviewRequest request) {
        int count = request.getQuestionCount() > 0 ? request.getQuestionCount() : 5;
        InterviewRoundType roundType = request.getRoundType() != null ? request.getRoundType() : InterviewRoundType.HR;

        List<Map<String, String>> questions = switch (roundType) {
            case HR -> questionGenerator.generateHrQuestions(count);
            case TECHNICAL -> questionGenerator.generateTechnicalQuestions(request.getSkills(), count);
            case CODING -> questionGenerator.generateCodingQuestions(Math.min(count, 3));
        };

        // Create session
        MockInterviewSession session = MockInterviewSession.builder()
                .userEmail(userEmail)
                .roundType(roundType)
                .questionsJson(toJson(questions))
                .overallScore(0)
                .build();

        MockInterviewSession saved = repository.save(session);

        return StartInterviewResponse.builder()
                .sessionId(saved.getId().toString())
                .roundType(roundType.getDisplayName())
                .questions(questions)
                .totalQuestions(questions.size())
                .build();
    }

    public InterviewResultResponse submitAnswers(String userEmail, SubmitInterviewRequest request) {
        Optional<MockInterviewSession> optSession = repository.findById(UUID.fromString(request.getSessionId()));

        if (optSession.isEmpty()) {
            return InterviewResultResponse.builder()
                    .sessionId(request.getSessionId())
                    .verdict("Error: Session not found")
                    .build();
        }

        MockInterviewSession session = optSession.get();
        InterviewRoundType roundType = session.getRoundType();

        List<InterviewResultResponse.QuestionResult> questionResults = new ArrayList<>();
        double totalScore = 0;
        List<String> allStrengths = new ArrayList<>();
        List<String> allImprovements = new ArrayList<>();

        for (SubmitInterviewRequest.AnswerEntry entry : request.getAnswers()) {
            Map<String, Object> eval = switch (roundType) {
                case HR -> evaluationEngine.evaluateHrAnswer(entry.getQuestion(), entry.getAnswer());
                case TECHNICAL -> evaluationEngine.evaluateTechnicalAnswer(entry.getQuestion(), entry.getAnswer());
                case CODING -> evaluationEngine.evaluateCodingAnswer(entry.getQuestion(), entry.getAnswer());
            };

            int score = (int) eval.get("score");
            totalScore += score;
            String verdict = (String) eval.get("verdict");
            @SuppressWarnings("unchecked")
            List<String> feedback = (List<String>) eval.get("feedback");

            if ("Strong".equals(verdict)) {
                allStrengths.add("Q: " + truncate(entry.getQuestion(), 60) + " — " + verdict);
            } else if ("Needs Improvement".equals(verdict)) {
                allImprovements.add("Q: " + truncate(entry.getQuestion(), 60) + " — " + String.join("; ", feedback));
            }

            questionResults.add(InterviewResultResponse.QuestionResult.builder()
                    .questionId(entry.getQuestionId())
                    .question(entry.getQuestion())
                    .answer(entry.getAnswer())
                    .score(score)
                    .verdict(verdict)
                    .feedback(feedback)
                    .build());
        }

        double overallScore = request.getAnswers().isEmpty() ? 0 : totalScore / request.getAnswers().size();
        overallScore = Math.round(overallScore * 10.0) / 10.0;

        String overallVerdict;
        String summary;
        if (overallScore >= 80) {
            overallVerdict = "Interview Ready";
            summary = "Excellent performance! You demonstrated strong communication, technical depth, and structured responses.";
        } else if (overallScore >= 60) {
            overallVerdict = "Competitive";
            summary = "Good performance with room for improvement. Focus on structuring answers better and providing concrete examples.";
        } else if (overallScore >= 40) {
            overallVerdict = "Needs Practice";
            summary = "Average performance. Revisit the fundamentals and practice STAR-format answers for HR, and deeper technical explanations.";
        } else {
            overallVerdict = "Foundational Work Required";
            summary = "Significant preparation needed. Review core concepts and practice mock interviews regularly.";
        }

        if (allStrengths.isEmpty()) allStrengths.add("Attempted all questions");
        if (allImprovements.isEmpty()) allImprovements.add("Keep practicing to maintain consistency");

        // Update session
        session.setAnswersJson(toJson(request.getAnswers()));
        session.setOverallScore(overallScore);
        session.setVerdict(overallVerdict);
        session.setFeedback(summary);
        repository.save(session);

        return InterviewResultResponse.builder()
                .sessionId(request.getSessionId())
                .roundType(roundType.getDisplayName())
                .overallScore(overallScore)
                .verdict(overallVerdict)
                .executiveSummary(summary)
                .questionResults(questionResults)
                .strengths(allStrengths)
                .areasForImprovement(allImprovements)
                .build();
    }

    public List<InterviewHistoryItem> getHistory(String userEmail) {
        return repository.findByUserEmailOrderByCreatedAtDesc(userEmail).stream()
                .map(s -> {
                    int qCount = 0;
                    String qJson = s.getQuestionsJson();
                    if (qJson != null && !qJson.isEmpty()) {
                        // Simple count of objects in JSON array by counting '{'
                        qCount = (int) qJson.chars().filter(c -> c == '{').count();
                    }

                    return InterviewHistoryItem.builder()
                            .sessionId(s.getId().toString())
                            .roundType(s.getRoundType().getDisplayName())
                            .overallScore(s.getOverallScore())
                            .verdict(s.getVerdict() != null ? s.getVerdict() : "In Progress")
                            .totalQuestions(qCount)
                            .createdAt(s.getCreatedAt().toString())
                            .build();
                })
                .toList();
    }

    private String toJson(Object obj) {
        return obj != null ? obj.toString() : "[]";
    }

    private String truncate(String s, int max) {
        return s != null && s.length() > max ? s.substring(0, max) + "…" : (s != null ? s : "");
    }
}

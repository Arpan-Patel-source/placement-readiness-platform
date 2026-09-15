package com.majorproject.backend.hrtraining;

import com.majorproject.backend.hrtraining.dto.*;
import com.majorproject.backend.user.User;
import com.majorproject.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HrTrainingService {

    private final HrPromptCatalog promptCatalog;
    private final HrEvaluationEngine evaluationEngine;
    private final HrSubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public List<HrPromptDto> getPrompts(HrCategory category) {
        List<HrPrompt> stream = promptCatalog.getAllPrompts();
        if (category != null) {
            stream = stream.stream().filter(p -> p.getCategory() == category).toList();
        }
        return stream.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<HrPromptDto> getPromptById(String id) {
        return promptCatalog.findById(id).map(this::toDto);
    }

    @Transactional
    public HrEvaluationResponse evaluateResponse(String userEmail, HrEvaluationRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        HrPrompt prompt = null;
        if (request.getPromptId() != null) {
            prompt = promptCatalog.findById(request.getPromptId()).orElse(null);
        }

        HrEvaluationResponse evaluation = evaluationEngine.evaluate(request.getPromptId(), prompt, request.getUserResponse());

        HrCategory cat = request.getCategory() != null ? request.getCategory() :
                (prompt != null ? prompt.getCategory() : HrCategory.SELF_INTRODUCTION);

        String question = request.getQuestionText() != null && !request.getQuestionText().isBlank()
                ? request.getQuestionText()
                : (prompt != null ? prompt.getQuestion() : "General Behavioral Question");

        HrSubmission submission = HrSubmission.builder()
                .userId(user.getId())
                .promptId(request.getPromptId() != null ? request.getPromptId() : "CUSTOM")
                .category(cat)
                .questionText(question)
                .userResponse(request.getUserResponse())
                .overallScore(evaluation.getOverallScore())
                .situationScore(evaluation.getSituationScore())
                .taskScore(evaluation.getTaskScore())
                .actionScore(evaluation.getActionScore())
                .resultScore(evaluation.getResultScore())
                .clarityScore(evaluation.getClarityScore())
                .verdict(evaluation.getVerdict())
                .strengthsText(String.join("||", evaluation.getStrengths()))
                .improvementsText(String.join("||", evaluation.getAreasForImprovement()))
                .barRaiserModelAnswer(evaluation.getBarRaiserModelAnswer())
                .build();

        HrSubmission saved = submissionRepository.save(submission);
        evaluation.setId(saved.getId().toString());

        return evaluation;
    }

    public List<HrHistoryItemDto> getUserHistory(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        List<HrSubmission> submissions = submissionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        return submissions.stream().map(sub -> {
            String snippet = sub.getUserResponse().length() > 140
                    ? sub.getUserResponse().substring(0, 137) + "..."
                    : sub.getUserResponse();

            return HrHistoryItemDto.builder()
                    .id(sub.getId().toString())
                    .promptId(sub.getPromptId())
                    .category(sub.getCategory())
                    .categoryTitle(sub.getCategory().getTitle())
                    .questionText(sub.getQuestionText())
                    .userResponseSnippet(snippet)
                    .overallScore(sub.getOverallScore())
                    .verdict(sub.getVerdict())
                    .createdAt(sub.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    private HrPromptDto toDto(HrPrompt p) {
        return HrPromptDto.builder()
                .id(p.getId())
                .category(p.getCategory())
                .categoryTitle(p.getCategory().getTitle())
                .question(p.getQuestion())
                .recruiterIntent(p.getRecruiterIntent())
                .keyPointsToInclude(p.getKeyPointsToInclude())
                .commonPitfalls(p.getCommonPitfalls())
                .sampleModelAnswer(p.getSampleModelAnswer())
                .companyTags(p.getCompanyTags())
                .build();
    }
}

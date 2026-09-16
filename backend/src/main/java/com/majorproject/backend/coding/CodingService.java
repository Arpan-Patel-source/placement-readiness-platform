package com.majorproject.backend.coding;

import com.majorproject.backend.coding.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodingService {

    private final CodingProblemBank problemBank;
    private final CodeJudgeService judgeService;
    private final CodingSubmissionRepository submissionRepository;

    public List<CodingCategorySummaryDto> getCategorySummaries() {
        List<CodingProblem> all = problemBank.getAllProblems();
        List<CodingCategorySummaryDto> summaries = new ArrayList<>();

        for (CodingCategory cat : CodingCategory.values()) {
            List<CodingProblem> catProblems = all.stream()
                    .filter(p -> p.getCategory() == cat).toList();

            summaries.add(CodingCategorySummaryDto.builder()
                    .category(cat.name())
                    .displayName(cat.getDisplayName())
                    .totalProblems(catProblems.size())
                    .easyCount((int) catProblems.stream().filter(p -> p.getDifficulty() == CodingDifficulty.EASY).count())
                    .mediumCount((int) catProblems.stream().filter(p -> p.getDifficulty() == CodingDifficulty.MEDIUM).count())
                    .hardCount((int) catProblems.stream().filter(p -> p.getDifficulty() == CodingDifficulty.HARD).count())
                    .build());
        }
        return summaries;
    }

    public List<CodingProblemDto> getProblems(CodingCategory category, CodingDifficulty difficulty, String search) {
        List<CodingProblem> stream = problemBank.getAllProblems();

        if (category != null) {
            stream = stream.stream().filter(p -> p.getCategory() == category).toList();
        }
        if (difficulty != null) {
            stream = stream.stream().filter(p -> p.getDifficulty() == difficulty).toList();
        }
        if (search != null && !search.trim().isEmpty()) {
            String q = search.trim().toLowerCase();
            stream = stream.stream()
                    .filter(p -> p.getTitle().toLowerCase().contains(q) || p.getDescription().toLowerCase().contains(q))
                    .toList();
        }

        return stream.stream().map(this::toDto).toList();
    }

    public Optional<CodingProblemDto> getProblemById(String id) {
        return problemBank.getProblemById(id).map(this::toDto);
    }

    public CodingResultResponse submitSolution(String userEmail, CodingSubmitRequest request) {
        Optional<CodingProblem> optProblem = problemBank.getProblemById(request.getProblemId());
        if (optProblem.isEmpty()) {
            return CodingResultResponse.builder()
                    .problemId(request.getProblemId())
                    .status("ERROR")
                    .verdict("Problem not found")
                    .feedback("The requested problem ID does not exist.")
                    .build();
        }

        CodingProblem problem = optProblem.get();

        // For MVP: simulate execution by accepting user outputs
        // In production, code would be sent to Judge0 or executed in sandbox
        CodingResultResponse result = judgeService.evaluate(
                problem, request.getCode(), request.getLanguage(),
                List.of(), // empty outputs = all fail (user needs to use the run endpoint)
                request.isRunSampleOnly()
        );

        // Persist submission
        CodingSubmission submission = CodingSubmission.builder()
                .userEmail(userEmail)
                .problemId(request.getProblemId())
                .language(request.getLanguage())
                .code(request.getCode())
                .status(result.getStatus())
                .passedTestCases(result.getPassedTestCases())
                .totalTestCases(result.getTotalTestCases())
                .runtimeMs(result.getRuntimeMs())
                .memoryKb(result.getMemoryKb())
                .build();

        CodingSubmission saved = submissionRepository.save(submission);
        result.setSubmissionId(saved.getId().toString());

        return result;
    }

    public List<CodingHistoryItem> getUserHistory(String userEmail) {
        return submissionRepository.findByUserEmailOrderByCreatedAtDesc(userEmail).stream()
                .map(sub -> {
                    Optional<CodingProblem> p = problemBank.getProblemById(sub.getProblemId());
                    return CodingHistoryItem.builder()
                            .submissionId(sub.getId().toString())
                            .problemId(sub.getProblemId())
                            .problemTitle(p.map(CodingProblem::getTitle).orElse("Unknown"))
                            .category(p.map(prob -> prob.getCategory().getDisplayName()).orElse("Unknown"))
                            .difficulty(p.map(prob -> prob.getDifficulty().name()).orElse("Unknown"))
                            .language(sub.getLanguage().getDisplayName())
                            .status(sub.getStatus())
                            .passedTestCases(sub.getPassedTestCases())
                            .totalTestCases(sub.getTotalTestCases())
                            .runtimeMs(sub.getRuntimeMs())
                            .createdAt(sub.getCreatedAt().toString())
                            .build();
                })
                .toList();
    }

    public Map<String, Object> getUserStats(String userEmail) {
        long totalSubmissions = submissionRepository.findByUserEmailOrderByCreatedAtDesc(userEmail).size();
        long accepted = submissionRepository.countByUserEmailAndStatus(userEmail, "ACCEPTED");
        long totalProblems = problemBank.getAllProblems().size();

        // Count distinct accepted problems
        Set<String> solvedProblemIds = submissionRepository.findByUserEmailOrderByCreatedAtDesc(userEmail).stream()
                .filter(s -> "ACCEPTED".equals(s.getStatus()))
                .map(CodingSubmission::getProblemId)
                .collect(Collectors.toSet());

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalSubmissions", totalSubmissions);
        stats.put("acceptedSubmissions", accepted);
        stats.put("totalProblems", totalProblems);
        stats.put("solvedProblems", solvedProblemIds.size());
        stats.put("acceptanceRate", totalSubmissions > 0 ? Math.round((double) accepted / totalSubmissions * 100.0) : 0);
        return stats;
    }

    private CodingProblemDto toDto(CodingProblem p) {
        List<CodingProblemDto.TestCaseDto> sampleCases = p.getTestCases().stream()
                .filter(tc -> !tc.isHidden())
                .map(tc -> CodingProblemDto.TestCaseDto.builder()
                        .input(tc.getInput())
                        .expectedOutput(tc.getExpectedOutput())
                        .build())
                .toList();

        return CodingProblemDto.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .category(p.getCategory())
                .difficulty(p.getDifficulty())
                .constraints(p.getConstraints())
                .inputFormat(p.getInputFormat())
                .outputFormat(p.getOutputFormat())
                .sampleTestCases(sampleCases)
                .totalTestCases(p.getTestCases().size())
                .starterCodeJava(p.getStarterCodeJava())
                .starterCodePython(p.getStarterCodePython())
                .starterCodeCpp(p.getStarterCodeCpp())
                .starterCodeC(p.getStarterCodeC())
                .hints(p.getHints())
                .timeComplexity(p.getTimeComplexity())
                .spaceComplexity(p.getSpaceComplexity())
                .companiesAsked(p.getCompaniesAsked())
                .build();
    }
}

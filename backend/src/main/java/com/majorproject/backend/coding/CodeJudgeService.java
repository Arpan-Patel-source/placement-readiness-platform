package com.majorproject.backend.coding;

import com.majorproject.backend.coding.dto.CodingResultResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Local code judge that evaluates submitted code by comparing expected output
 * against user-provided stdout for each test case.
 *
 * This is a simulated judge — it does not actually execute user code server-side.
 * In production, this would be replaced with a Judge0 API integration.
 * The frontend sends the code to the backend, and the backend evaluates
 * by running the code description against test cases.
 *
 * For the MVP, the judge works by accepting the user's claimed output
 * (computed client-side or simulated) and comparing it to expected output.
 */
@Service
public class CodeJudgeService {

    /**
     * Evaluate code output against test cases.
     * In production this would actually compile and run the code.
     * For MVP, we accept claimed outputs and validate them.
     */
    public CodingResultResponse evaluate(CodingProblem problem, String code, CodingLanguage language,
                                          List<String> userOutputs, boolean sampleOnly) {

        List<TestCase> testCases = problem.getTestCases();
        List<TestCase> toRun = sampleOnly
                ? testCases.stream().filter(tc -> !tc.isHidden()).toList()
                : testCases;

        List<CodingResultResponse.TestCaseResult> results = new ArrayList<>();
        int passed = 0;
        long simulatedRuntime = 0;

        for (int i = 0; i < toRun.size(); i++) {
            TestCase tc = toRun.get(i);
            String userOutput = (userOutputs != null && i < userOutputs.size())
                    ? userOutputs.get(i).trim()
                    : "";
            String expected = tc.getExpectedOutput().trim();
            boolean match = expected.equals(userOutput);

            if (match) passed++;
            simulatedRuntime += (long) (Math.random() * 50 + 10); // simulate 10-60ms per test case

            results.add(CodingResultResponse.TestCaseResult.builder()
                    .testCaseIndex(i)
                    .passed(match)
                    .input(tc.isHidden() ? "[Hidden]" : tc.getInput())
                    .expectedOutput(tc.isHidden() ? "[Hidden]" : expected)
                    .actualOutput(tc.isHidden() && !match ? "[Hidden]" : userOutput)
                    .isHidden(tc.isHidden())
                    .build());
        }

        String status;
        String feedback;
        if (passed == toRun.size()) {
            status = "ACCEPTED";
            feedback = "All test cases passed! Great work.";
        } else if (passed == 0) {
            status = "WRONG_ANSWER";
            feedback = "No test cases passed. Review your logic and try again.";
        } else {
            status = "WRONG_ANSWER";
            feedback = String.format("%d of %d test cases passed. Check edge cases.", passed, toRun.size());
        }

        long simulatedMemory = (long) (Math.random() * 5000 + 2000); // 2-7 MB

        return CodingResultResponse.builder()
                .problemId(problem.getId())
                .status(status)
                .passedTestCases(passed)
                .totalTestCases(toRun.size())
                .runtimeMs(simulatedRuntime)
                .memoryKb(simulatedMemory)
                .testCaseResults(results)
                .verdict(status.equals("ACCEPTED")
                        ? "✅ Accepted — " + problem.getTimeComplexity() + " time, " + problem.getSpaceComplexity() + " space"
                        : "❌ Wrong Answer — " + passed + "/" + toRun.size() + " passed")
                .feedback(feedback)
                .build();
    }
}

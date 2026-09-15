package com.majorproject.backend.aptitude;

import com.majorproject.backend.aptitude.dto.AptitudeCategorySummaryDto;
import com.majorproject.backend.aptitude.dto.AptitudeQuestionDto;
import com.majorproject.backend.aptitude.dto.AptitudeResultResponse;
import com.majorproject.backend.aptitude.dto.AptitudeSubmitRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AptitudeServiceTest {

    private AptitudeService aptitudeService;
    private AptitudeQuestionBank questionBank;

    @BeforeEach
    void setUp() {
        questionBank = new AptitudeQuestionBank();
        aptitudeService = new AptitudeService(questionBank);
    }

    @Test
    void testCategorySummaries() {
        List<AptitudeCategorySummaryDto> summaries = aptitudeService.getCategorySummaries();
        assertNotNull(summaries);
        assertEquals(3, summaries.size()); // Quantitative, Logical, Verbal
        assertTrue(summaries.stream().anyMatch(s -> s.getCategory() == AptitudeCategory.QUANTITATIVE));
        assertTrue(summaries.stream().anyMatch(s -> s.getCategory() == AptitudeCategory.LOGICAL_REASONING));
        assertTrue(summaries.stream().anyMatch(s -> s.getCategory() == AptitudeCategory.VERBAL_ABILITY));
    }

    @Test
    void testGetQuestionsByTopic() {
        List<AptitudeQuestionDto> questions = aptitudeService.getQuestions(AptitudeCategory.QUANTITATIVE, "Percentages", null, 10);
        assertNotNull(questions);
        assertFalse(questions.isEmpty());
        assertTrue(questions.stream().allMatch(q -> q.getTopic().equalsIgnoreCase("Percentages")));
    }

    @Test
    void testGenerateMockTest() {
        List<AptitudeQuestionDto> mockTest = aptitudeService.generateMockTest(null, 5);
        assertNotNull(mockTest);
        assertEquals(5, mockTest.size());
    }

    @Test
    void testEvaluateTestSubmission() {
        List<AptitudeQuestion> all = questionBank.getAllQuestions();
        AptitudeQuestion q1 = all.get(0);
        AptitudeQuestion q2 = all.get(1);

        AptitudeSubmitRequest request = AptitudeSubmitRequest.builder()
                .testId("TEST_001")
                .totalTimeSpentSeconds(120)
                .answers(List.of(
                        AptitudeSubmitRequest.AnswerSubmission.builder()
                                .questionId(q1.getId())
                                .selectedOptionIndex(q1.getCorrectOptionIndex()) // Correct
                                .timeSpentSeconds(60)
                                .build(),
                        AptitudeSubmitRequest.AnswerSubmission.builder()
                                .questionId(q2.getId())
                                .selectedOptionIndex((q2.getCorrectOptionIndex() + 1) % 4) // Wrong
                                .timeSpentSeconds(60)
                                .build()
                ))
                .build();

        AptitudeResultResponse result = aptitudeService.evaluateTest(request);
        assertNotNull(result);
        assertEquals(2, result.getTotalQuestions());
        assertEquals(1, result.getCorrectCount());
        assertEquals(1, result.getIncorrectCount());
        assertEquals(50.0, result.getScorePercentage());
        assertEquals(2, result.getQuestionReviews().size());
        assertTrue(result.getQuestionReviews().get(0).isCorrect());
        assertFalse(result.getQuestionReviews().get(1).isCorrect());
    }
}

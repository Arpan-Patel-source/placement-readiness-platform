package com.majorproject.backend.hrtraining;

import com.majorproject.backend.hrtraining.dto.HrEvaluationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HrEvaluationEngineTest {

    private HrEvaluationEngine evaluationEngine;
    private HrPromptCatalog promptCatalog;

    @BeforeEach
    void setUp() {
        evaluationEngine = new HrEvaluationEngine();
        promptCatalog = new HrPromptCatalog();
    }

    @Test
    void testStarAnswerEvaluationHighQuality() {
        HrPrompt prompt = promptCatalog.findById("HR_LEAD_01").orElse(null);
        assertNotNull(prompt);

        String answer = "During my final year project, we faced critical latency spikes on the payment service. " +
                "My responsibility was to investigate root causes and optimize database transactions. " +
                "I engineered a Redis caching layer, profiled slow SQL queries, and refactored the connection pool. " +
                "This resulted in a 42% reduction in latency and prevented timeouts for 1500 users.";

        HrEvaluationResponse response = evaluationEngine.evaluate(prompt.getId(), prompt, answer);

        assertNotNull(response);
        assertTrue(response.getOverallScore() >= 75, "High-quality STAR answer should score >= 75");
        assertTrue(response.getSituationScore() >= 70);
        assertTrue(response.getTaskScore() >= 70);
        assertTrue(response.getActionScore() >= 75);
        assertTrue(response.getResultScore() >= 70);
        assertFalse(response.getMetricsDetected().isEmpty(), "Should detect metrics like 42%");
        assertFalse(response.getActionVerbsDetected().isEmpty(), "Should detect action verbs like engineered, refactored");
    }

    @Test
    void testTooBriefAnswerIsPenalized() {
        HrPrompt prompt = promptCatalog.findById("HR_LEAD_01").orElse(null);
        String briefAnswer = "I solved a problem when my team was stuck.";

        HrEvaluationResponse response = evaluationEngine.evaluate(prompt.getId(), prompt, briefAnswer);

        assertNotNull(response);
        assertTrue(response.getOverallScore() <= 45, "Very brief answers lacking detail should be penalized");
        assertFalse(response.getAreasForImprovement().isEmpty());
    }
}

package com.majorproject.backend.prediction;

import com.majorproject.backend.dashboard.DashboardService;
import com.majorproject.backend.dashboard.ReadinessScoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final DashboardService dashboardService;
    private final CompanyProfileCatalog companyCatalog;

    public List<CompanyPredictionDto> getPredictions(String userEmail) {
        ReadinessScoreResponse readiness = dashboardService.getReadinessScore(userEmail);

        List<CompanyPredictionDto> predictions = new ArrayList<>();

        for (CompanyProfile company : companyCatalog.getAllProfiles()) {
            double weightedScore =
                    (readiness.getAptitudeScore() * company.getAptitudeWeight()) +
                    (readiness.getCodingScore() * company.getCodingWeight()) +
                    (readiness.getHrScore() * company.getHrWeight()) +
                    (readiness.getTechnicalScore() * company.getTechnicalWeight()) +
                    (readiness.getResumeScore() * company.getResumeWeight());

            // Apply cutoff penalties
            double penalty = 0;
            List<String> focusAreas = new ArrayList<>();
            if (readiness.getAptitudeScore() < company.getAptitudeCutoff()) {
                penalty += 15;
                focusAreas.add("Aptitude (below " + (int) company.getAptitudeCutoff() + "% cutoff)");
            }
            if (readiness.getCodingScore() < company.getCodingCutoff()) {
                penalty += 15;
                focusAreas.add("Coding (below " + (int) company.getCodingCutoff() + "% cutoff)");
            }
            if (readiness.getHrScore() < 40) {
                focusAreas.add("HR interview skills");
            }
            if (readiness.getResumeScore() < 60) {
                focusAreas.add("Resume ATS optimization");
            }

            double probability = Math.max(5, Math.min(95, weightedScore - penalty));
            probability = Math.round(probability * 10.0) / 10.0;

            double interviewRate = Math.max(10, Math.min(90, (readiness.getInterviewScore() * 0.6) + (readiness.getHrScore() * 0.4)));
            interviewRate = Math.round(interviewRate * 10.0) / 10.0;

            if (focusAreas.isEmpty()) focusAreas.add("Maintain current preparation level");

            String advice;
            if (probability >= 75) advice = "Strong match! Keep your current pace.";
            else if (probability >= 50) advice = "Good chances. Focus on: " + String.join(", ", focusAreas);
            else advice = "Needs improvement in: " + String.join(", ", focusAreas);

            predictions.add(CompanyPredictionDto.builder()
                    .companyName(company.getName())
                    .placementProbability(probability)
                    .interviewSuccessRate(interviewRate)
                    .prepAdvice(advice)
                    .focusAreas(focusAreas)
                    .previousQuestionTopics(company.getCommonTopics())
                    .aptitudePattern(company.getAptitudePattern())
                    .codingDifficulty(company.getCodingDifficulty())
                    .interviewStyle(company.getInterviewStyle())
                    .build());
        }

        // Sort by probability descending
        predictions.sort((a, b) -> Double.compare(b.getPlacementProbability(), a.getPlacementProbability()));

        return predictions;
    }
}

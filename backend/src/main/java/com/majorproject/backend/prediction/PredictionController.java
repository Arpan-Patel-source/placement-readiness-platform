package com.majorproject.backend.prediction;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/prediction")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyPredictionDto>> getCompanyPredictions(Principal principal) {
        String email = principal != null ? principal.getName() : "guest@placementai.internal";
        return ResponseEntity.ok(predictionService.getPredictions(email));
    }
}

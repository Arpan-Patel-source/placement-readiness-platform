package com.majorproject.backend.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/readiness")
    public ResponseEntity<ReadinessScoreResponse> getReadinessScore(Principal principal) {
        String email = principal != null ? principal.getName() : "guest@placementai.internal";
        return ResponseEntity.ok(dashboardService.getReadinessScore(email));
    }
}

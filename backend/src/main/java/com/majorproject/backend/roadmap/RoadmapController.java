package com.majorproject.backend.roadmap;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    @GetMapping("/generate")
    public ResponseEntity<RoadmapResponse> generateRoadmap(Principal principal) {
        String email = principal != null ? principal.getName() : "guest@placementai.internal";
        return ResponseEntity.ok(roadmapService.generateRoadmap(email));
    }
}

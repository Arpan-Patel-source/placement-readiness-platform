package com.majorproject.backend.resume;

import com.majorproject.backend.resume.ResumeAnalysisEngine.AnalysisResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResumeAnalysisEngineTest {

    private ResumeAnalysisEngine engine;

    @BeforeEach
    void setUp() {
        RoleSkillCatalog catalog = new RoleSkillCatalog();
        engine = new ResumeAnalysisEngine(catalog, "", "gemini-1.5-flash");
    }

    @Test
    void testAnalyze_JavaBackendDeveloper_Success() {
        String sampleResume = """
                Azhar Khan
                azhar@example.com | +91 9876543210 | linkedin.com/in/azharkhan | github.com/azharkhan
                
                SUMMARY
                Passionate Software Engineer specializing in backend systems.
                
                EDUCATION
                Bachelor of Technology in Computer Science
                XYZ Institute of Technology, 2021 - 2025
                
                EXPERIENCE & PROJECTS
                Placement AI Platform
                - Architected and engineered a scalable RESTful microservice using Java, Spring Boot, and PostgreSQL.
                - Optimized database query response times by 45%, handling over 10,000 requests per minute.
                - Integrated Redis caching and automated continuous integration with Git and Docker.
                
                SKILLS
                Java, Spring Boot, SQL, Hibernate, Git, REST APIs, Docker, PostgreSQL
                """;

        AnalysisResult result = engine.analyze(sampleResume, "Java Backend Developer");

        assertNotNull(result);
        assertTrue(result.atsScore >= 75, "ATS score should be high for well-formatted resume");
        assertTrue(result.strengthScore >= 70, "Strength score should be high due to metrics and strong action verbs");
        assertTrue(result.skillsFound.contains("Java"));
        assertTrue(result.skillsFound.contains("Spring Boot"));
        assertTrue(result.skillsFound.contains("SQL"));
        assertNotNull(result.sections);
        assertTrue(result.sections.getContactScore() >= 80);
        assertTrue(result.sections.getStructureScore() >= 80);
        assertFalse(result.actionableSuggestions.isEmpty());
        assertNotNull(result.executiveSummary);
    }

    @Test
    void testAnalyze_DetectsMissingSkillsAndGaps() {
        String minimalResume = """
                John Doe
                john@example.com
                
                EDUCATION
                B.Tech CS
                
                PROJECTS
                - Helped with basic HTML and CSS website.
                """;

        AnalysisResult result = engine.analyze(minimalResume, "Java Backend Developer");

        assertNotNull(result);
        assertTrue(result.missingSkills.contains("Java"));
        assertTrue(result.missingSkills.contains("Spring Boot"));
        assertTrue(result.missingSkills.contains("SQL"));
        assertTrue(result.strengthScore < 60, "Strength score should be low for minimal resume lacking metrics");
        assertTrue(result.actionableSuggestions.stream().anyMatch(s -> s.contains("Java") || s.contains("Spring Boot")));
    }
}

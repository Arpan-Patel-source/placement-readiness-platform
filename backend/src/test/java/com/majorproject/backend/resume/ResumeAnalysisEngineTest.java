package com.majorproject.backend.resume;

import com.majorproject.backend.resume.ResumeAnalysisEngine.AnalysisResult;
import com.majorproject.backend.resume.dto.*;
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
                azhar@example.com | +91 9876543210 | linkedin.com/in/azhar-khan | github.com/azharkhan
                
                PROFESSIONAL SUMMARY
                Results-driven Software Engineer with extensive experience in enterprise Java, Spring Boot, and microservices architecture.
                Skilled in designing scalable REST APIs, database optimization, and cloud-native containerized deployments.
                
                EDUCATION
                Bachelor of Technology in Computer Science and Engineering
                XYZ Institute of Technology, 2021 - 2025 | CGPA: 8.9/10
                
                TECHNICAL SKILLS
                Languages: Java, SQL, Python
                Frameworks & Libraries: Spring Boot, Hibernate, JPA, REST APIs, Spring Security
                Databases & Caching: PostgreSQL, MySQL, Redis
                Tools & DevOps: Git, Maven, Docker, CI/CD Pipeline, Linux
                Core Competencies: Object-Oriented Programming (OOP), Clean Architecture, Unit Testing, API Design
                
                EXPERIENCE & PROJECTS
                Placement AI Enterprise Platform (github.com/azharkhan/placement-ai)
                - Architected and engineered a scalable RESTful microservice using Java, Spring Boot, JPA, and PostgreSQL.
                - Optimized database query response times by 45%, reducing average API latency from 240ms to under 50ms for over 10,000 concurrent requests per minute.
                - Implemented Redis caching for high-frequency queries, achieving a 99.9% uptime SLA and 3x throughput improvement.
                - Automated continuous integration and deployment with Git, Docker, and GitHub Actions, incorporating JUnit unit testing with 85% code coverage.
                
                Distributed Task Scheduler Service (github.com/azharkhan/task-scheduler)
                - Developed an asynchronous event-driven scheduler handling over 50,000 background jobs daily using Spring Boot and PostgreSQL.
                - Streamlined database indexing and transaction management, cutting CPU utilization by 30%.
                """;

        assertDoesNotThrow(() -> engine.validateResumeDocument(sampleResume, "resume.pdf"));

        AnalysisResult result = engine.analyze(sampleResume, "Java Backend Developer");

        assertNotNull(result);
        assertTrue(result.atsScore >= 70, "ATS score should be high (>=70) for well-formatted complete resume, got: " + result.atsScore);
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
    void testValidateResumeDocument_RejectsNonResume() {
        String ptaMeetingDocument = """
                Event: Parent-Teacher Association Meeting
                Date: September 20, 2026
                Location: Main Auditorium
                
                Agenda:
                1. Review of annual school curriculum and performance.
                2. Parent queries regarding bus transportation and cafeteria fees.
                3. Discussion on upcoming sports day preparations.
                
                Contact: schooloffice@example.com | +91 9876543210
                Signed by PTA President and Secretary.
                """;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                engine.validateResumeDocument(ptaMeetingDocument, "PTA_Meeting.pdf")
        );

        assertTrue(ex.getMessage().contains("not recognized as a resume") || ex.getMessage().contains("does not appear to be a resume"));
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

        assertDoesNotThrow(() -> engine.validateResumeDocument(minimalResume, "minimal_resume.txt"));

        AnalysisResult result = engine.analyze(minimalResume, "Java Backend Developer");

        assertNotNull(result);
        assertTrue(result.missingSkills.contains("Java"));
        assertTrue(result.missingSkills.contains("Spring Boot"));
        assertTrue(result.missingSkills.contains("SQL"));
        assertTrue(result.strengthScore < 50, "Strength score should be low for minimal resume lacking metrics");
        assertTrue(result.actionableSuggestions.stream().anyMatch(s -> s.contains("Java") || s.contains("Spring Boot")));
    }

    @Test
    void testRewriteBullet_Success() {
        BulletRewriteRequest req = BulletRewriteRequest.builder()
                .bulletText("Developed MindBridge mental health app with Spring Boot and Java.")
                .roleTitle("Java Backend Developer")
                .build();

        BulletRewriteResponse res = engine.rewriteBullet(req);
        assertNotNull(res);
        assertNotNull(res.getQuantifiedXyz());
        assertNotNull(res.getEnterpriseStack());
        assertNotNull(res.getLeadershipImpact());
        assertTrue(res.getQuantifiedXyz().getText().contains("MindBridge"));
        assertFalse(res.getImprovementsApplied().isEmpty());
    }

    @Test
    void testMatchJobDescription_Success() {
        String resumeText = """
                Azhar Khan
                azhar@example.com | +91 7067289757
                Skills: Java, Spring Boot, REST APIs, SQL, MySQL, Git, Maven, Hibernate, JPA
                Experience: Developed MindBridge mental health platform using Spring Boot and Java.
                """;

        JdMatchRequest req = JdMatchRequest.builder()
                .jobDescriptionText("Looking for a Java Backend Developer with expertise in Spring Boot, REST APIs, Microservices, Docker, Redis, and MySQL.")
                .companyName("Bajaj Finserv")
                .targetRole("Java Backend Developer")
                .build();

        JdMatchResponse res = engine.matchJobDescription(resumeText, req);
        assertNotNull(res);
        assertTrue(res.getMatchScore() > 0);
        assertTrue(res.getMatchedSkills().contains("Java") || res.getMatchedSkills().contains("Spring Boot"));
        assertTrue(res.getMissingMustHaveSkills().contains("Docker") || res.getMissingMustHaveSkills().contains("Microservices"));
        assertNotNull(res.getMatchVerdict());
        assertFalse(res.getTailoringTips().isEmpty());
    }

    @Test
    void testCompareAcrossAllRoles_Success() {
        String resumeText = """
                Azhar Khan
                azhar@example.com | +91 9876543210 | linkedin.com/in/azhar-khan | github.com/azharkhan
                
                EDUCATION
                B.Tech in Computer Science, XYZ Institute, 2021-2025
                
                SKILLS
                Java, Spring Boot, REST APIs, SQL, MySQL, Hibernate, JPA, Git, Maven
                
                PROJECTS
                MindBridge AI Platform
                - Architected backend microservices using Java, Spring Boot, and MySQL.
                - Reduced query response times by 35% and handled 10,000+ daily queries.
                """;

        CrossRoleComparisonResponse res = engine.compareAcrossAllRoles("Azhar Khan", resumeText, "Java Backend Developer");
        assertNotNull(res);
        assertFalse(res.getRoleScores().isEmpty());
        // Java backend should be among top scores
        assertTrue(res.getRoleScores().stream().anyMatch(r -> r.getRoleTitle().contains("Java") && r.getAtsScore() > 40));
    }

    @Test
    void testExtractAtsParsedTree_Success() {
        String resumeText = """
                Azhar Khan
                136, Ilyas Colony, Indore (M.P.)
                azharkhan230826@acropolis.in | (+91) 7067289757 | linkedin.com/in/azhar-khan | github.com/azharkhan924
                
                EDUCATION
                Pursuing B.Tech. from AITR, Indore (2023-27) with 79.7%
                
                SKILLS
                Java, Spring Boot, REST APIs, SQL, MySQL, Git, Maven
                
                PROJECTS
                MindBridge: Mental Health App
                - Developed AI platform with Spring Boot and MySQL
                """;

        AtsParsedTreeDto tree = engine.extractAtsParsedTree(resumeText, "resume.pdf");
        assertNotNull(tree);
        assertEquals("Azhar Khan", tree.getCandidateName());
        assertNotNull(tree.getDetectedEmail());
        assertNotNull(tree.getDetectedLinkedIn());
        assertNotNull(tree.getEducationSummary());
        assertNotNull(tree.getHealth());
    }
}

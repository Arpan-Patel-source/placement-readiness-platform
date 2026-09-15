package com.majorproject.backend.resume;

import com.majorproject.backend.profile.StudentProfile;
import com.majorproject.backend.profile.StudentProfileRepository;
import com.majorproject.backend.resume.dto.ResumeAnalysisResponse;
import com.majorproject.backend.user.Role;
import com.majorproject.backend.user.User;
import com.majorproject.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeAnalysisServiceTest {

    @Mock
    private ResumeTextExtractor textExtractor;

    @Mock
    private ResumeAnalysisEngine analysisEngine;

    @Mock
    private ResumeAnalysisRepository resumeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentProfileRepository profileRepository;

    @InjectMocks
    private ResumeAnalysisService service;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("azhar@example.com")
                .password("encoded_pass")
                .role(Role.STUDENT)
                .build();
    }

    @Test
    void testAnalyzeResume_Success() {
        String sampleText = "Azhar Khan Java Spring Boot REST APIs";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                sampleText.getBytes(StandardCharsets.UTF_8)
        );

        when(userRepository.findByEmail("azhar@example.com")).thenReturn(Optional.of(testUser));
        when(profileRepository.findByUser(testUser)).thenReturn(Optional.of(
                StudentProfile.builder().targetRole("Java Backend Developer").build()
        ));
        when(textExtractor.extractText(file)).thenReturn(sampleText);

        ResumeAnalysisEngine.AnalysisResult mockResult = new ResumeAnalysisEngine.AnalysisResult();
        mockResult.atsScore = 85;
        mockResult.strengthScore = 80;
        mockResult.readinessScore = 82;
        mockResult.sections = com.majorproject.backend.resume.dto.SectionScoreDto.builder()
                .contactScore(90)
                .structureScore(85)
                .skillsScore(80)
                .impactScore(75)
                .build();
        mockResult.skillsFound = List.of("Java", "Spring Boot");
        mockResult.missingSkills = List.of("Docker");
        mockResult.criticalKeywords = List.of("Microservices");
        mockResult.actionableSuggestions = List.of("Add Docker containerization");
        mockResult.grammarSuggestions = List.of("Formatting looks clean");
        mockResult.strengths = List.of("Solid Java coverage");
        mockResult.executiveSummary = "Strong candidate profile.";

        when(analysisEngine.analyze(sampleText, "Java Backend Developer")).thenReturn(mockResult);

        when(resumeRepository.save(any(ResumeAnalysis.class))).thenAnswer(invocation -> {
            ResumeAnalysis entity = invocation.getArgument(0);
            entity.setId(UUID.randomUUID());
            entity.setCreatedAt(LocalDateTime.now());
            return entity;
        });

        ResumeAnalysisResponse response = service.analyzeResume("azhar@example.com", file, null);

        assertNotNull(response);
        assertEquals(85, response.getAtsScore());
        assertEquals(80, response.getStrengthScore());
        assertEquals(82, response.getReadinessScore());
        assertEquals("resume.pdf", response.getFileName());
        assertEquals("Java Backend Developer", response.getTargetRole());
        assertTrue(response.getSkillsFound().contains("Java"));
        verify(resumeRepository, times(1)).save(any(ResumeAnalysis.class));
    }
}

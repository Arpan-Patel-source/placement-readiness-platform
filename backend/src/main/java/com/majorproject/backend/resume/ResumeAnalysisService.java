package com.majorproject.backend.resume;

import com.majorproject.backend.profile.StudentProfile;
import com.majorproject.backend.profile.StudentProfileRepository;
import com.majorproject.backend.resume.ResumeAnalysisEngine.AnalysisResult;
import com.majorproject.backend.resume.dto.ResumeAnalysisResponse;
import com.majorproject.backend.resume.dto.ResumeHistoryItemResponse;
import com.majorproject.backend.resume.dto.SectionScoreDto;
import com.majorproject.backend.user.User;
import com.majorproject.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeAnalysisService {

    private final ResumeTextExtractor textExtractor;
    private final ResumeAnalysisEngine analysisEngine;
    private final ResumeAnalysisRepository resumeRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;

    @Transactional
    public ResumeAnalysisResponse analyzeResume(String userEmail, MultipartFile file, String targetRoleOverride) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        // Resolve target role
        String targetRole = targetRoleOverride;
        if (targetRole == null || targetRole.isBlank()) {
            StudentProfile profile = profileRepository.findByUser(user).orElse(null);
            if (profile != null && profile.getTargetRole() != null && !profile.getTargetRole().isBlank()) {
                targetRole = profile.getTargetRole();
            } else {
                targetRole = "Java Backend Developer";
            }
        }

        // Extract text
        String extractedText = textExtractor.extractText(file);

        // Validate document authenticity (verify it is a resume, not a random document)
        analysisEngine.validateResumeDocument(extractedText, file.getOriginalFilename());

        // Run engine
        AnalysisResult analysis = analysisEngine.analyze(extractedText, targetRole);

        // Raw snippet for storage
        String snippet = extractedText.length() > 2000 ? extractedText.substring(0, 2000) : extractedText;

        // Persist
        ResumeAnalysis entity = ResumeAnalysis.builder()
                .user(user)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSizeBytes(file.getSize())
                .targetRole(targetRole)
                .atsScore(analysis.atsScore)
                .strengthScore(analysis.strengthScore)
                .readinessScore(analysis.readinessScore)
                .sectionContactScore(analysis.sections.getContactScore())
                .sectionStructureScore(analysis.sections.getStructureScore())
                .sectionSkillsScore(analysis.sections.getSkillsScore())
                .sectionImpactScore(analysis.sections.getImpactScore())
                .skillsFound(analysis.skillsFound)
                .missingSkills(analysis.missingSkills)
                .criticalKeywords(analysis.criticalKeywords)
                .actionableSuggestions(analysis.actionableSuggestions)
                .grammarSuggestions(analysis.grammarSuggestions)
                .strengths(analysis.strengths)
                .executiveSummary(analysis.executiveSummary)
                .rawTextSnippet(snippet)
                .build();

        ResumeAnalysis saved = resumeRepository.save(entity);

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public ResumeAnalysisResponse getLatestAnalysis(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        return resumeRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                .map(this::mapToResponse)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ResumeHistoryItemResponse> getHistory(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        return resumeRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(r -> ResumeHistoryItemResponse.builder()
                        .id(r.getId())
                        .fileName(r.getFileName())
                        .targetRole(r.getTargetRole())
                        .atsScore(r.getAtsScore())
                        .strengthScore(r.getStrengthScore())
                        .readinessScore(r.getReadinessScore())
                        .createdAt(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResumeAnalysisResponse getAnalysisById(String userEmail, UUID id) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        ResumeAnalysis entity = resumeRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Resume analysis not found or access denied"));

        return mapToResponse(entity);
    }

    @Transactional
    public void deleteAnalysis(String userEmail, UUID id) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        ResumeAnalysis entity = resumeRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Resume analysis not found or access denied"));

        resumeRepository.delete(entity);
    }

    private ResumeAnalysisResponse mapToResponse(ResumeAnalysis r) {
        return ResumeAnalysisResponse.builder()
                .id(r.getId())
                .fileName(r.getFileName())
                .fileType(r.getFileType())
                .fileSizeBytes(r.getFileSizeBytes())
                .targetRole(r.getTargetRole())
                .atsScore(r.getAtsScore())
                .strengthScore(r.getStrengthScore())
                .readinessScore(r.getReadinessScore())
                .sections(SectionScoreDto.builder()
                        .contactScore(r.getSectionContactScore())
                        .structureScore(r.getSectionStructureScore())
                        .skillsScore(r.getSectionSkillsScore())
                        .impactScore(r.getSectionImpactScore())
                        .build())
                .skillsFound(r.getSkillsFound())
                .missingSkills(r.getMissingSkills())
                .criticalKeywords(r.getCriticalKeywords())
                .actionableSuggestions(r.getActionableSuggestions())
                .grammarSuggestions(r.getGrammarSuggestions())
                .strengths(r.getStrengths())
                .executiveSummary(r.getExecutiveSummary())
                .createdAt(r.getCreatedAt())
                .build();
    }
}

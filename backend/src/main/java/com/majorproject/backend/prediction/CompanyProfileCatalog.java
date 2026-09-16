package com.majorproject.backend.prediction;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class CompanyProfileCatalog {

    private final List<CompanyProfile> profiles = new ArrayList<>();

    public CompanyProfileCatalog() {
        profiles.add(CompanyProfile.builder()
                .name("TCS").aptitudeWeight(0.35).codingWeight(0.20).hrWeight(0.20).technicalWeight(0.15).resumeWeight(0.10)
                .aptitudeCutoff(50).codingCutoff(30)
                .commonTopics(List.of("Aptitude speed tests", "Email writing", "Java basics", "SQL queries", "DBMS normalization"))
                .aptitudePattern("TCS NQT — Numerical, Verbal, Reasoning (90 min)")
                .codingDifficulty("Easy to Medium").interviewStyle("HR-heavy, panel format").build());

        profiles.add(CompanyProfile.builder()
                .name("Infosys").aptitudeWeight(0.30).codingWeight(0.25).hrWeight(0.20).technicalWeight(0.15).resumeWeight(0.10)
                .aptitudeCutoff(55).codingCutoff(35)
                .commonTopics(List.of("Pseudo-code logic", "Database concepts", "OOP principles", "Java collections", "OS basics"))
                .aptitudePattern("InfyTQ — Reasoning + Pseudo code (3 hours)")
                .codingDifficulty("Medium").interviewStyle("Technical + HR in single round").build());

        profiles.add(CompanyProfile.builder()
                .name("Wipro").aptitudeWeight(0.35).codingWeight(0.15).hrWeight(0.25).technicalWeight(0.15).resumeWeight(0.10)
                .aptitudeCutoff(50).codingCutoff(25)
                .commonTopics(List.of("Verbal ability", "Logical reasoning", "Basic coding", "Essay writing", "Communication"))
                .aptitudePattern("Wipro NLTH — Aptitude + Essay (2 hours)")
                .codingDifficulty("Easy").interviewStyle("Group discussion + HR interview").build());

        profiles.add(CompanyProfile.builder()
                .name("Accenture").aptitudeWeight(0.30).codingWeight(0.20).hrWeight(0.25).technicalWeight(0.15).resumeWeight(0.10)
                .aptitudeCutoff(50).codingCutoff(30)
                .commonTopics(List.of("Cognitive assessment", "Coding in Python/Java", "Communication skills", "Problem solving", "Teamwork"))
                .aptitudePattern("Cognitive & Technical Assessment (90 min)")
                .codingDifficulty("Easy to Medium").interviewStyle("Virtual interview + communication round").build());

        profiles.add(CompanyProfile.builder()
                .name("Cognizant").aptitudeWeight(0.30).codingWeight(0.25).hrWeight(0.20).technicalWeight(0.15).resumeWeight(0.10)
                .aptitudeCutoff(55).codingCutoff(35)
                .commonTopics(List.of("Quantitative aptitude", "Coding (GenC patterns)", "SQL queries", "Agile methodology", "Cloud basics"))
                .aptitudePattern("GenC Assessment — Aptitude + Hands-on coding")
                .codingDifficulty("Medium").interviewStyle("Technical + HR combined").build());

        profiles.add(CompanyProfile.builder()
                .name("Capgemini").aptitudeWeight(0.30).codingWeight(0.20).hrWeight(0.25).technicalWeight(0.15).resumeWeight(0.10)
                .aptitudeCutoff(50).codingCutoff(30)
                .commonTopics(List.of("Game-based aptitude", "English proficiency", "Pseudo-code", "Behavioral assessment", "Technical MCQs"))
                .aptitudePattern("Game-based + Pseudo-code (no traditional aptitude)")
                .codingDifficulty("Easy to Medium").interviewStyle("Behavioral assessment + technical interview").build());

        profiles.add(CompanyProfile.builder()
                .name("Amazon").aptitudeWeight(0.10).codingWeight(0.40).hrWeight(0.15).technicalWeight(0.25).resumeWeight(0.10)
                .aptitudeCutoff(30).codingCutoff(60)
                .commonTopics(List.of("DSA (Arrays, Trees, Graphs)", "System Design basics", "Leadership Principles", "OOP", "Problem solving"))
                .aptitudePattern("Online Assessment — 2 coding questions (70 min)")
                .codingDifficulty("Medium to Hard").interviewStyle("Bar raiser + leadership principles focused").build());
    }

    public List<CompanyProfile> getAllProfiles() {
        return Collections.unmodifiableList(profiles);
    }
}

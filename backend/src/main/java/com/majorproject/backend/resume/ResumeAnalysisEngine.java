package com.majorproject.backend.resume;

import com.majorproject.backend.resume.RoleSkillCatalog.RoleBenchmark;
import com.majorproject.backend.resume.dto.SectionScoreDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ResumeAnalysisEngine {

    private final RoleSkillCatalog roleSkillCatalog;
    private final String geminiApiKey;
    private final String geminiModel;
    private final HttpClient httpClient;

    public ResumeAnalysisEngine(
            RoleSkillCatalog roleSkillCatalog,
            @Value("${ai.gemini.api-key:}") String geminiApiKey,
            @Value("${ai.gemini.model:gemini-1.5-flash}") String geminiModel
    ) {
        this.roleSkillCatalog = roleSkillCatalog;
        this.geminiApiKey = geminiApiKey != null ? geminiApiKey.trim() : "";
        this.geminiModel = geminiModel != null && !geminiModel.isBlank() ? geminiModel : "gemini-1.5-flash";
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(6))
                .build();
    }

    public static class AnalysisResult {
        public int atsScore;
        public int strengthScore;
        public int readinessScore;
        public SectionScoreDto sections;
        public List<String> skillsFound;
        public List<String> missingSkills;
        public List<String> criticalKeywords;
        public List<String> actionableSuggestions;
        public List<String> grammarSuggestions;
        public List<String> strengths;
        public String executiveSummary;
    }

    /**
     * Strictly verifies that the uploaded document contains structural pillars of a genuine resume.
     * Prevents false positives on random files (meeting circulars, textbook pages, invoices, forms).
     */
    public void validateResumeDocument(String text, String fileName) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("The uploaded file contains no readable text.");
        }

        String lower = text.toLowerCase();

        // 1. Check for non-resume document markers
        boolean hasNonResumeMarkers = lower.contains("parent-teacher") ||
                lower.contains("meeting minutes") ||
                lower.contains("agenda:") ||
                lower.contains("meeting notice") ||
                lower.contains("pta meeting") ||
                lower.contains("attendance sheet") ||
                lower.contains("invoice #") ||
                lower.contains("bill to:") ||
                lower.contains("receipt #") ||
                lower.contains("terms and conditions") ||
                lower.contains("homework assignment") ||
                lower.contains("question paper") ||
                lower.contains("syllabus copy");

        // 2. Core Resume Pillar A: Education / Academics
        boolean hasEducation = containsWord(lower, "education") ||
                containsWord(lower, "academic") ||
                containsWord(lower, "academics") ||
                containsWord(lower, "b.tech") ||
                containsWord(lower, "btech") ||
                containsWord(lower, "b.e") ||
                containsWord(lower, "bachelor") ||
                containsWord(lower, "master") ||
                containsWord(lower, "m.tech") ||
                containsWord(lower, "degree") ||
                containsWord(lower, "university") ||
                containsWord(lower, "college") ||
                containsWord(lower, "cgpa") ||
                containsWord(lower, "gpa") ||
                containsWord(lower, "coursework");

        // 3. Core Resume Pillar B: Experience / Projects / Work History
        boolean hasExperienceOrProjects = containsWord(lower, "experience") ||
                containsWord(lower, "projects") ||
                containsWord(lower, "project") ||
                containsWord(lower, "internship") ||
                containsWord(lower, "employment") ||
                containsWord(lower, "work history") ||
                containsWord(lower, "capstone") ||
                containsWord(lower, "contributions");

        // 4. Core Resume Pillar C: Technical or Professional Skills
        boolean hasSkills = containsWord(lower, "skills") ||
                containsWord(lower, "technical skills") ||
                containsWord(lower, "technologies") ||
                containsWord(lower, "programming languages") ||
                containsWord(lower, "proficiencies") ||
                containsWord(lower, "core competencies") ||
                containsWord(lower, "tools");

        // 5. Resume / CV identity headers
        boolean hasResumeIdentity = containsWord(lower, "resume") ||
                containsWord(lower, "curriculum vitae") ||
                containsWord(lower, "cv") ||
                containsWord(lower, "career objective") ||
                containsWord(lower, "professional summary") ||
                containsWord(lower, "summary of qualifications");

        int pillarCount = (hasEducation ? 1 : 0) + (hasExperienceOrProjects ? 1 : 0) + (hasSkills ? 1 : 0);

        String displayFile = (fileName != null && !fileName.isBlank()) ? fileName : "uploaded file";

        // Non-resume markers present without all 3 pillars
        if (hasNonResumeMarkers && pillarCount < 3) {
            throw new IllegalArgumentException(
                    "The file '" + displayFile + "' is not recognized as a resume or CV. " +
                    "It appears to be a general notice, meeting document, or circular. " +
                    "Please upload a valid resume containing Education, Projects, and Skills."
            );
        }

        // Must satisfy at least two core pillars or have an explicit resume identity + one pillar
        if (pillarCount < 2 && !hasResumeIdentity) {
            throw new IllegalArgumentException(
                    "The file '" + displayFile + "' does not appear to be a resume or CV. " +
                    "A valid resume must contain standard sections such as Education, Projects/Experience, and Skills. " +
                    "Please upload a valid student placement resume."
            );
        }
    }

    public AnalysisResult analyze(String text, String targetRole) {
        if (text == null) text = "";
        String lowerText = text.toLowerCase();

        RoleBenchmark benchmark = roleSkillCatalog.findBenchmark(targetRole);

        // 1. Calibrated Section & ATS Scoring
        int contactScore = evaluateContact(text, lowerText);
        int structureScore = evaluateStructure(lowerText);
        int readabilityScore = evaluateReadability(text);

        // Mathematically calibrated ATS Compatibility:
        // Structure = 50%, Contact = 25%, Readability/Formatting = 25%
        int rawAts = (int) Math.round((structureScore * 0.50) + (contactScore * 0.25) + (readabilityScore * 0.25));

        // Penalty if structure is broken: If core sections are missing, standard ATS cannot parse it
        if (structureScore == 0) {
            rawAts = 0;
        } else if (structureScore < 30) {
            rawAts = Math.min(rawAts, 25);
        }
        int atsScore = Math.max(0, Math.min(100, rawAts));

        // 2. Skills Analysis
        List<String> skillsFound = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();
        int primarySkillMatches = 0;

        // Check primary skills
        for (String skill : benchmark.getPrimarySkills()) {
            if (containsWord(lowerText, skill.toLowerCase())) {
                skillsFound.add(skill);
                primarySkillMatches++;
            } else {
                missingSkills.add(skill);
            }
        }

        // Check secondary skills
        int secondarySkillMatches = 0;
        for (String skill : benchmark.getSecondarySkills()) {
            if (containsWord(lowerText, skill.toLowerCase())) {
                skillsFound.add(skill);
                secondarySkillMatches++;
            }
        }

        int totalPrimary = benchmark.getPrimarySkills().size();
        int skillsScore = 0;
        if (totalPrimary > 0 && primarySkillMatches > 0) {
            skillsScore = (int) Math.round(((double) primarySkillMatches / totalPrimary) * 85);
            skillsScore += Math.min(15, secondarySkillMatches * 5); // secondary skill bonus
            skillsScore = Math.min(100, skillsScore);
        }

        // 3. Keywords Analysis
        List<String> missingKeywords = new ArrayList<>();
        for (String keyword : benchmark.getIndustryKeywords()) {
            if (!containsWord(lowerText, keyword.toLowerCase())) {
                missingKeywords.add(keyword);
            }
        }

        // 4. Strength & Impact Analysis
        int actionVerbScore = evaluateActionVerbs(lowerText);
        int impactScore = evaluateQuantifiableMetrics(lowerText);
        int projectDepthScore = evaluateProjectQuality(lowerText);

        // Strength: Action verbs (40%), Measurable impact (35%), Project depth (25%)
        int rawStrength = (int) Math.round((actionVerbScore * 0.40) + (impactScore * 0.35) + (projectDepthScore * 0.25));
        int strengthScore = Math.max(0, Math.min(100, rawStrength));

        // 5. Grammar & Style Checks
        List<String> grammarIssues = checkGrammarAndStyle(text, lowerText);

        // 6. Actionable Suggestions & Strengths
        List<String> suggestions = generateSuggestions(benchmark, missingSkills, missingKeywords, impactScore, actionVerbScore, contactScore, structureScore);
        List<String> strengthsList = generateStrengths(skillsFound, atsScore, strengthScore, contactScore, structureScore);

        // 7. Overall Readiness Composite Score
        int readinessScore = (int) Math.round((atsScore * 0.35) + (strengthScore * 0.35) + (skillsScore * 0.30));
        readinessScore = Math.max(0, Math.min(99, readinessScore));

        // 8. Executive Summary (Local or Gemini)
        String executiveSummary = generateExecutiveSummary(benchmark.getRoleTitle(), atsScore, readinessScore, skillsFound, missingSkills, text);

        AnalysisResult result = new AnalysisResult();
        result.atsScore = atsScore;
        result.strengthScore = strengthScore;
        result.readinessScore = readinessScore;
        result.sections = SectionScoreDto.builder()
                .contactScore(contactScore)
                .structureScore(structureScore)
                .skillsScore(skillsScore)
                .impactScore(impactScore)
                .build();
        result.skillsFound = skillsFound;
        result.missingSkills = missingSkills;
        result.criticalKeywords = missingKeywords;
        result.actionableSuggestions = suggestions;
        result.grammarSuggestions = grammarIssues;
        result.strengths = strengthsList;
        result.executiveSummary = executiveSummary;

        return result;
    }

    private int evaluateContact(String text, String lowerText) {
        int score = 0;
        // Email check: 30 pts
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        if (emailPattern.matcher(text).find()) {
            score += 30;
        }
        // Phone check: 30 pts
        Pattern phonePattern = Pattern.compile("(\\+?[0-9]{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}");
        if (phonePattern.matcher(text).find()) {
            score += 30;
        }
        // LinkedIn check: 20 pts
        if (lowerText.contains("linkedin.com") || lowerText.contains("linkedin")) {
            score += 20;
        }
        // GitHub check: 20 pts
        if (lowerText.contains("github.com") || lowerText.contains("github")) {
            score += 20;
        }
        return Math.min(100, score);
    }

    private int evaluateStructure(String lowerText) {
        int score = 0;
        // Education section (30 pts)
        if (lowerText.contains("education") || lowerText.contains("academic") || lowerText.contains("b.tech") || lowerText.contains("bachelor") || lowerText.contains("degree")) {
            score += 30;
        }
        // Experience or Projects (40 pts)
        if (lowerText.contains("experience") || lowerText.contains("projects") || lowerText.contains("internship") || lowerText.contains("employment") || lowerText.contains("capstone")) {
            score += 40;
        }
        // Skills section (20 pts)
        if (lowerText.contains("skills") || lowerText.contains("technologies") || lowerText.contains("technical proficiencies") || lowerText.contains("programming languages")) {
            score += 20;
        }
        // Summary or Objective (10 pts)
        if (lowerText.contains("summary") || lowerText.contains("objective") || lowerText.contains("about me") || lowerText.contains("profile")) {
            score += 10;
        }
        return Math.min(100, score);
    }

    private int evaluateReadability(String text) {
        String[] words = text.trim().split("\\s+");
        int count = words.length;
        if (count >= 250 && count <= 950) {
            return 100; // Ideal length for college/entry level resume
        } else if (count >= 150 && count <= 1300) {
            return 70;
        } else if (count > 50) {
            return 35;
        }
        return 0;
    }

    private int evaluateActionVerbs(String lowerText) {
        String[] strongVerbs = {
                "built", "developed", "engineered", "architected", "implemented", "designed",
                "optimized", "spearheaded", "automated", "deployed", "scaled", "created",
                "collaborated", "refactored", "integrated", "streamlined", "configured", "maintained",
                "accelerated", "reduced", "increased"
        };
        int found = 0;
        for (String verb : strongVerbs) {
            if (containsWord(lowerText, verb)) {
                found++;
            }
        }
        if (found >= 6) return 100;
        if (found >= 4) return 80;
        if (found >= 2) return 55;
        if (found == 1) return 30;
        return 0;
    }

    private int evaluateQuantifiableMetrics(String lowerText) {
        int matches = 0;
        Pattern metricPattern = Pattern.compile("(\\d+%)|(\\d+x)|(reduced\\s+by)|(increased\\s+by)|(\\$\\d+)|(\\d+\\+?\\s*(users|clients|requests|ms|seconds|minutes|stars))");
        Matcher m = metricPattern.matcher(lowerText);
        while (m.find()) {
            matches++;
        }
        if (matches >= 3) return 100;
        if (matches == 2) return 70;
        if (matches == 1) return 40;
        return 0;
    }

    private int evaluateProjectQuality(String lowerText) {
        boolean hasProjects = lowerText.contains("project") || lowerText.contains("experience") || lowerText.contains("internship");
        if (!hasProjects) return 0;

        int score = 30;
        if (lowerText.contains("github.com") || lowerText.contains("gitlab") || lowerText.contains("bitbucket")) {
            score += 35;
        }
        if (lowerText.contains("live demo") || lowerText.contains("deployed") || lowerText.contains("https://") || lowerText.contains("demo:")) {
            score += 35;
        }
        return Math.min(100, score);
    }

    private List<String> checkGrammarAndStyle(String text, String lowerText) {
        List<String> issues = new ArrayList<>();

        // 1. First-person pronoun check
        Pattern pronounPattern = Pattern.compile("\\b(i|my|me|we|our)\\b");
        Matcher pm = pronounPattern.matcher(lowerText);
        int pronounCount = 0;
        while (pm.find()) {
            pronounCount++;
        }
        if (pronounCount > 2) {
            issues.add("Resume contains first-person pronouns ('I', 'my', 'we'). Industry standard resumes use action-oriented phrases without personal pronouns.");
        }

        // 2. Passive/Weak Phrasing
        if (lowerText.contains("responsible for") || lowerText.contains("was responsible for")) {
            issues.add("Replace passive phrasing like 'responsible for' with assertive action verbs such as 'Spearheaded', 'Engineered', or 'Delivered'.");
        }
        if (lowerText.contains("helped with") || lowerText.contains("worked on")) {
            issues.add("Avoid vague verbs like 'worked on' or 'helped with'. Specifically state what you built, designed, or optimized.");
        }

        // 3. Email professional format check
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher em = emailPattern.matcher(text);
        if (!em.find()) {
            issues.add("No professional email address detected. Ensure your contact header includes your primary email.");
        }

        // 4. Length check
        String[] words = text.trim().split("\\s+");
        if (words.length < 200) {
            issues.add("Resume word count is low (" + words.length + " words). Expand project descriptions and add relevant coursework/certifications.");
        } else if (words.length > 1200) {
            issues.add("Resume appears overly lengthy (" + words.length + " words). Keep entry-level resumes concise within 1 to 2 pages.");
        }

        if (issues.isEmpty()) {
            issues.add("Formatting and phrasing follow professional standards with no obvious structural flags.");
        }

        return issues;
    }

    private List<String> generateSuggestions(
            RoleBenchmark benchmark,
            List<String> missingSkills,
            List<String> missingKeywords,
            int impactScore,
            int actionVerbScore,
            int contactScore,
            int structureScore
    ) {
        List<String> suggestions = new ArrayList<>();

        // Structure suggestion
        if (structureScore < 70) {
            suggestions.add("Add missing core resume sections: Standardize your headers into Education, Technical Projects, Work Experience, and Technical Skills.");
        }

        // Skill-gap suggestions
        if (!missingSkills.isEmpty()) {
            List<String> topMissing = missingSkills.subList(0, Math.min(3, missingSkills.size()));
            suggestions.add("Add target skills for " + benchmark.getRoleTitle() + ": Prioritize learning and listing " + String.join(", ", topMissing) + ".");
        }

        // Project recommendations
        if (benchmark.getRecommendedProjects() != null && !benchmark.getRecommendedProjects().isEmpty()) {
            suggestions.add("Project Recommendation: " + benchmark.getRecommendedProjects().get(0));
        }

        // Quantifiable metrics suggestion
        if (impactScore < 60) {
            suggestions.add("Quantify your achievements: Use the STAR/XYZ method (e.g. 'Optimized database queries by 35%, reducing average API latency from 240ms to 90ms').");
        }

        // Action verbs suggestion
        if (actionVerbScore < 60) {
            suggestions.add("Elevate bullet point verbs: Start project bullets with punchy impact verbs like 'Architected', 'Automated', 'Deployed', or 'Refactored'.");
        }

        // Contact info suggestion
        if (contactScore < 70) {
            suggestions.add("Enhance contact visibility: Make sure your GitHub profile, LinkedIn URL, phone, and professional email are clearly listed at the top.");
        }

        // ATS Keyword suggestion
        if (!missingKeywords.isEmpty()) {
            List<String> topKeywords = missingKeywords.subList(0, Math.min(4, missingKeywords.size()));
            suggestions.add("Optimize ATS keyword density: Weave in relevant industry terms such as " + String.join(", ", topKeywords) + " into your experience descriptions.");
        }

        return suggestions;
    }

    private List<String> generateStrengths(
            List<String> skillsFound,
            int atsScore,
            int strengthScore,
            int contactScore,
            int structureScore
    ) {
        List<String> strengths = new ArrayList<>();

        if (contactScore >= 70) {
            strengths.add("Complete and accessible contact header with professional contact channels.");
        }
        if (structureScore >= 70) {
            strengths.add("Clean section hierarchy (Education, Projects, Skills) easily parsed by standard ATS scanners.");
        }
        if (!skillsFound.isEmpty()) {
            strengths.add("Demonstrated competency in key technologies: " + String.join(", ", skillsFound.subList(0, Math.min(4, skillsFound.size()))) + ".");
        }
        if (strengthScore >= 65) {
            strengths.add("Good utilization of active voice and technical project descriptions.");
        }
        if (atsScore >= 75) {
            strengths.add("High ATS compatibility profile suitable for campus and enterprise hiring portals.");
        }
        if (strengths.isEmpty()) {
            strengths.add("The document requires foundational additions (Education, Projects, Skills) to pass standard ATS screening.");
        }
        return strengths;
    }

    private String generateExecutiveSummary(
            String roleTitle,
            int atsScore,
            int readinessScore,
            List<String> skillsFound,
            List<String> missingSkills,
            String fullText
    ) {
        // Try Gemini API if configured
        if (!geminiApiKey.isEmpty()) {
            try {
                String aiSummary = callGeminiApi(roleTitle, atsScore, readinessScore, skillsFound, missingSkills, fullText);
                if (aiSummary != null && !aiSummary.isBlank()) {
                    return aiSummary.trim();
                }
            } catch (Exception ignored) {
                // Graceful fallback to deterministic generator
            }
        }

        // Deterministic Generator
        StringBuilder sb = new StringBuilder();
        sb.append("Your resume currently scores ").append(atsScore).append("/100 on ATS formatting and demonstrates a ")
                .append(readinessScore).append("% overall placement readiness for the ").append(roleTitle).append(" profile. ");

        if (!skillsFound.isEmpty()) {
            sb.append("You demonstrate solid competence in ").append(String.join(", ", skillsFound.subList(0, Math.min(3, skillsFound.size())))).append(". ");
        }

        if (!missingSkills.isEmpty()) {
            sb.append("To stand out to recruiters, strengthen your profile by integrating ").append(String.join(", ", missingSkills.subList(0, Math.min(3, missingSkills.size()))))
                    .append(" into your project descriptions and quantifying measurable business/system impact.");
        } else {
            sb.append("Your core skill coverage aligns well with industry expectations. Focus on adding quantifiable metric achievements to your project bullets.");
        }

        return sb.toString();
    }

    private String callGeminiApi(
            String roleTitle,
            int atsScore,
            int readinessScore,
            List<String> skillsFound,
            List<String> missingSkills,
            String fullText
    ) {
        String snippet = fullText.length() > 1500 ? fullText.substring(0, 1500) : fullText;
        String prompt = "You are an expert technical recruiter and campus placement coach. " +
                "Evaluate this candidate's resume for the role: " + roleTitle + ".\n" +
                "ATS Score: " + atsScore + "/100. Readiness: " + readinessScore + "%.\n" +
                "Found skills: " + String.join(", ", skillsFound) + ".\n" +
                "Missing skills: " + String.join(", ", missingSkills) + ".\n" +
                "Resume excerpt: " + snippet + "\n\n" +
                "Write a concise 2-3 sentence executive evaluation highlighting their primary strength, key skill gap to close, and highest-priority resume fix. Keep it encouraging, professional, and direct.";

        String escapedPrompt = prompt.replace("\"", "\\\"").replace("\n", "\\n");
        String requestBody = "{\"contents\":[{\"parts\":[{\"text\":\"" + escapedPrompt + "\"}]}]}";

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel + ":generateContent?key=" + geminiApiKey;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(6))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body();
                int textIndex = body.indexOf("\"text\": \"");
                if (textIndex != -1) {
                    int start = textIndex + 9;
                    int end = body.indexOf("\"", start);
                    if (end > start) {
                        return body.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
                    }
                }
            }
        } catch (Exception e) {
            // Fallback
        }
        return null;
    }

    private boolean containsWord(String source, String target) {
        if (source == null || target == null) return false;
        String regex = "\\b" + Pattern.quote(target) + "\\b";
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source).find();
    }
}

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

    public AnalysisResult analyze(String text, String targetRole) {
        if (text == null) text = "";
        String lowerText = text.toLowerCase();

        RoleBenchmark benchmark = roleSkillCatalog.findBenchmark(targetRole);

        // 1. ATS Scoring & Section Analysis
        int contactScore = evaluateContact(text, lowerText);
        int structureScore = evaluateStructure(lowerText);
        int readabilityScore = evaluateReadability(text);
        int rawAts = contactScore + structureScore + readabilityScore;
        int atsScore = Math.max(10, Math.min(100, rawAts));

        // 2. Skills Analysis
        List<String> skillsFound = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();
        int skillMatchCount = 0;

        // Check primary skills
        for (String skill : benchmark.getPrimarySkills()) {
            if (containsWord(lowerText, skill.toLowerCase())) {
                skillsFound.add(skill);
                skillMatchCount++;
            } else {
                missingSkills.add(skill);
            }
        }

        // Check secondary skills
        for (String skill : benchmark.getSecondarySkills()) {
            if (containsWord(lowerText, skill.toLowerCase())) {
                skillsFound.add(skill);
            }
        }

        int skillsScore = benchmark.getPrimarySkills().isEmpty() ? 70 :
                (int) Math.round(((double) skillMatchCount / benchmark.getPrimarySkills().size()) * 100);
        skillsScore = Math.min(100, Math.max(15, skillsScore));

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
        int rawStrength = (int) Math.round((actionVerbScore * 0.35) + (impactScore * 0.40) + (projectDepthScore * 0.25));
        int strengthScore = Math.max(15, Math.min(100, rawStrength));

        // 5. Grammar & Style Checks
        List<String> grammarIssues = checkGrammarAndStyle(text, lowerText);

        // 6. Actionable Suggestions & Strengths
        List<String> suggestions = generateSuggestions(benchmark, missingSkills, missingKeywords, impactScore, actionVerbScore, contactScore, structureScore);
        List<String> strengthsList = generateStrengths(skillsFound, atsScore, strengthScore, contactScore, structureScore);

        // 7. Overall Readiness Composite Score
        int readinessScore = (int) Math.round((atsScore * 0.35) + (strengthScore * 0.35) + (skillsScore * 0.30));
        readinessScore = Math.max(20, Math.min(99, readinessScore));

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
        // Email check
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        if (emailPattern.matcher(text).find()) {
            score += 35;
        }
        // Phone check
        Pattern phonePattern = Pattern.compile("(\\+?[0-9]{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}");
        if (phonePattern.matcher(text).find()) {
            score += 35;
        }
        // LinkedIn check
        if (lowerText.contains("linkedin.com") || lowerText.contains("linkedin")) {
            score += 15;
        }
        // GitHub check
        if (lowerText.contains("github.com") || lowerText.contains("github")) {
            score += 15;
        }
        return Math.min(100, score);
    }

    private int evaluateStructure(String lowerText) {
        int score = 0;
        // Education section
        if (lowerText.contains("education") || lowerText.contains("academic") || lowerText.contains("b.tech") || lowerText.contains("bachelor")) {
            score += 30;
        }
        // Experience or Projects
        if (lowerText.contains("experience") || lowerText.contains("projects") || lowerText.contains("internship") || lowerText.contains("employment")) {
            score += 35;
        }
        // Skills section
        if (lowerText.contains("skills") || lowerText.contains("technologies") || lowerText.contains("technical proficiencies")) {
            score += 25;
        }
        // Summary or Objective
        if (lowerText.contains("summary") || lowerText.contains("objective") || lowerText.contains("about me") || lowerText.contains("profile")) {
            score += 10;
        }
        return Math.min(100, score);
    }

    private int evaluateReadability(String text) {
        String[] words = text.trim().split("\\s+");
        int count = words.length;
        if (count >= 300 && count <= 900) {
            return 20;
        } else if (count >= 200 && count <= 1200) {
            return 15;
        } else if (count > 0) {
            return 10;
        }
        return 0;
    }

    private int evaluateActionVerbs(String lowerText) {
        String[] strongVerbs = {
                "built", "developed", "engineered", "architected", "implemented", "designed",
                "optimized", "spearheaded", "automated", "deployed", "scaled", "created",
                "collaborated", "refactored", "integrated", "streamlined", "configured", "maintained"
        };
        int found = 0;
        for (String verb : strongVerbs) {
            if (containsWord(lowerText, verb)) {
                found++;
            }
        }
        if (found >= 7) return 95;
        if (found >= 5) return 82;
        if (found >= 3) return 65;
        if (found >= 1) return 45;
        return 25;
    }

    private int evaluateQuantifiableMetrics(String lowerText) {
        int matches = 0;
        // % signs or numbers
        Pattern metricPattern = Pattern.compile("(\\d+%)|(\\d+x)|(reduced\\s+by)|(increased\\s+by)|(\\$\\d+)|(\\d+\\+?\\s*(users|clients|requests|ms|seconds|minutes))");
        Matcher m = metricPattern.matcher(lowerText);
        while (m.find()) {
            matches++;
        }
        if (matches >= 4) return 95;
        if (matches >= 2) return 80;
        if (matches == 1) return 60;
        return 30;
    }

    private int evaluateProjectQuality(String lowerText) {
        int score = 50;
        if (lowerText.contains("github.com") || lowerText.contains("gitlab") || lowerText.contains("bitbucket")) {
            score += 25;
        }
        if (lowerText.contains("live demo") || lowerText.contains("deployed") || lowerText.contains("https://") || lowerText.contains("demo:")) {
            score += 25;
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
        if (words.length < 250) {
            issues.add("Resume word count is low (" + words.length + " words). Expand project descriptions and add relevant coursework/certifications.");
        } else if (words.length > 1100) {
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

        // Skill-gap suggestions
        if (!missingSkills.isEmpty()) {
            List<String> topMissing = missingSkills.subList(0, Math.min(3, missingSkills.size()));
            suggestions.add("Add target skills for " + benchmark.getRoleTitle() + ": Prioritize learning and listing " + String.join(", ", topMissing) + ".");
        }

        // Project recommendations
        if (benchmark.getRecommendedProjects() != null && !benchmark.getRecommendedProjects().isEmpty()) {
            suggestions.add("Project Idea: " + benchmark.getRecommendedProjects().get(0));
        }

        // Quantifiable metrics suggestion
        if (impactScore < 70) {
            suggestions.add("Quantify your achievements: Use the STAR/XYZ method (e.g. 'Optimized database queries by 35%, reducing average API latency from 240ms to 90ms').");
        }

        // Action verbs suggestion
        if (actionVerbScore < 70) {
            suggestions.add("Elevate bullet point verbs: Start project bullets with punchy impact verbs like 'Architected', 'Automated', 'Deployed', or 'Refactored'.");
        }

        // Contact info suggestion
        if (contactScore < 80) {
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

        if (contactScore >= 80) {
            strengths.add("Complete and accessible contact header with direct social/portfolio links.");
        }
        if (structureScore >= 80) {
            strengths.add("Clean section hierarchy (Education, Projects, Skills) easily parsed by standard ATS scanners.");
        }
        if (!skillsFound.isEmpty()) {
            strengths.add("Strong foundational tech stack demonstrating " + String.join(", ", skillsFound.subList(0, Math.min(4, skillsFound.size()))) + ".");
        }
        if (strengthScore >= 70) {
            strengths.add("Good utilization of active voice and technical project descriptions.");
        }
        if (atsScore >= 75) {
            strengths.add("High ATS compatibility profile suitable for campus and enterprise hiring portals.");
        }
        if (strengths.isEmpty()) {
            strengths.add("Good baseline resume structure with clear room for targeted optimization.");
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

        // Deterministic Fallback Generator
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
                // Simple parse of "text": "..."
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

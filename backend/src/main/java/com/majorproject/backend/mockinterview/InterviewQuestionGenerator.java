package com.majorproject.backend.mockinterview;

import com.majorproject.backend.hrtraining.HrCategory;
import com.majorproject.backend.hrtraining.HrPrompt;
import com.majorproject.backend.hrtraining.HrPromptCatalog;
import com.majorproject.backend.coding.CodingProblem;
import com.majorproject.backend.coding.CodingProblemBank;
import com.majorproject.backend.coding.CodingDifficulty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates interview questions based on round type and user skills.
 */
@Component
@RequiredArgsConstructor
public class InterviewQuestionGenerator {

    private final HrPromptCatalog hrPromptCatalog;
    private final CodingProblemBank codingProblemBank;

    // ─── Curated technical questions by skill ────────────────────────────
    private static final Map<String, List<String>> TECH_QUESTIONS_BY_SKILL = new LinkedHashMap<>();
    static {
        TECH_QUESTIONS_BY_SKILL.put("java", List.of(
                "What is the difference between JDK, JRE, and JVM?",
                "Explain the concept of Garbage Collection in Java.",
                "What are the differences between ArrayList and LinkedList?",
                "Explain the difference between abstract class and interface in Java.",
                "What is multithreading and how is it achieved in Java?"
        ));
        TECH_QUESTIONS_BY_SKILL.put("spring boot", List.of(
                "What is Dependency Injection in Spring?",
                "Explain the difference between @Component, @Service, and @Repository.",
                "How does Spring Boot auto-configuration work?",
                "What are Spring Boot Actuators?",
                "Explain @RestController vs @Controller."
        ));
        TECH_QUESTIONS_BY_SKILL.put("python", List.of(
                "What are Python decorators and how do they work?",
                "Explain the difference between list and tuple in Python.",
                "What is the GIL (Global Interpreter Lock)?",
                "How does memory management work in Python?",
                "Explain list comprehension vs generator expressions."
        ));
        TECH_QUESTIONS_BY_SKILL.put("sql", List.of(
                "What is the difference between INNER JOIN, LEFT JOIN, and RIGHT JOIN?",
                "Explain database normalization (1NF, 2NF, 3NF).",
                "What are indexes and how do they improve query performance?",
                "What is the difference between WHERE and HAVING clauses?",
                "Explain ACID properties in databases."
        ));
        TECH_QUESTIONS_BY_SKILL.put("react", List.of(
                "What are React hooks? Explain useState and useEffect.",
                "What is the Virtual DOM and how does it work?",
                "Explain the difference between props and state.",
                "What is React context and when should you use it?",
                "How does React reconciliation work?"
        ));
        TECH_QUESTIONS_BY_SKILL.put("dsa", List.of(
                "What is the time complexity of common sorting algorithms?",
                "Explain the difference between a stack and a queue.",
                "What is a balanced BST and why is it important?",
                "Explain dynamic programming vs greedy approach.",
                "What is the difference between BFS and DFS?"
        ));
        TECH_QUESTIONS_BY_SKILL.put("default", List.of(
                "What is Object-Oriented Programming? Explain its four pillars.",
                "What is the difference between a process and a thread?",
                "Explain the OSI model layers.",
                "What is REST API and what are HTTP methods?",
                "What is version control and why is Git important?"
        ));
    }

    public List<Map<String, String>> generateHrQuestions(int count) {
        List<HrPrompt> all = hrPromptCatalog.getAllPrompts();
        Collections.shuffle(all, new Random());
        return all.stream()
                .limit(count)
                .map(p -> Map.of(
                        "id", p.getId(),
                        "question", p.getQuestion(),
                        "category", p.getCategory().name(),
                        "recruiterIntent", p.getRecruiterIntent()
                ))
                .collect(Collectors.toList());
    }

    public List<Map<String, String>> generateTechnicalQuestions(List<String> skills, int count) {
        List<String> allQuestions = new ArrayList<>();
        Set<String> matched = new HashSet<>();

        if (skills != null) {
            for (String skill : skills) {
                String key = skill.toLowerCase().trim();
                for (Map.Entry<String, List<String>> entry : TECH_QUESTIONS_BY_SKILL.entrySet()) {
                    if (key.contains(entry.getKey()) || entry.getKey().contains(key)) {
                        allQuestions.addAll(entry.getValue());
                        matched.add(entry.getKey());
                    }
                }
            }
        }

        // Fill with default questions if not enough
        if (allQuestions.size() < count) {
            allQuestions.addAll(TECH_QUESTIONS_BY_SKILL.get("default"));
        }

        Collections.shuffle(allQuestions, new Random());
        List<String> selected = allQuestions.stream().distinct().limit(count).toList();

        List<Map<String, String>> result = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            result.add(Map.of(
                    "id", "tech-q-" + (i + 1),
                    "question", selected.get(i),
                    "type", "TECHNICAL"
            ));
        }
        return result;
    }

    public List<Map<String, String>> generateCodingQuestions(int count) {
        List<CodingProblem> mediums = codingProblemBank.getAllProblems().stream()
                .filter(p -> p.getDifficulty() == CodingDifficulty.EASY || p.getDifficulty() == CodingDifficulty.MEDIUM)
                .collect(Collectors.toList());
        Collections.shuffle(mediums, new Random());

        return mediums.stream()
                .limit(count)
                .map(p -> Map.of(
                        "id", p.getId(),
                        "question", p.getTitle() + ": " + p.getDescription(),
                        "title", p.getTitle(),
                        "category", p.getCategory().getDisplayName(),
                        "difficulty", p.getDifficulty().name()
                ))
                .collect(Collectors.toList());
    }
}

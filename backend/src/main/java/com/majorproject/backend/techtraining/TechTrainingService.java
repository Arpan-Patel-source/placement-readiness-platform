package com.majorproject.backend.techtraining;

import com.majorproject.backend.techtraining.dto.*;
import com.majorproject.backend.user.User;
import com.majorproject.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechTrainingService {

    private final TechQuestionBank questionBank;
    private final TechSubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    // ── Category summaries ──────────────────────────────────────────────
    public List<TechCategorySummaryDto> getCategorySummaries() {
        List<TechQuestion> all = questionBank.getAllQuestions();
        List<TechCategorySummaryDto> summaries = new ArrayList<>();

        for (TechCategory category : TechCategory.values()) {
            List<TechQuestion> catQuestions = all.stream()
                    .filter(q -> q.getCategory() == category)
                    .toList();

            Map<String, List<TechQuestion>> byTopic = catQuestions.stream()
                    .collect(Collectors.groupingBy(TechQuestion::getTopic));

            List<TechCategorySummaryDto.TopicSummary> topicSummaries = byTopic.entrySet().stream()
                    .map(e -> TechCategorySummaryDto.TopicSummary.builder()
                            .topicId(e.getKey().toLowerCase().replace(" ", "-").replace("&", "and"))
                            .topicName(e.getKey())
                            .questionCount(e.getValue().size())
                            .keyConcept(getKeyConceptForTopic(e.getKey()))
                            .build())
                    .sorted(Comparator.comparing(TechCategorySummaryDto.TopicSummary::getTopicName))
                    .toList();

            summaries.add(TechCategorySummaryDto.builder()
                    .category(category)
                    .title(category.getDisplayName())
                    .description(category.getDescription())
                    .totalQuestions(catQuestions.size())
                    .topics(topicSummaries)
                    .build());
        }

        return summaries;
    }

    // ── Filtered questions ──────────────────────────────────────────────
    public List<TechQuestionDto> getQuestions(TechCategory category, String topic, TechDifficulty difficulty, Integer limit) {
        List<TechQuestion> stream = questionBank.getAllQuestions();

        if (category != null) {
            stream = stream.stream().filter(q -> q.getCategory() == category).toList();
        }

        if (topic != null && !topic.trim().isEmpty()) {
            String cleanTopic = topic.trim().toLowerCase();
            stream = stream.stream()
                    .filter(q -> q.getTopic().toLowerCase().contains(cleanTopic)
                            || q.getTopic().toLowerCase().replace(" ", "-").replace("&", "and").equals(cleanTopic))
                    .toList();
        }

        if (difficulty != null) {
            stream = stream.stream().filter(q -> q.getDifficulty() == difficulty).toList();
        }

        if (limit != null && limit > 0 && limit < stream.size()) {
            stream = stream.subList(0, limit);
        }

        return stream.stream().map(this::toDto).toList();
    }

    // ── Mock test generation ────────────────────────────────────────────
    public List<TechQuestionDto> generateMockTest(TechCategory category, int count) {
        List<TechQuestion> pool = new ArrayList<>(questionBank.getAllQuestions());
        if (category != null) {
            pool = pool.stream().filter(q -> q.getCategory() == category).collect(Collectors.toList());
        }

        Collections.shuffle(pool, new Random());
        int targetSize = Math.min(count <= 0 ? 10 : count, pool.size());
        return pool.subList(0, targetSize).stream().map(this::toDto).toList();
    }

    // ── Evaluate submitted test ─────────────────────────────────────────
    @Transactional
    public TechResultResponse evaluateTest(String userEmail, TechSubmitRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        List<TechQuestion> all = questionBank.getAllQuestions();
        Map<String, TechQuestion> questionMap = all.stream()
                .collect(Collectors.toMap(TechQuestion::getId, q -> q));

        int correctCount = 0;
        int incorrectCount = 0;
        int unattemptedCount = 0;

        Map<String, int[]> topicScores = new HashMap<>(); // topic -> [correct, total]
        List<TechResultResponse.QuestionReview> reviews = new ArrayList<>();

        if (request.getAnswers() != null) {
            for (TechSubmitRequest.AnswerSubmission sub : request.getAnswers()) {
                TechQuestion q = questionMap.get(sub.getQuestionId());
                if (q == null) continue;

                topicScores.putIfAbsent(q.getTopic(), new int[]{0, 0});
                topicScores.get(q.getTopic())[1]++;

                boolean attempted = sub.getSelectedOptionIndex() != null;
                boolean correct = attempted && sub.getSelectedOptionIndex() == q.getCorrectOptionIndex();

                if (!attempted) {
                    unattemptedCount++;
                } else if (correct) {
                    correctCount++;
                    topicScores.get(q.getTopic())[0]++;
                } else {
                    incorrectCount++;
                }

                reviews.add(TechResultResponse.QuestionReview.builder()
                        .questionId(q.getId())
                        .topic(q.getTopic())
                        .question(q.getQuestion())
                        .options(q.getOptions())
                        .selectedOptionIndex(sub.getSelectedOptionIndex())
                        .correctOptionIndex(q.getCorrectOptionIndex())
                        .isCorrect(correct)
                        .isAttempted(attempted)
                        .explanation(q.getExplanation())
                        .conceptTip(q.getConceptTip())
                        .build());
            }
        }

        int totalQuestions = reviews.size();
        double scorePercentage = totalQuestions > 0 ? ((double) correctCount / totalQuestions) * 100.0 : 0.0;
        scorePercentage = Math.round(scorePercentage * 10.0) / 10.0;

        List<TechResultResponse.TopicBreakdown> breakdowns = topicScores.entrySet().stream()
                .map(e -> {
                    int corr = e.getValue()[0];
                    int tot = e.getValue()[1];
                    double acc = tot > 0 ? ((double) corr / tot) * 100.0 : 0.0;
                    return TechResultResponse.TopicBreakdown.builder()
                            .topic(e.getKey())
                            .total(tot)
                            .correct(corr)
                            .accuracy(Math.round(acc * 10.0) / 10.0)
                            .build();
                })
                .sorted((a, b) -> Double.compare(b.getAccuracy(), a.getAccuracy()))
                .toList();

        String verdict;
        String feedback;
        if (scorePercentage >= 80.0) {
            verdict = "Interview Ready — Strong Technical Foundation";
            feedback = "Excellent command of core CS fundamentals! You are well-prepared for technical screening rounds at top product and service companies.";
        } else if (scorePercentage >= 60.0) {
            verdict = "Competitive — Room for Improvement";
            feedback = "Good baseline knowledge. Focus on strengthening your weak areas in the topic breakdown below and practice more questions in those topics.";
        } else {
            verdict = "Foundation Building Required";
            feedback = "Review the concept cheatsheets and explanations carefully. Focus on understanding core principles before attempting timed tests.";
        }

        // Persist submission
        String testId = request.getTestId() != null ? request.getTestId() : UUID.randomUUID().toString();

        TechCategory cat = null;
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            try {
                cat = TechCategory.valueOf(request.getCategory());
            } catch (IllegalArgumentException ignored) {}
        }

        TechSubmission submission = TechSubmission.builder()
                .userId(user.getId())
                .testId(testId)
                .category(cat)
                .totalQuestions(totalQuestions)
                .correctCount(correctCount)
                .incorrectCount(incorrectCount)
                .unattemptedCount(unattemptedCount)
                .scorePercentage(scorePercentage)
                .totalTimeSpentSeconds(request.getTotalTimeSpentSeconds())
                .verdict(verdict)
                .build();

        submissionRepository.save(submission);

        return TechResultResponse.builder()
                .testId(testId)
                .totalQuestions(totalQuestions)
                .correctCount(correctCount)
                .incorrectCount(incorrectCount)
                .unattemptedCount(unattemptedCount)
                .scorePercentage(scorePercentage)
                .totalTimeSpentSeconds(request.getTotalTimeSpentSeconds())
                .performanceVerdict(verdict)
                .performanceFeedback(feedback)
                .topicBreakdowns(breakdowns)
                .questionReviews(reviews)
                .build();
    }

    // ── Cheatsheet ──────────────────────────────────────────────────────
    public List<TechFormulaCardDto> getConceptCheatsheet() {
        return questionBank.getConceptCheatsheet();
    }

    // ── User history ────────────────────────────────────────────────────
    public List<TechHistoryItemDto> getUserHistory(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        List<TechSubmission> submissions = submissionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        return submissions.stream().map(sub -> TechHistoryItemDto.builder()
                .id(sub.getId().toString())
                .testId(sub.getTestId())
                .category(sub.getCategory())
                .categoryTitle(sub.getCategory() != null ? sub.getCategory().getDisplayName() : "Mixed")
                .totalQuestions(sub.getTotalQuestions())
                .correctCount(sub.getCorrectCount())
                .scorePercentage(sub.getScorePercentage())
                .verdict(sub.getVerdict())
                .createdAt(sub.getCreatedAt())
                .build()).collect(Collectors.toList());
    }

    // ── Helpers ─────────────────────────────────────────────────────────
    private TechQuestionDto toDto(TechQuestion q) {
        return TechQuestionDto.builder()
                .id(q.getId())
                .category(q.getCategory())
                .topic(q.getTopic())
                .difficulty(q.getDifficulty())
                .question(q.getQuestion())
                .options(q.getOptions())
                .correctOptionIndex(q.getCorrectOptionIndex())
                .explanation(q.getExplanation())
                .conceptTip(q.getConceptTip())
                .companiesAsked(q.getCompaniesAsked())
                .build();
    }

    private String getKeyConceptForTopic(String topic) {
        return switch (topic.toLowerCase()) {
            case "encapsulation" -> "Data hiding via access modifiers and getter/setter methods.";
            case "inheritance" -> "Code reuse through class hierarchies and method overriding.";
            case "polymorphism" -> "Compile-time (overloading) and runtime (overriding) polymorphism.";
            case "abstraction" -> "Hiding implementation details using abstract classes and interfaces.";
            case "interfaces" -> "Contracts for behavior, default methods (Java 8+), functional interfaces.";
            case "constructors" -> "Object initialization, constructor chaining, overloading.";
            case "design principles" -> "SOLID principles, composition over inheritance, design patterns.";
            case "normalization" -> "1NF, 2NF, 3NF, BCNF — reducing redundancy and anomalies.";
            case "sql" -> "DDL, DML, joins, subqueries, aggregations, window functions.";
            case "transactions" -> "ACID properties, isolation levels, concurrency control, deadlocks.";
            case "indexing" -> "B+ trees, hash indexes, clustered vs non-clustered, query optimization.";
            case "joins" -> "INNER, LEFT, RIGHT, FULL, CROSS joins and their use cases.";
            case "keys" -> "Primary, foreign, candidate, composite, and super keys.";
            case "er modeling" -> "Entity-relationship diagrams, cardinality, weak entities.";
            case "process management" -> "Process states, PCB, fork(), context switching.";
            case "scheduling" -> "FCFS, SJF, Round Robin, Priority, MLFQ algorithms.";
            case "deadlocks" -> "Coffman conditions, prevention, avoidance (Banker's), detection.";
            case "memory management" -> "Paging, segmentation, virtual memory, page replacement.";
            case "synchronization" -> "Mutex, semaphores, monitors, producer-consumer, readers-writers.";
            case "file systems" -> "Inodes, directory structure, allocation methods, journaling.";
            case "osi model" -> "7-layer reference model for network communication.";
            case "tcp vs udp" -> "Reliable ordered (TCP) vs fast unreliable (UDP) transport.";
            case "ip addressing" -> "IPv4/IPv6, subnetting, CIDR, NAT, DHCP.";
            case "dns" -> "Domain name resolution, hierarchy, caching, record types.";
            case "http" -> "Request/response cycle, status codes, headers, HTTPS/TLS.";
            case "routing" -> "Distance-vector vs link-state, OSPF, BGP, RIP.";
            case "network security" -> "Encryption, firewalls, MITM, DDoS, TLS/SSL.";
            case "protocols" -> "ARP, DHCP, ICMP, SMTP, FTP — purpose and operation.";
            case "arrays" -> "Contiguous memory, O(1) access, O(n) insertion/deletion.";
            case "linked lists" -> "Node-pointer structure, O(1) insert/delete at known position.";
            case "trees" -> "BST, AVL, Red-Black, traversals, height-balanced properties.";
            case "sorting" -> "QuickSort, MergeSort, HeapSort — stability, complexity, in-place.";
            case "stacks" -> "LIFO operations, expression evaluation, DFS, backtracking.";
            case "graphs" -> "BFS, DFS, Dijkstra, topological sort, MST algorithms.";
            case "hashing" -> "Hash functions, collision resolution, load factor, rehashing.";
            case "dynamic programming" -> "Optimal substructure, overlapping subproblems, memoization vs tabulation.";
            case "heaps" -> "Max-heap, min-heap, priority queues, heap sort.";
            case "searching" -> "Linear search, binary search, interpolation search.";
            case "html/css" -> "Box model, Flexbox, Grid, selectors, specificity.";
            case "javascript" -> "Closures, event loop, hoisting, prototypes, async/await.";
            case "rest apis" -> "HTTP methods, CORS, status codes, idempotency, HATEOAS.";
            case "web security" -> "XSS, CSRF, SQL injection, CORS, CSP, HTTPS.";
            case "cookies & sessions" -> "Client vs server storage, HttpOnly, SameSite, JWT.";
            default -> "Core foundational concepts and best practices.";
        };
    }
}

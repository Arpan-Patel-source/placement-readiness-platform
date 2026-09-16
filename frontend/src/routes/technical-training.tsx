import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import {
  Code2,
  Clock,
  CheckCircle2,
  XCircle,
  AlertCircle,
  BookOpen,
  Sparkles,
  ChevronRight,
  ChevronLeft,
  Flag,
  RotateCcw,
  FileText,
  Award,
  BarChart3,
  Target,
  ArrowRight,
  Filter,
  Check,
  Zap,
  HelpCircle,
  History,
  Database,
  Cpu,
  Globe,
  Layers,
  Network,
} from "lucide-react";
import {
  api,
  TechCategorySummary,
  TechQuestion,
  TechResultResponse,
  TechFormulaCard,
  TechHistoryItem,
  TechCategoryType,
} from "../lib/api";
import { SiteHeader } from "../components/SiteHeader";

export const Route = createFileRoute("/technical-training")({
  head: () => ({
    meta: [
      { title: "Technical Training — PlacementAI" },
      {
        name: "description",
        content:
          "Practice OOP, DBMS, OS, Computer Networks, DSA and Web Technologies MCQs for placement technical rounds.",
      },
    ],
  }),
  component: TechnicalTrainingModule,
});

type Mode = "practice" | "mock" | "cheatsheet" | "history";

const CATEGORY_ICONS: Record<string, typeof Code2> = {
  OOP: Layers,
  DBMS: Database,
  OPERATING_SYSTEMS: Cpu,
  COMPUTER_NETWORKS: Network,
  DSA: Code2,
  WEB_TECHNOLOGIES: Globe,
};

function TechnicalTrainingModule() {
  const [mode, setMode] = useState<Mode>("practice");
  const [categories, setCategories] = useState<TechCategorySummary[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>("OOP");
  const [selectedTopic, setSelectedTopic] = useState<string>("");
  const [selectedDifficulty, setSelectedDifficulty] = useState<string>("");
  const [questions, setQuestions] = useState<TechQuestion[]>([]);
  const [loading, setLoading] = useState(true);

  // Practice state
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedOption, setSelectedOption] = useState<number | null>(null);
  const [showExplanation, setShowExplanation] = useState(false);

  // Mock test state
  const [mockQuestions, setMockQuestions] = useState<TechQuestion[]>([]);
  const [mockAnswers, setMockAnswers] = useState<Record<string, number | null>>({});
  const [flaggedQuestions, setFlaggedQuestions] = useState<Set<string>>(new Set());
  const [mockCurrentIndex, setMockCurrentIndex] = useState(0);
  const [timeLeft, setTimeLeft] = useState(600);
  const [testActive, setTestActive] = useState(false);
  const [submittingTest, setSubmittingTest] = useState(false);
  const [testResult, setTestResult] = useState<TechResultResponse | null>(null);

  // Cheatsheet state
  const [formulas, setFormulas] = useState<TechFormulaCard[]>([]);
  const [formulaFilter, setFormulaFilter] = useState("All");

  // History state
  const [history, setHistory] = useState<TechHistoryItem[]>([]);
  const [loadingHistory, setLoadingHistory] = useState(false);

  // Load category summaries & formulas on mount
const FALLBACK_TECH_CATEGORIES: TechCategorySummary[] = [
  { category: "OOP", displayName: "Object-Oriented Programming", totalQuestions: 15, topics: [{ topic: "Encapsulation", questionCount: 5 }, { topic: "Inheritance & Polymorphism", questionCount: 5 }, { topic: "Interfaces & Abstraction", questionCount: 5 }] },
  { category: "DBMS", displayName: "Database Management Systems", totalQuestions: 15, topics: [{ topic: "Normalization", questionCount: 5 }, { topic: "ACID & Transactions", questionCount: 5 }, { topic: "Indexing & Joins", questionCount: 5 }] },
  { category: "OPERATING_SYSTEMS", displayName: "Operating Systems", totalQuestions: 12, topics: [{ topic: "Deadlocks", questionCount: 4 }, { topic: "Process Management", questionCount: 4 }, { topic: "Virtual Memory", questionCount: 4 }] },
  { category: "COMPUTER_NETWORKS", displayName: "Computer Networks", totalQuestions: 12, topics: [{ topic: "OSI & TCP/IP", questionCount: 4 }, { topic: "Routing & Protocols", questionCount: 4 }, { topic: "DNS & HTTP", questionCount: 4 }] },
  { category: "DSA", displayName: "Data Structures & Algorithms", totalQuestions: 15, topics: [{ topic: "Trees & BST", questionCount: 5 }, { topic: "Graph Algorithms", questionCount: 5 }, { topic: "Complexity Analysis", questionCount: 5 }] },
  { category: "WEB_TECHNOLOGIES", displayName: "Web Technologies", totalQuestions: 12, topics: [{ topic: "REST APIs", questionCount: 4 }, { topic: "Browser Storage & Cookies", questionCount: 4 }, { topic: "Authentication & Security", questionCount: 4 }] },
];

const FALLBACK_TECH_QUESTIONS: Record<string, TechQuestion[]> = {
  OOP: [
    {
      id: "OOP-001",
      category: "OOP",
      topic: "Encapsulation",
      difficulty: "EASY",
      question: "Which OOP concept is defined as the bundling of data and the methods that operate on that data into a single unit, while restricting direct access to some components?",
      options: ["Polymorphism", "Encapsulation", "Inheritance", "Abstraction"],
      correctOptionIndex: 1,
      explanation: "Encapsulation prevents unauthorized access to internal representation by hiding object state behind getter and setter methods.",
      companiesAsked: ["TCS", "Infosys", "Wipro"],
    },
    {
      id: "OOP-002",
      category: "OOP",
      topic: "Interfaces & Abstraction",
      difficulty: "MEDIUM",
      question: "What is the primary architectural difference between an interface and an abstract class in modern Java (Java 8+)?",
      options: [
        "Interfaces cannot contain method bodies",
        "Abstract classes can declare instance state (fields), while interfaces can only have static final constants",
        "A class can inherit multiple abstract classes",
        "Interfaces cannot be used as reference types"
      ],
      correctOptionIndex: 1,
      explanation: "Even though Java 8 introduced default and static methods in interfaces, interfaces still cannot maintain instance state, whereas abstract classes can.",
      companiesAsked: ["Amazon", "Microsoft", "Goldman Sachs"],
    },
  ],
  DBMS: [
    {
      id: "DBMS-001",
      category: "DBMS",
      topic: "Normalization",
      difficulty: "EASY",
      question: "Which Normal Form strictly requires that all non-key attributes are fully functionally dependent on the entire primary key (eliminating partial dependencies)?",
      options: ["First Normal Form (1NF)", "Second Normal Form (2NF)", "Third Normal Form (3NF)", "Boyce-Codd Normal Form (BCNF)"],
      correctOptionIndex: 1,
      explanation: "2NF requires 1NF compliance and mandates that every non-prime attribute depends on the whole primary key, not a proper subset of it.",
      companiesAsked: ["Cognizant", "Accenture", "TCS"],
    },
    {
      id: "DBMS-002",
      category: "DBMS",
      topic: "ACID & Transactions",
      difficulty: "MEDIUM",
      question: "Which ACID property guarantees that a committed transaction will remain saved and persistent even in the event of an abrupt system power loss or database crash?",
      options: ["Atomicity", "Consistency", "Isolation", "Durability"],
      correctOptionIndex: 3,
      explanation: "Durability guarantees that once a transaction has committed, its changes are written to non-volatile storage (via Write-Ahead Logging) and will not be lost.",
      companiesAsked: ["Amazon", "Oracle", "Flipkart"],
    },
  ],
  OPERATING_SYSTEMS: [
    {
      id: "OS-001",
      category: "OPERATING_SYSTEMS",
      topic: "Deadlocks",
      difficulty: "EASY",
      question: "Which of the following is NOT one of Coffman's four necessary conditions for a deadlock to occur?",
      options: ["Mutual Exclusion", "Hold and Wait", "Preemption of Resources", "Circular Wait"],
      correctOptionIndex: 2,
      explanation: "The condition is NO PREEMPTION (resources cannot be forcibly reclaimed). If preemption is allowed, deadlocks cannot persist.",
      companiesAsked: ["TCS Digital", "Infosys", "Cisco"],
    },
    {
      id: "OS-002",
      category: "OPERATING_SYSTEMS",
      topic: "Virtual Memory",
      difficulty: "MEDIUM",
      question: "What hardware component is responsible for translating virtual memory addresses to physical RAM addresses at runtime?",
      options: ["Arithmetic Logic Unit (ALU)", "Memory Management Unit (MMU)", "DMA Controller", "Cache Controller"],
      correctOptionIndex: 1,
      explanation: "The MMU hardware, working alongside the TLB (Translation Lookaside Buffer), translates virtual page addresses to physical page frames.",
      companiesAsked: ["Intel", "Qualcomm", "Microsoft"],
    },
  ],
  COMPUTER_NETWORKS: [
    {
      id: "CN-001",
      category: "COMPUTER_NETWORKS",
      topic: "OSI & TCP/IP",
      difficulty: "EASY",
      question: "At which layer of the OSI model does the TCP (Transmission Control Protocol) operate?",
      options: ["Network Layer", "Transport Layer", "Session Layer", "Data Link Layer"],
      correctOptionIndex: 1,
      explanation: "TCP and UDP are Transport Layer (Layer 4) protocols providing end-to-end communication and port multiplexing.",
      companiesAsked: ["Wipro", "TCS", "Accenture"],
    },
  ],
  DSA: [
    {
      id: "DSA-001",
      category: "DSA",
      topic: "Trees & BST",
      difficulty: "EASY",
      question: "What is the worst-case time complexity of searching for an element in a balanced Binary Search Tree containing n nodes?",
      options: ["O(1)", "O(log n)", "O(n)", "O(n log n)"],
      correctOptionIndex: 1,
      explanation: "In balanced trees like Red-Black or AVL trees, the height is guaranteed to be O(log n), so search is O(log n).",
      companiesAsked: ["Amazon", "Google", "Microsoft"],
    },
  ],
  WEB_TECHNOLOGIES: [
    {
      id: "WEB-001",
      category: "WEB_TECHNOLOGIES",
      topic: "REST APIs",
      difficulty: "EASY",
      question: "Which HTTP request method is considered idempotent and used for replacing an entire resource at a specified URI?",
      options: ["POST", "PUT", "PATCH", "CONNECT"],
      correctOptionIndex: 1,
      explanation: "PUT is idempotent: making multiple identical PUT requests produces the exact same server state as a single request.",
      companiesAsked: ["Adobe", "Salesforce", "Infosys"],
    },
  ],
};

  useEffect(() => {
    async function loadInitial() {
      try {
        setLoading(true);
        const [cats, forms] = await Promise.all([
          api.getTechCategories().catch(() => []),
          api.getTechCheatsheet().catch(() => []),
        ]);
        setCategories(cats && cats.length > 0 ? cats : FALLBACK_TECH_CATEGORIES);
        setFormulas(forms);
      } catch (err) {
        console.warn("Using fallback tech categories:", err);
        setCategories(FALLBACK_TECH_CATEGORIES);
      } finally {
        setLoading(false);
      }
    }
    loadInitial();
  }, []);

  // Fetch questions whenever category, topic, or difficulty changes in practice mode
  useEffect(() => {
    if (mode !== "practice") return;
    async function fetchQuestions() {
      try {
        setLoading(true);
        const qList = await api.getTechQuestions({
          category: selectedCategory,
          ...(selectedTopic ? { topic: selectedTopic } : {}),
          ...(selectedDifficulty ? { difficulty: selectedDifficulty } : {}),
        });
        if (qList && qList.length > 0) {
          setQuestions(qList);
        } else {
          setQuestions(FALLBACK_TECH_QUESTIONS[selectedCategory] || FALLBACK_TECH_QUESTIONS.OOP);
        }
        setCurrentIndex(0);
        setSelectedOption(null);
        setShowExplanation(false);
      } catch (err) {
        console.warn("Using fallback tech questions:", err);
        setQuestions(FALLBACK_TECH_QUESTIONS[selectedCategory] || FALLBACK_TECH_QUESTIONS.OOP);
        setCurrentIndex(0);
        setSelectedOption(null);
        setShowExplanation(false);
      } finally {
        setLoading(false);
      }
    }
    fetchQuestions();
  }, [selectedCategory, selectedTopic, selectedDifficulty, mode]);

  // Timer for mock test
  useEffect(() => {
    let interval: any = null;
    if (testActive && timeLeft > 0 && !testResult) {
      interval = setInterval(() => {
        setTimeLeft((prev) => {
          if (prev <= 1) {
            handleMockSubmit();
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    }
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [testActive, timeLeft, testResult]);

  // Load history when switching to history tab
  useEffect(() => {
    if (mode !== "history") return;
    async function loadHistory() {
      try {
        setLoadingHistory(true);
        const hist = await api.getTechHistory();
        setHistory(hist);
      } catch (err) {
        console.error("Failed to load history:", err);
      } finally {
        setLoadingHistory(false);
      }
    }
    loadHistory();
  }, [mode]);

  const startMockTest = async () => {
    try {
      setLoading(true);
      const qs = await api.getTechMockTest(selectedCategory, 10);
      setMockQuestions(qs);
      setMockAnswers({});
      setFlaggedQuestions(new Set());
      setMockCurrentIndex(0);
      setTimeLeft(600);
      setTestActive(true);
      setTestResult(null);
    } catch (err) {
      console.error("Failed to start mock test:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleMockSubmit = async () => {
    if (submittingTest) return;
    setSubmittingTest(true);
    setTestActive(false);

    try {
      const answers = mockQuestions.map((q) => ({
        questionId: q.id,
        selectedOptionIndex: mockAnswers[q.id] ?? null,
        timeSpentSeconds: 0,
      }));

      const result = await api.submitTechTest({
        category: selectedCategory,
        totalTimeSpentSeconds: 600 - timeLeft,
        answers,
      });

      setTestResult(result);
    } catch (err) {
      console.error("Failed to submit test:", err);
    } finally {
      setSubmittingTest(false);
    }
  };

  const currentQuestion = questions[currentIndex];
  const mockQuestion = mockQuestions[mockCurrentIndex];
  const currentCategory = categories.find((c) => c.category === selectedCategory);

  const formatTime = (s: number) =>
    `${String(Math.floor(s / 60)).padStart(2, "0")}:${String(s % 60).padStart(2, "0")}`;

  // ────────────────────────────────────────────────────────────────────
  // RENDER
  // ────────────────────────────────────────────────────────────────────
  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />

      <div className="mx-auto max-w-7xl px-5 py-8">
        {/* ── Header ─────────────────────────────────────────────── */}
        <div className="mb-8 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <Link
              to="/dashboard"
              className="mb-2 inline-flex items-center gap-1 text-xs font-medium text-muted-foreground transition-colors hover:text-coral"
            >
              ← Back to Dashboard
            </Link>
            <h1 className="font-display text-3xl font-extrabold tracking-tight text-ink">
              Technical Training
            </h1>
            <p className="mt-1 text-sm text-muted-foreground">
              Master core CS subjects for placement technical screening rounds
            </p>
          </div>
          <div className="flex gap-2">
            {(["practice", "mock", "cheatsheet", "history"] as Mode[]).map((m) => (
              <button
                key={m}
                onClick={() => {
                  setMode(m);
                  if (m === "mock") {
                    setTestActive(false);
                    setTestResult(null);
                  }
                }}
                className={`rounded-full px-4 py-2 text-sm font-semibold transition-all ${
                  mode === m
                    ? "bg-coral text-white shadow-sm"
                    : "bg-muted text-ink/70 hover:bg-muted/80"
                }`}
              >
                {m === "practice"
                  ? "Practice"
                  : m === "mock"
                    ? "Mock Test"
                    : m === "cheatsheet"
                      ? "Cheatsheet"
                      : "History"}
              </button>
            ))}
          </div>
        </div>

        {/* ── Category Selector ──────────────────────────────────── */}
        {(mode === "practice" || mode === "mock") && (
          <div className="mb-6 flex flex-wrap gap-2">
            {categories.map((cat) => {
              const Icon = CATEGORY_ICONS[cat.category] || Code2;
              return (
                <button
                  key={cat.category}
                  onClick={() => {
                    setSelectedCategory(cat.category);
                    setSelectedTopic("");
                    setSelectedDifficulty("");
                  }}
                  className={`inline-flex items-center gap-2 rounded-xl border px-4 py-2.5 text-sm font-medium transition-all ${
                    selectedCategory === cat.category
                      ? "border-coral bg-coral/10 text-coral shadow-sm"
                      : "border-border bg-card text-ink/70 hover:border-coral/40"
                  }`}
                >
                  <Icon className="size-4" />
                  {cat.title}
                  <span className="ml-1 rounded-full bg-muted px-2 py-0.5 text-xs font-semibold text-muted-foreground">
                    {cat.totalQuestions}
                  </span>
                </button>
              );
            })}
          </div>
        )}

        {/* ═══════════════════════════════════════════════════════════════
            PRACTICE MODE
           ═══════════════════════════════════════════════════════════════ */}
        {mode === "practice" && (
          <div className="grid gap-6 lg:grid-cols-[280px_1fr]">
            {/* Topic sidebar */}
            <div className="rounded-2xl border border-border bg-card p-4">
              <h3 className="mb-3 text-sm font-bold text-ink">
                Topics in {currentCategory?.title}
              </h3>
              <div className="mb-4 flex flex-col gap-1">
                <button
                  onClick={() => setSelectedTopic("")}
                  className={`rounded-lg px-3 py-2 text-left text-sm transition-colors ${
                    !selectedTopic
                      ? "bg-coral/10 font-semibold text-coral"
                      : "text-ink/70 hover:bg-muted"
                  }`}
                >
                  All Topics
                </button>
                {currentCategory?.topics.map((t) => (
                  <button
                    key={t.topicId}
                    onClick={() => setSelectedTopic(t.topicName)}
                    className={`flex items-center justify-between rounded-lg px-3 py-2 text-left text-sm transition-colors ${
                      selectedTopic === t.topicName
                        ? "bg-coral/10 font-semibold text-coral"
                        : "text-ink/70 hover:bg-muted"
                    }`}
                  >
                    <span>{t.topicName}</span>
                    <span className="text-xs text-muted-foreground">{t.questionCount}</span>
                  </button>
                ))}
              </div>

              <h3 className="mb-2 mt-4 text-sm font-bold text-ink">Difficulty</h3>
              <div className="flex gap-2">
                {["", "EASY", "MEDIUM", "HARD"].map((d) => (
                  <button
                    key={d}
                    onClick={() => setSelectedDifficulty(d)}
                    className={`rounded-lg px-3 py-1.5 text-xs font-semibold transition-colors ${
                      selectedDifficulty === d
                        ? "bg-coral/10 text-coral"
                        : "bg-muted text-ink/60 hover:bg-muted/80"
                    }`}
                  >
                    {d || "All"}
                  </button>
                ))}
              </div>
            </div>

            {/* Question card */}
            <div className="rounded-2xl border border-border bg-card p-6">
              {loading ? (
                <div className="flex items-center justify-center py-20">
                  <div className="size-8 animate-spin rounded-full border-4 border-coral border-t-transparent" />
                </div>
              ) : questions.length === 0 ? (
                <div className="py-20 text-center">
                  <HelpCircle className="mx-auto mb-3 size-12 text-muted-foreground" />
                  <p className="text-muted-foreground">No questions found for this filter.</p>
                </div>
              ) : currentQuestion ? (
                <>
                  {/* Progress bar */}
                  <div className="mb-4 flex items-center gap-3">
                    <span className="text-xs font-semibold text-muted-foreground">
                      {currentIndex + 1} / {questions.length}
                    </span>
                    <div className="h-1.5 flex-1 rounded-full bg-muted">
                      <div
                        className="h-full rounded-full bg-coral transition-all"
                        style={{ width: `${((currentIndex + 1) / questions.length) * 100}%` }}
                      />
                    </div>
                    <span
                      className={`rounded-full px-2 py-0.5 text-xs font-semibold ${
                        currentQuestion.difficulty === "EASY"
                          ? "bg-emerald-100 text-emerald-700"
                          : currentQuestion.difficulty === "MEDIUM"
                            ? "bg-amber-100 text-amber-700"
                            : "bg-red-100 text-red-700"
                      }`}
                    >
                      {currentQuestion.difficulty}
                    </span>
                  </div>

                  {/* Topic badge */}
                  <div className="mb-3 flex items-center gap-2">
                    <span className="rounded-full bg-sky/30 px-3 py-1 text-xs font-semibold text-ink/80">
                      {currentQuestion.topic}
                    </span>
                    {currentQuestion.companiesAsked && currentQuestion.companiesAsked.length > 0 && (
                      <span className="text-xs text-muted-foreground">
                        Asked by: {currentQuestion.companiesAsked.join(", ")}
                      </span>
                    )}
                  </div>

                  {/* Question */}
                  <h2 className="mb-5 text-lg font-bold leading-relaxed text-ink">
                    {currentQuestion.question}
                  </h2>

                  {/* Options */}
                  <div className="mb-6 flex flex-col gap-3">
                    {currentQuestion.options.map((opt, i) => {
                      const isSelected = selectedOption === i;
                      const isCorrect = showExplanation && i === currentQuestion.correctOptionIndex;
                      const isWrong = showExplanation && isSelected && !isCorrect;

                      return (
                        <button
                          key={i}
                          disabled={showExplanation}
                          onClick={() => setSelectedOption(i)}
                          className={`flex items-start gap-3 rounded-xl border-2 px-4 py-3 text-left text-sm transition-all ${
                            isCorrect
                              ? "border-emerald-400 bg-emerald-50"
                              : isWrong
                                ? "border-red-400 bg-red-50"
                                : isSelected
                                  ? "border-coral bg-coral/5"
                                  : "border-border hover:border-coral/40"
                          }`}
                        >
                          <span
                            className={`mt-0.5 flex size-6 shrink-0 items-center justify-center rounded-full text-xs font-bold ${
                              isCorrect
                                ? "bg-emerald-500 text-white"
                                : isWrong
                                  ? "bg-red-500 text-white"
                                  : isSelected
                                    ? "bg-coral text-white"
                                    : "bg-muted text-ink/60"
                            }`}
                          >
                            {isCorrect ? (
                              <Check className="size-3.5" />
                            ) : isWrong ? (
                              <XCircle className="size-3.5" />
                            ) : (
                              String.fromCharCode(65 + i)
                            )}
                          </span>
                          <span className="leading-relaxed">{opt}</span>
                        </button>
                      );
                    })}
                  </div>

                  {/* Check / Explanation */}
                  {!showExplanation ? (
                    <button
                      disabled={selectedOption === null}
                      onClick={() => setShowExplanation(true)}
                      className="inline-flex items-center gap-2 rounded-xl bg-coral px-6 py-2.5 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-coral/90 disabled:opacity-40"
                    >
                      <Sparkles className="size-4" /> Check Answer
                    </button>
                  ) : (
                    <div className="rounded-xl border border-sky/40 bg-sky/10 p-4">
                      <div className="mb-2 flex items-center gap-2 text-sm font-bold text-ink">
                        {selectedOption === currentQuestion.correctOptionIndex ? (
                          <>
                            <CheckCircle2 className="size-5 text-emerald-500" />
                            Correct!
                          </>
                        ) : (
                          <>
                            <XCircle className="size-5 text-red-500" />
                            Incorrect
                          </>
                        )}
                      </div>
                      <p className="text-sm leading-relaxed text-ink/80">
                        {currentQuestion.explanation}
                      </p>
                      {currentQuestion.conceptTip && (
                        <p className="mt-2 rounded-lg bg-peach/20 p-2 text-xs font-medium text-ink/70">
                          💡 {currentQuestion.conceptTip}
                        </p>
                      )}
                    </div>
                  )}

                  {/* Navigation */}
                  <div className="mt-6 flex items-center justify-between">
                    <button
                      disabled={currentIndex === 0}
                      onClick={() => {
                        setCurrentIndex((i) => i - 1);
                        setSelectedOption(null);
                        setShowExplanation(false);
                      }}
                      className="inline-flex items-center gap-1 rounded-lg px-4 py-2 text-sm font-medium text-ink/70 transition-colors hover:bg-muted disabled:opacity-30"
                    >
                      <ChevronLeft className="size-4" /> Previous
                    </button>
                    <button
                      disabled={currentIndex >= questions.length - 1}
                      onClick={() => {
                        setCurrentIndex((i) => i + 1);
                        setSelectedOption(null);
                        setShowExplanation(false);
                      }}
                      className="inline-flex items-center gap-1 rounded-lg bg-ink/5 px-4 py-2 text-sm font-medium text-ink transition-colors hover:bg-ink/10 disabled:opacity-30"
                    >
                      Next <ChevronRight className="size-4" />
                    </button>
                  </div>
                </>
              ) : null}
            </div>
          </div>
        )}

        {/* ═══════════════════════════════════════════════════════════════
            MOCK TEST MODE
           ═══════════════════════════════════════════════════════════════ */}
        {mode === "mock" && !testActive && !testResult && (
          <div className="mx-auto max-w-lg rounded-2xl border border-border bg-card p-8 text-center">
            <div className="mx-auto mb-4 flex size-16 items-center justify-center rounded-full bg-coral/10">
              <Target className="size-8 text-coral" />
            </div>
            <h2 className="mb-2 text-xl font-bold text-ink">Technical Mock Test</h2>
            <p className="mb-6 text-sm text-muted-foreground">
              10 randomized questions from{" "}
              <strong>{currentCategory?.title || selectedCategory}</strong> — 10 minutes — instant
              grading with detailed review.
            </p>
            <button
              onClick={startMockTest}
              disabled={loading}
              className="inline-flex items-center gap-2 rounded-xl bg-coral px-8 py-3 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-coral/90 disabled:opacity-50"
            >
              {loading ? (
                <div className="size-4 animate-spin rounded-full border-2 border-white border-t-transparent" />
              ) : (
                <Zap className="size-4" />
              )}
              Start Mock Test
            </button>
          </div>
        )}

        {mode === "mock" && testActive && mockQuestion && (
          <div className="grid gap-6 lg:grid-cols-[220px_1fr]">
            {/* Navigation panel */}
            <div className="rounded-2xl border border-border bg-card p-4">
              <div className="mb-4 flex items-center gap-2 text-lg font-bold text-coral">
                <Clock className="size-5" />
                {formatTime(timeLeft)}
              </div>
              <div className="mb-4 grid grid-cols-5 gap-1.5">
                {mockQuestions.map((q, i) => {
                  const answered = mockAnswers[q.id] !== undefined;
                  const flagged = flaggedQuestions.has(q.id);
                  return (
                    <button
                      key={q.id}
                      onClick={() => setMockCurrentIndex(i)}
                      className={`relative flex size-9 items-center justify-center rounded-lg text-xs font-bold transition-all ${
                        i === mockCurrentIndex
                          ? "bg-coral text-white ring-2 ring-coral/30"
                          : answered
                            ? "bg-emerald-100 text-emerald-700"
                            : "bg-muted text-ink/50"
                      }`}
                    >
                      {i + 1}
                      {flagged && (
                        <span className="absolute -right-1 -top-1 size-2.5 rounded-full bg-amber-400" />
                      )}
                    </button>
                  );
                })}
              </div>
              <div className="flex flex-col gap-1 text-xs text-muted-foreground">
                <span>
                  ✅ Answered: {Object.keys(mockAnswers).length}/{mockQuestions.length}
                </span>
                <span>🚩 Flagged: {flaggedQuestions.size}</span>
              </div>
              <button
                onClick={handleMockSubmit}
                disabled={submittingTest}
                className="mt-4 w-full rounded-xl bg-coral px-4 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-coral/90 disabled:opacity-50"
              >
                {submittingTest ? "Submitting…" : "Submit Test"}
              </button>
            </div>

            {/* Question */}
            <div className="rounded-2xl border border-border bg-card p-6">
              <div className="mb-3 flex items-center justify-between">
                <span className="text-xs font-semibold text-muted-foreground">
                  Question {mockCurrentIndex + 1} of {mockQuestions.length}
                </span>
                <button
                  onClick={() => {
                    const next = new Set(flaggedQuestions);
                    if (next.has(mockQuestion.id)) next.delete(mockQuestion.id);
                    else next.add(mockQuestion.id);
                    setFlaggedQuestions(next);
                  }}
                  className={`inline-flex items-center gap-1 rounded-lg px-3 py-1.5 text-xs font-semibold transition-colors ${
                    flaggedQuestions.has(mockQuestion.id)
                      ? "bg-amber-100 text-amber-700"
                      : "bg-muted text-ink/50 hover:text-amber-600"
                  }`}
                >
                  <Flag className="size-3.5" />
                  {flaggedQuestions.has(mockQuestion.id) ? "Flagged" : "Flag"}
                </button>
              </div>

              <div className="mb-2">
                <span className="rounded-full bg-sky/30 px-3 py-1 text-xs font-semibold text-ink/80">
                  {mockQuestion.topic}
                </span>
              </div>

              <h2 className="mb-5 text-lg font-bold leading-relaxed text-ink">
                {mockQuestion.question}
              </h2>

              <div className="mb-6 flex flex-col gap-3">
                {mockQuestion.options.map((opt, i) => (
                  <button
                    key={i}
                    onClick={() => setMockAnswers({ ...mockAnswers, [mockQuestion.id]: i })}
                    className={`flex items-start gap-3 rounded-xl border-2 px-4 py-3 text-left text-sm transition-all ${
                      mockAnswers[mockQuestion.id] === i
                        ? "border-coral bg-coral/5"
                        : "border-border hover:border-coral/40"
                    }`}
                  >
                    <span
                      className={`mt-0.5 flex size-6 shrink-0 items-center justify-center rounded-full text-xs font-bold ${
                        mockAnswers[mockQuestion.id] === i
                          ? "bg-coral text-white"
                          : "bg-muted text-ink/60"
                      }`}
                    >
                      {String.fromCharCode(65 + i)}
                    </span>
                    <span className="leading-relaxed">{opt}</span>
                  </button>
                ))}
              </div>

              <div className="flex items-center justify-between">
                <button
                  disabled={mockCurrentIndex === 0}
                  onClick={() => setMockCurrentIndex((i) => i - 1)}
                  className="inline-flex items-center gap-1 rounded-lg px-4 py-2 text-sm font-medium text-ink/70 transition-colors hover:bg-muted disabled:opacity-30"
                >
                  <ChevronLeft className="size-4" /> Previous
                </button>
                <button
                  disabled={mockCurrentIndex >= mockQuestions.length - 1}
                  onClick={() => setMockCurrentIndex((i) => i + 1)}
                  className="inline-flex items-center gap-1 rounded-lg bg-ink/5 px-4 py-2 text-sm font-medium text-ink transition-colors hover:bg-ink/10 disabled:opacity-30"
                >
                  Next <ChevronRight className="size-4" />
                </button>
              </div>
            </div>
          </div>
        )}

        {/* ── MOCK TEST RESULT ───────────────────────────────────── */}
        {mode === "mock" && testResult && (
          <div className="space-y-6">
            {/* Score card */}
            <div className="rounded-2xl border border-border bg-card p-6">
              <div className="flex flex-wrap items-center gap-6">
                <div
                  className="grid size-28 place-items-center rounded-full"
                  style={{
                    background: `conic-gradient(var(--coral) ${testResult.scorePercentage * 3.6}deg, var(--muted) 0deg)`,
                  }}
                >
                  <div className="flex size-[90px] flex-col items-center justify-center rounded-full bg-card">
                    <span className="text-2xl font-extrabold text-ink">
                      {testResult.scorePercentage}%
                    </span>
                  </div>
                </div>
                <div className="flex-1">
                  <h2 className="text-lg font-bold text-ink">{testResult.performanceVerdict}</h2>
                  <p className="mt-1 text-sm text-muted-foreground">
                    {testResult.performanceFeedback}
                  </p>
                  <div className="mt-3 flex flex-wrap gap-3 text-xs">
                    <span className="rounded-full bg-emerald-100 px-3 py-1 font-semibold text-emerald-700">
                      ✓ {testResult.correctCount} Correct
                    </span>
                    <span className="rounded-full bg-red-100 px-3 py-1 font-semibold text-red-700">
                      ✕ {testResult.incorrectCount} Incorrect
                    </span>
                    <span className="rounded-full bg-muted px-3 py-1 font-semibold text-muted-foreground">
                      — {testResult.unattemptedCount} Unattempted
                    </span>
                    <span className="rounded-full bg-sky/30 px-3 py-1 font-semibold text-ink/70">
                      ⏱ {formatTime(testResult.totalTimeSpentSeconds)}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            {/* Topic breakdown */}
            {testResult.topicBreakdowns.length > 0 && (
              <div className="rounded-2xl border border-border bg-card p-6">
                <h3 className="mb-4 flex items-center gap-2 text-sm font-bold text-ink">
                  <BarChart3 className="size-4 text-coral" /> Topic Breakdown
                </h3>
                <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                  {testResult.topicBreakdowns.map((tb) => (
                    <div key={tb.topic} className="rounded-xl border border-border p-3">
                      <div className="mb-1 text-sm font-semibold text-ink">{tb.topic}</div>
                      <div className="mb-2 flex items-center gap-2 text-xs text-muted-foreground">
                        {tb.correct}/{tb.total} correct
                      </div>
                      <div className="h-1.5 rounded-full bg-muted">
                        <div
                          className={`h-full rounded-full transition-all ${
                            tb.accuracy >= 80
                              ? "bg-emerald-500"
                              : tb.accuracy >= 50
                                ? "bg-amber-500"
                                : "bg-red-500"
                          }`}
                          style={{ width: `${tb.accuracy}%` }}
                        />
                      </div>
                      <span className="mt-1 text-xs font-semibold text-ink/60">
                        {tb.accuracy}%
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Question reviews */}
            <div className="rounded-2xl border border-border bg-card p-6">
              <h3 className="mb-4 flex items-center gap-2 text-sm font-bold text-ink">
                <FileText className="size-4 text-coral" /> Question Review
              </h3>
              <div className="flex flex-col gap-4">
                {testResult.questionReviews.map((r, idx) => (
                  <div
                    key={r.questionId}
                    className={`rounded-xl border-2 p-4 ${
                      r.isCorrect
                        ? "border-emerald-200 bg-emerald-50/50"
                        : r.isAttempted
                          ? "border-red-200 bg-red-50/50"
                          : "border-border bg-muted/30"
                    }`}
                  >
                    <div className="mb-2 flex items-center gap-2">
                      <span className="text-xs font-bold text-muted-foreground">Q{idx + 1}</span>
                      {r.isCorrect ? (
                        <CheckCircle2 className="size-4 text-emerald-500" />
                      ) : r.isAttempted ? (
                        <XCircle className="size-4 text-red-500" />
                      ) : (
                        <AlertCircle className="size-4 text-muted-foreground" />
                      )}
                      <span className="rounded-full bg-sky/20 px-2 py-0.5 text-xs text-ink/60">
                        {r.topic}
                      </span>
                    </div>
                    <p className="mb-2 text-sm font-semibold text-ink">{r.question}</p>
                    <div className="mb-2 grid gap-1 text-xs">
                      {r.options.map((o, oi) => (
                        <div
                          key={oi}
                          className={`rounded-lg px-3 py-1.5 ${
                            oi === r.correctOptionIndex
                              ? "bg-emerald-100 font-semibold text-emerald-800"
                              : oi === r.selectedOptionIndex && !r.isCorrect
                                ? "bg-red-100 text-red-800 line-through"
                                : "text-ink/60"
                          }`}
                        >
                          {String.fromCharCode(65 + oi)}. {o}
                        </div>
                      ))}
                    </div>
                    <p className="text-xs leading-relaxed text-ink/70">{r.explanation}</p>
                    {r.conceptTip && (
                      <p className="mt-1 text-xs font-medium text-coral/80">💡 {r.conceptTip}</p>
                    )}
                  </div>
                ))}
              </div>
            </div>

            {/* Retake button */}
            <div className="text-center">
              <button
                onClick={() => {
                  setTestResult(null);
                  setTestActive(false);
                }}
                className="inline-flex items-center gap-2 rounded-xl bg-coral px-6 py-2.5 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-coral/90"
              >
                <RotateCcw className="size-4" /> Take Another Test
              </button>
            </div>
          </div>
        )}

        {/* ═══════════════════════════════════════════════════════════════
            CHEATSHEET MODE
           ═══════════════════════════════════════════════════════════════ */}
        {mode === "cheatsheet" && (
          <div>
            <div className="mb-5 flex flex-wrap gap-2">
              {["All", ...new Set(formulas.map((f) => f.category))].map((cat) => (
                <button
                  key={cat}
                  onClick={() => setFormulaFilter(cat)}
                  className={`rounded-full px-4 py-1.5 text-xs font-semibold transition-colors ${
                    formulaFilter === cat
                      ? "bg-coral text-white"
                      : "bg-muted text-ink/60 hover:bg-muted/80"
                  }`}
                >
                  {cat}
                </button>
              ))}
            </div>
            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {formulas
                .filter((f) => formulaFilter === "All" || f.category === formulaFilter)
                .map((f, i) => (
                  <div
                    key={i}
                    className="rounded-2xl border border-border bg-card p-5 transition-shadow hover:shadow-md"
                  >
                    <div className="mb-1 flex items-center gap-2">
                      <span className="rounded-full bg-coral/10 px-2.5 py-0.5 text-xs font-semibold text-coral">
                        {f.category}
                      </span>
                      <span className="text-xs text-muted-foreground">{f.topic}</span>
                    </div>
                    <h4 className="mb-2 text-sm font-bold text-ink">{f.title}</h4>
                    <div className="mb-2 rounded-lg bg-ink/5 px-3 py-2 font-mono text-xs leading-relaxed text-ink/80">
                      {f.formula}
                    </div>
                    <p className="mb-1 text-xs text-muted-foreground">💡 {f.tip}</p>
                    <p className="text-xs italic text-ink/50">Example: {f.example}</p>
                  </div>
                ))}
            </div>
          </div>
        )}

        {/* ═══════════════════════════════════════════════════════════════
            HISTORY MODE
           ═══════════════════════════════════════════════════════════════ */}
        {mode === "history" && (
          <div className="rounded-2xl border border-border bg-card p-6">
            <h3 className="mb-4 flex items-center gap-2 text-sm font-bold text-ink">
              <History className="size-4 text-coral" /> Past Technical Tests
            </h3>
            {loadingHistory ? (
              <div className="flex items-center justify-center py-12">
                <div className="size-6 animate-spin rounded-full border-3 border-coral border-t-transparent" />
              </div>
            ) : history.length === 0 ? (
              <div className="py-12 text-center">
                <FileText className="mx-auto mb-3 size-10 text-muted-foreground" />
                <p className="text-sm text-muted-foreground">
                  No test history yet. Take a mock test to see your results here.
                </p>
              </div>
            ) : (
              <div className="flex flex-col gap-3">
                {history.map((h) => (
                  <div
                    key={h.id}
                    className="flex items-center gap-4 rounded-xl border border-border p-4 transition-colors hover:bg-muted/30"
                  >
                    <div
                      className="grid size-12 place-items-center rounded-full"
                      style={{
                        background: `conic-gradient(var(--coral) ${h.scorePercentage * 3.6}deg, var(--muted) 0deg)`,
                      }}
                    >
                      <div className="flex size-9 items-center justify-center rounded-full bg-card text-xs font-bold text-ink">
                        {h.scorePercentage}%
                      </div>
                    </div>
                    <div className="flex-1">
                      <div className="text-sm font-semibold text-ink">{h.categoryTitle}</div>
                      <div className="text-xs text-muted-foreground">
                        {h.correctCount}/{h.totalQuestions} correct — {h.verdict}
                      </div>
                    </div>
                    <div className="text-xs text-muted-foreground">
                      {new Date(h.createdAt).toLocaleDateString()}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import {
  Calculator,
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
} from "lucide-react";
import {
  api,
  AptitudeCategorySummary,
  AptitudeQuestion,
  AptitudeResultResponse,
  FormulaCard,
} from "../lib/api";
import { SiteHeader } from "../components/SiteHeader";

export const Route = createFileRoute("/aptitude")({
  component: AptitudeModule,
});

type Mode = "practice" | "mock" | "cheatsheet";

function AptitudeModule() {
  const [mode, setMode] = useState<Mode>("practice");
  const [categories, setCategories] = useState<AptitudeCategorySummary[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>("QUANTITATIVE");
  const [selectedTopic, setSelectedTopic] = useState<string>("");
  const [selectedDifficulty, setSelectedDifficulty] = useState<string>("");
  const [questions, setQuestions] = useState<AptitudeQuestion[]>([]);
  const [loading, setLoading] = useState(true);

  // Practice state
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedOption, setSelectedOption] = useState<number | null>(null);
  const [showExplanation, setShowExplanation] = useState(false);

  // Mock test state
  const [mockQuestions, setMockQuestions] = useState<AptitudeQuestion[]>([]);
  const [mockAnswers, setMockAnswers] = useState<Record<string, number | null>>({});
  const [flaggedQuestions, setFlaggedQuestions] = useState<Set<string>>(new Set());
  const [mockCurrentIndex, setMockCurrentIndex] = useState(0);
  const [timeLeft, setTimeLeft] = useState(600); // 10 minutes
  const [testActive, setTestActive] = useState(false);
  const [submittingTest, setSubmittingTest] = useState(false);
  const [testResult, setTestResult] = useState<AptitudeResultResponse | null>(null);

  // Cheatsheet state
  const [formulas, setFormulas] = useState<FormulaCard[]>([]);
  const [formulaFilter, setFormulaFilter] = useState("All");

  // Load category summaries & formulas on mount
  useEffect(() => {
    async function loadInitial() {
      try {
        setLoading(true);
        const [cats, forms] = await Promise.all([
          api.getAptitudeCategories(),
          api.getAptitudeCheatsheet(),
        ]);
        setCategories(cats);
        setFormulas(forms);
      } catch (err) {
        console.error("Failed to load aptitude categories:", err);
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
        const qList = await api.getAptitudeQuestions({
          category: selectedCategory,
          topic: selectedTopic || undefined,
          difficulty: selectedDifficulty || undefined,
        });
        setQuestions(qList);
        setCurrentIndex(0);
        setSelectedOption(null);
        setShowExplanation(false);
      } catch (err) {
        console.error("Failed to load questions:", err);
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
    return () => clearInterval(interval);
  }, [testActive, timeLeft, testResult]);

  const handleStartMockTest = async () => {
    try {
      setLoading(true);
      const testSet = await api.getAptitudeMockTest(selectedCategory || undefined, 10);
      setMockQuestions(testSet);
      setMockAnswers({});
      setFlaggedQuestions(new Set());
      setMockCurrentIndex(0);
      setTimeLeft(600); // 10 minutes
      setTestResult(null);
      setTestActive(true);
    } catch (err) {
      console.error("Failed to generate mock test:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleMockSubmit = async () => {
    if (submittingTest) return;
    setSubmittingTest(true);
    try {
      const submission = {
        testId: `MOCK_${Date.now()}`,
        category: selectedCategory,
        totalTimeSpentSeconds: 600 - timeLeft,
        answers: mockQuestions.map((q) => ({
          questionId: q.id,
          selectedOptionIndex: mockAnswers[q.id] !== undefined ? mockAnswers[q.id] : null,
          timeSpentSeconds: Math.floor((600 - timeLeft) / (mockQuestions.length || 1)),
        })),
      };

      const result = await api.submitAptitudeTest(submission);
      setTestResult(result);
      setTestActive(false);
    } catch (err) {
      console.error("Failed to submit test:", err);
    } finally {
      setSubmittingTest(false);
    }
  };

  const currentQ = questions[currentIndex];
  const currentMockQ = mockQuestions[mockCurrentIndex];

  const formatTimer = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`;
  };

  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />

      <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6">
        {/* Header Breadcrumb & Modes */}
        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-coral">
              <Link to="/dashboard" className="hover:underline">Dashboard</Link>
              <span>/</span>
              <span>Aptitude Training</span>
            </div>
            <h1 className="mt-1 font-display text-3xl font-extrabold text-ink sm:text-4xl">
              Aptitude & Reasoning Practice
            </h1>
            <p className="mt-1 text-sm text-ink/70">
              Practice placement quantitative problems, logical puzzles, and verbal reasoning with clear step-by-step solutions.
            </p>
          </div>

          {/* Mode Switcher Tabs */}
          <div className="inline-flex rounded-2xl border border-border bg-card p-1 shadow-2xs">
            <button
              onClick={() => {
                setMode("practice");
                setTestActive(false);
              }}
              className={`flex items-center gap-2 rounded-xl px-4 py-2 text-xs font-bold transition-all ${
                mode === "practice"
                  ? "bg-coral text-primary-foreground shadow-sm"
                  : "text-ink/70 hover:text-ink"
              }`}
            >
              <BookOpen className="size-3.5" />
              Practice
            </button>
            <button
              onClick={() => {
                setMode("mock");
                if (!testActive && !testResult) {
                  handleStartMockTest();
                }
              }}
              className={`flex items-center gap-2 rounded-xl px-4 py-2 text-xs font-bold transition-all ${
                mode === "mock"
                  ? "bg-coral text-primary-foreground shadow-sm"
                  : "text-ink/70 hover:text-ink"
              }`}
            >
              <Clock className="size-3.5" />
              Timed Test
            </button>
            <button
              onClick={() => {
                setMode("cheatsheet");
                setTestActive(false);
              }}
              className={`flex items-center gap-2 rounded-xl px-4 py-2 text-xs font-bold transition-all ${
                mode === "cheatsheet"
                  ? "bg-coral text-primary-foreground shadow-sm"
                  : "text-ink/70 hover:text-ink"
              }`}
            >
              <Zap className="size-3.5" />
              Formula Cheatsheet
            </button>
          </div>
        </div>

        {/* ════════════════════════════════════════════════════════════════════ */}
        {/* MODE 1: PRACTICE MODE */}
        {/* ════════════════════════════════════════════════════════════════════ */}
        {mode === "practice" && (
          <div className="mt-8 space-y-6">
            {/* Category Navigation Bar */}
            <div className="grid gap-4 sm:grid-cols-3">
              {categories.map((cat) => {
                const isSelected = selectedCategory === cat.category;
                return (
                  <button
                    key={cat.category}
                    onClick={() => {
                      setSelectedCategory(cat.category);
                      setSelectedTopic("");
                    }}
                    className={`rounded-3xl border p-5 text-left transition-all ${
                      isSelected
                        ? "border-coral bg-card shadow-md ring-2 ring-coral/20"
                        : "border-border bg-card/60 hover:bg-card hover:shadow-sm"
                    }`}
                  >
                    <div className="flex items-center justify-between">
                      <span className="text-xs font-bold uppercase tracking-wider text-coral">
                        {cat.category === "QUANTITATIVE"
                          ? "Quantitative"
                          : cat.category === "LOGICAL_REASONING"
                          ? "Logical"
                          : "Verbal"}
                      </span>
                      <span className="rounded-full bg-peach/40 px-2.5 py-0.5 text-xs font-semibold text-ink">
                        {cat.totalQuestions} Questions
                      </span>
                    </div>
                    <h3 className="mt-2 font-display text-lg font-bold text-ink">{cat.title}</h3>
                    <p className="mt-1 text-xs text-ink/65 line-clamp-2">{cat.description}</p>
                  </button>
                );
              })}
            </div>

            {/* Filter Pills: Topics & Difficulties */}
            <div className="flex flex-wrap items-center justify-between gap-3 rounded-2xl border border-border bg-card p-4">
              <div className="flex flex-wrap items-center gap-2">
                <span className="text-xs font-bold text-ink/60 mr-1 flex items-center gap-1">
                  <Filter className="size-3.5" /> Topics:
                </span>
                <button
                  onClick={() => setSelectedTopic("")}
                  className={`rounded-full px-3 py-1 text-xs font-semibold transition-all ${
                    selectedTopic === ""
                      ? "bg-ink text-background"
                      : "bg-muted text-ink/70 hover:text-ink"
                  }`}
                >
                  All Topics
                </button>
                {categories
                  .find((c) => c.category === selectedCategory)
                  ?.topics.map((t) => (
                    <button
                      key={t.topicId}
                      onClick={() => setSelectedTopic(t.topicName)}
                      className={`rounded-full px-3 py-1 text-xs font-semibold transition-all ${
                        selectedTopic === t.topicName
                          ? "bg-coral text-primary-foreground shadow-2xs"
                          : "bg-muted text-ink/70 hover:text-ink"
                      }`}
                    >
                      {t.topicName} ({t.questionCount})
                    </button>
                  ))}
              </div>

              {/* Difficulty Dropdown / Pills */}
              <div className="flex items-center gap-1.5">
                {["", "EASY", "MEDIUM", "HARD"].map((diff) => (
                  <button
                    key={diff}
                    onClick={() => setSelectedDifficulty(diff)}
                    className={`rounded-full px-2.5 py-1 text-[0.7rem] font-bold uppercase transition-all ${
                      selectedDifficulty === diff
                        ? "bg-coral text-primary-foreground"
                        : "bg-muted text-ink/60 hover:text-ink"
                    }`}
                  >
                    {diff || "All Levels"}
                  </button>
                ))}
              </div>
            </div>

            {/* Question Studio Card */}
            {loading ? (
              <div className="flex flex-col items-center justify-center rounded-3xl border border-border bg-card py-20">
                <div className="size-8 animate-spin rounded-full border-2 border-coral border-t-transparent" />
                <p className="mt-3 text-xs font-semibold text-ink/70">Loading placement questions...</p>
              </div>
            ) : questions.length === 0 ? (
              <div className="rounded-3xl border border-dashed border-border bg-card/40 p-12 text-center">
                <AlertCircle className="mx-auto size-8 text-coral/70" />
                <h3 className="mt-2 font-display text-lg font-bold text-ink">No questions found</h3>
                <p className="mt-1 text-xs text-ink/60">
                  Try clearing topic or difficulty filters to explore all questions in this category.
                </p>
                <button
                  onClick={() => {
                    setSelectedTopic("");
                    setSelectedDifficulty("");
                  }}
                  className="mt-4 inline-flex items-center gap-1.5 rounded-full bg-coral px-4 py-1.5 text-xs font-bold text-primary-foreground"
                >
                  Reset Filters
                </button>
              </div>
            ) : (
              <div className="grid gap-6 lg:grid-cols-3">
                {/* Main Question Display */}
                <div className="space-y-6 lg:col-span-2">
                  <div className="rounded-3xl border border-border bg-card p-6 shadow-sm sm:p-8">
                    {/* Header bar of question */}
                    <div className="flex flex-wrap items-center justify-between gap-3 border-b border-border/60 pb-4">
                      <div className="flex items-center gap-2">
                        <span className="rounded-full bg-coral/15 px-3 py-1 text-xs font-bold text-coral">
                          Question {currentIndex + 1} of {questions.length}
                        </span>
                        <span className="rounded-full bg-muted px-2.5 py-1 text-xs font-semibold text-ink/70">
                          {currentQ.topic}
                        </span>
                        <span
                          className={`rounded-full px-2.5 py-1 text-[0.65rem] font-extrabold uppercase tracking-wide ${
                            currentQ.difficulty === "EASY"
                              ? "bg-mint/60 text-emerald-800"
                              : currentQ.difficulty === "MEDIUM"
                              ? "bg-peach/60 text-amber-900"
                              : "bg-coral/20 text-coral"
                          }`}
                        >
                          {currentQ.difficulty}
                        </span>
                      </div>

                      {/* Companies tags */}
                      {currentQ.companiesAsked && (
                        <div className="flex items-center gap-1 text-xs text-ink/60">
                          <span className="font-semibold text-ink/80">Asked in:</span>
                          {currentQ.companiesAsked.map((c) => (
                            <span
                              key={c}
                              className="rounded bg-muted/80 px-1.5 py-0.5 text-[0.65rem] font-bold text-ink"
                            >
                              {c}
                            </span>
                          ))}
                        </div>
                      )}
                    </div>

                    {/* Question Statement */}
                    <div className="mt-6">
                      <p className="font-display text-lg font-bold leading-relaxed text-ink sm:text-xl">
                        {currentQ.question}
                      </p>
                    </div>

                    {/* Options Grid */}
                    <div className="mt-6 space-y-3">
                      {currentQ.options.map((opt, idx) => {
                        const isChosen = selectedOption === idx;
                        const isCorrect = currentQ.correctOptionIndex === idx;
                        let optionStyle = "border-border bg-muted/40 hover:bg-muted text-ink";

                        if (selectedOption !== null || showExplanation) {
                          if (isCorrect) {
                            optionStyle = "border-emerald-500 bg-emerald-50/80 text-emerald-950 font-semibold ring-1 ring-emerald-500";
                          } else if (isChosen && !isCorrect) {
                            optionStyle = "border-coral bg-coral/10 text-coral font-semibold";
                          } else {
                            optionStyle = "border-border/60 opacity-60 text-ink/70";
                          }
                        }

                        return (
                          <button
                            key={idx}
                            onClick={() => {
                              setSelectedOption(idx);
                              setShowExplanation(true);
                            }}
                            className={`flex w-full items-center justify-between rounded-2xl border p-4 text-left transition-all ${optionStyle}`}
                          >
                            <div className="flex items-center gap-3">
                              <span
                                className={`flex size-7 items-center justify-center rounded-xl text-xs font-bold ${
                                  isChosen
                                    ? "bg-ink text-background"
                                    : "bg-background border border-border text-ink"
                                }`}
                              >
                                {String.fromCharCode(65 + idx)}
                              </span>
                              <span className="text-sm">{opt}</span>
                            </div>

                            {/* Result icon if revealed */}
                            {(selectedOption !== null || showExplanation) && isCorrect && (
                              <CheckCircle2 className="size-5 text-emerald-600 shrink-0" />
                            )}
                            {(selectedOption !== null || showExplanation) && isChosen && !isCorrect && (
                              <XCircle className="size-5 text-coral shrink-0" />
                            )}
                          </button>
                        );
                      })}
                    </div>

                    {/* Action Bar (Show Solution / Next Question) */}
                    <div className="mt-8 flex flex-wrap items-center justify-between gap-4 border-t border-border/60 pt-5">
                      <button
                        onClick={() => setShowExplanation(!showExplanation)}
                        className="inline-flex items-center gap-1.5 text-xs font-bold text-coral hover:underline"
                      >
                        <Sparkles className="size-4" />
                        {showExplanation ? "Hide Solution" : "Show Solution & Steps"}
                      </button>

                      <div className="flex items-center gap-2">
                        <button
                          disabled={currentIndex === 0}
                          onClick={() => {
                            setCurrentIndex((prev) => Math.max(0, prev - 1));
                            setSelectedOption(null);
                            setShowExplanation(false);
                          }}
                          className="flex size-9 items-center justify-center rounded-xl border border-border bg-card text-ink disabled:opacity-30 hover:bg-muted"
                        >
                          <ChevronLeft className="size-4" />
                        </button>
                        <button
                          disabled={currentIndex >= questions.length - 1}
                          onClick={() => {
                            setCurrentIndex((prev) => Math.min(questions.length - 1, prev + 1));
                            setSelectedOption(null);
                            setShowExplanation(false);
                          }}
                          className="flex items-center gap-1.5 rounded-xl bg-coral px-4 py-2 text-xs font-bold text-primary-foreground disabled:opacity-40 hover:bg-coral/90"
                        >
                          Next Question <ChevronRight className="size-4" />
                        </button>
                      </div>
                    </div>

                    {/* Step-by-Step Derivation Drawer */}
                    {showExplanation && (
                      <div className="mt-6 rounded-2xl border border-mint bg-mint/20 p-5">
                        <div className="flex items-center gap-2 text-emerald-900">
                          <CheckCircle2 className="size-4 text-emerald-700" />
                          <h4 className="font-display text-sm font-bold">
                            Solution Breakdown
                          </h4>
                        </div>
                        <div className="mt-3 whitespace-pre-line text-xs leading-relaxed text-ink/85 font-mono bg-card/80 p-3.5 rounded-xl border border-border/50">
                          {currentQ.explanation}
                        </div>

                        {currentQ.formulaTip && (
                          <div className="mt-3 flex items-start gap-2 rounded-xl bg-peach/40 p-3 text-xs text-ink/90 border border-peach">
                            <Zap className="mt-0.5 size-3.5 shrink-0 text-amber-700" />
                            <div>
                              <span className="font-bold text-amber-950">Quick Formula Tip: </span>
                              <span>{currentQ.formulaTip}</span>
                            </div>
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                </div>

                {/* Right Side: Topic Breakdown & Quick Cheats */}
                <div className="space-y-6">
                  {/* Topic Key Concept Card */}
                  <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
                    <div className="flex items-center gap-2 text-coral">
                      <Zap className="size-4" />
                      <h3 className="font-display text-base font-bold text-ink">Key Concept</h3>
                    </div>
                    <p className="mt-2 text-xs font-semibold text-ink">{currentQ.topic}</p>
                    <p className="mt-1 text-xs leading-relaxed text-ink/70">
                      {categories
                        .find((c) => c.category === selectedCategory)
                        ?.topics.find((t) => t.topicName === currentQ.topic)?.keyConcept ||
                        "Core principles and shortcuts for this question type."}
                    </p>

                    <div className="mt-5 border-t border-border/60 pt-4">
                      <p className="text-[0.65rem] font-bold uppercase tracking-wider text-ink/50">
                        Time Target
                      </p>
                      <p className="mt-1 text-xs text-ink/80">
                        Aim for <strong>under 60 seconds</strong> per question in actual placement assessments.
                      </p>
                    </div>
                  </div>

                  {/* Quick Cheatsheet snippet */}
                  <div className="rounded-3xl border border-border bg-peach/30 p-6">
                    <div className="flex items-center justify-between">
                      <h4 className="font-display text-sm font-bold text-ink">Formula Snapshot</h4>
                      <button
                        onClick={() => setMode("cheatsheet")}
                        className="text-xs font-bold text-coral hover:underline"
                      >
                        All Formulas →
                      </button>
                    </div>
                    <div className="mt-4 space-y-3">
                      {formulas.slice(0, 3).map((f, i) => (
                        <div key={i} className="rounded-xl bg-card p-3 shadow-2xs">
                          <p className="text-xs font-bold text-ink">{f.title}</p>
                          <p className="mt-1 font-mono text-[0.7rem] text-coral">{f.formula}</p>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* ════════════════════════════════════════════════════════════════════ */}
        {/* MODE 2: TIMED MOCK TEST SIMULATOR */}
        {/* ════════════════════════════════════════════════════════════════════ */}
        {mode === "mock" && (
          <div className="mt-8 space-y-6">
            {!testActive && !testResult && (
              <div className="mx-auto max-w-xl rounded-3xl border border-border bg-card p-8 text-center shadow-sm">
                <div className="mx-auto flex size-14 items-center justify-center rounded-2xl bg-coral/15 text-coral">
                  <Clock className="size-7" />
                </div>
                <h2 className="mt-4 font-display text-2xl font-bold text-ink">
                  Placement Mock Test Simulator
                </h2>
                <p className="mt-2 text-xs leading-relaxed text-ink/70">
                  Simulate the exact conditions of online assessment rounds (TCS NQT, Infosys DSE, Amazon OA).
                  10 randomized questions across Quantitative, Logical, and Verbal topics with a 10-minute timer.
                </p>

                <div className="mt-6 grid grid-cols-3 gap-3 rounded-2xl bg-muted/50 p-4 text-center">
                  <div>
                    <p className="text-xs text-ink/60">Questions</p>
                    <p className="font-display text-lg font-bold text-ink">10</p>
                  </div>
                  <div>
                    <p className="text-xs text-ink/60">Duration</p>
                    <p className="font-display text-lg font-bold text-ink">10 Mins</p>
                  </div>
                  <div>
                    <p className="text-xs text-ink/60">Cutoff Bar</p>
                    <p className="font-display text-lg font-bold text-coral">70%+</p>
                  </div>
                </div>

                <button
                  onClick={handleStartMockTest}
                  disabled={loading}
                  className="mt-6 inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-coral py-3.5 text-sm font-bold text-primary-foreground shadow-sm hover:bg-coral/90"
                >
                  <Sparkles className="size-4" /> Start Mock Assessment
                </button>
              </div>
            )}

            {/* Active Test Screen */}
            {testActive && currentMockQ && (
              <div className="grid gap-6 lg:grid-cols-4">
                {/* Main Question Interface */}
                <div className="space-y-6 lg:col-span-3">
                  <div className="rounded-3xl border border-border bg-card p-6 shadow-sm sm:p-8">
                    {/* Header with Timer and Flag */}
                    <div className="flex flex-wrap items-center justify-between gap-4 border-b border-border/60 pb-4">
                      <div className="flex items-center gap-2">
                        <span className="rounded-full bg-coral/15 px-3 py-1 text-xs font-bold text-coral">
                          Mock Question {mockCurrentIndex + 1} of {mockQuestions.length}
                        </span>
                        <span className="rounded-full bg-muted px-2.5 py-1 text-xs font-semibold text-ink/70">
                          {currentMockQ.topic}
                        </span>
                      </div>

                      {/* Timer & Mark for Review */}
                      <div className="flex items-center gap-3">
                        <button
                          onClick={() => {
                            const flagged = new Set(flaggedQuestions);
                            if (flagged.has(currentMockQ.id)) {
                              flagged.delete(currentMockQ.id);
                            } else {
                              flagged.add(currentMockQ.id);
                            }
                            setFlaggedQuestions(flagged);
                          }}
                          className={`flex items-center gap-1 rounded-xl px-3 py-1.5 text-xs font-bold transition-all ${
                            flaggedQuestions.has(currentMockQ.id)
                              ? "bg-amber-100 text-amber-900 border border-amber-300"
                              : "bg-muted text-ink/70 hover:text-ink"
                          }`}
                        >
                          <Flag className="size-3.5" />
                          {flaggedQuestions.has(currentMockQ.id) ? "Flagged" : "Mark Review"}
                        </button>

                        <div className="flex items-center gap-1.5 rounded-xl bg-coral/10 px-3 py-1.5 font-mono text-sm font-bold text-coral">
                          <Clock className="size-4" />
                          <span>{formatTimer(timeLeft)}</span>
                        </div>
                      </div>
                    </div>

                    {/* Question text */}
                    <div className="mt-6">
                      <p className="font-display text-lg font-bold leading-relaxed text-ink sm:text-xl">
                        {currentMockQ.question}
                      </p>
                    </div>

                    {/* Options list */}
                    <div className="mt-6 space-y-3">
                      {currentMockQ.options.map((opt, idx) => {
                        const isSelected = mockAnswers[currentMockQ.id] === idx;
                        return (
                          <button
                            key={idx}
                            onClick={() => {
                              setMockAnswers({
                                ...mockAnswers,
                                [currentMockQ.id]: idx,
                              });
                            }}
                            className={`flex w-full items-center justify-between rounded-2xl border p-4 text-left transition-all ${
                              isSelected
                                ? "border-coral bg-coral/10 font-bold text-ink shadow-2xs ring-1 ring-coral"
                                : "border-border bg-muted/40 hover:bg-muted text-ink"
                            }`}
                          >
                            <div className="flex items-center gap-3">
                              <span
                                className={`flex size-7 items-center justify-center rounded-xl text-xs font-bold ${
                                  isSelected
                                    ? "bg-coral text-primary-foreground"
                                    : "bg-background border border-border text-ink"
                                }`}
                              >
                                {String.fromCharCode(65 + idx)}
                              </span>
                              <span className="text-sm">{opt}</span>
                            </div>
                            {isSelected && <Check className="size-4 text-coral" />}
                          </button>
                        );
                      })}
                    </div>

                    {/* Test navigation controls */}
                    <div className="mt-8 flex flex-wrap items-center justify-between gap-4 border-t border-border/60 pt-5">
                      <button
                        onClick={() => {
                          const updated = { ...mockAnswers };
                          delete updated[currentMockQ.id];
                          setMockAnswers(updated);
                        }}
                        className="text-xs font-bold text-ink/60 hover:text-coral hover:underline"
                      >
                        Clear Response
                      </button>

                      <div className="flex items-center gap-2">
                        <button
                          disabled={mockCurrentIndex === 0}
                          onClick={() => setMockCurrentIndex((p) => Math.max(0, p - 1))}
                          className="flex size-9 items-center justify-center rounded-xl border border-border bg-card text-ink disabled:opacity-30 hover:bg-muted"
                        >
                          <ChevronLeft className="size-4" />
                        </button>
                        <button
                          disabled={mockCurrentIndex >= mockQuestions.length - 1}
                          onClick={() => setMockCurrentIndex((p) => Math.min(mockQuestions.length - 1, p + 1))}
                          className="flex items-center gap-1.5 rounded-xl bg-ink px-4 py-2 text-xs font-bold text-background disabled:opacity-30 hover:bg-ink/90"
                        >
                          Next <ChevronRight className="size-4" />
                        </button>
                        <button
                          onClick={handleMockSubmit}
                          disabled={submittingTest}
                          className="flex items-center gap-1.5 rounded-xl bg-coral px-5 py-2 text-xs font-bold text-primary-foreground hover:bg-coral/90"
                        >
                          {submittingTest ? "Grading..." : "Submit Test"}
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Question Palette Sidebar */}
                <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
                  <h3 className="font-display text-sm font-bold text-ink">Question Palette</h3>
                  <div className="mt-4 grid grid-cols-5 gap-2">
                    {mockQuestions.map((q, idx) => {
                      const isAnswered = mockAnswers[q.id] !== undefined;
                      const isFlagged = flaggedQuestions.has(q.id);
                      const isCurrent = mockCurrentIndex === idx;

                      let btnStyle = "bg-muted text-ink/70 border-border";
                      if (isAnswered) btnStyle = "bg-emerald-500 text-white border-emerald-600";
                      if (isFlagged) btnStyle = "bg-amber-400 text-amber-950 border-amber-500";
                      if (isCurrent) btnStyle += " ring-2 ring-coral ring-offset-2";

                      return (
                        <button
                          key={q.id}
                          onClick={() => setMockCurrentIndex(idx)}
                          className={`flex size-10 items-center justify-center rounded-xl border text-xs font-bold transition-all ${btnStyle}`}
                        >
                          {idx + 1}
                        </button>
                      );
                    })}
                  </div>

                  {/* Legend */}
                  <div className="mt-6 space-y-2 border-t border-border/60 pt-4 text-xs text-ink/70">
                    <div className="flex items-center gap-2">
                      <span className="size-3 rounded-md bg-emerald-500" />
                      <span>Answered ({Object.keys(mockAnswers).length})</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="size-3 rounded-md bg-amber-400" />
                      <span>Flagged for review ({flaggedQuestions.size})</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="size-3 rounded-md bg-muted border border-border" />
                      <span>Unattempted ({mockQuestions.length - Object.keys(mockAnswers).length})</span>
                    </div>
                  </div>

                  <button
                    onClick={handleMockSubmit}
                    disabled={submittingTest}
                    className="mt-6 w-full rounded-2xl bg-coral py-2.5 text-xs font-bold text-primary-foreground hover:bg-coral/90"
                  >
                    Finish & Submit Test
                  </button>
                </div>
              </div>
            )}

            {/* Test Results View */}
            {testResult && (
              <div className="space-y-8">
                {/* Score Card Banner */}
                <div className="rounded-3xl border border-border bg-card p-6 shadow-sm sm:p-8">
                  <div className="flex flex-col items-center justify-between gap-6 sm:flex-row">
                    <div>
                      <span className="rounded-full bg-coral/15 px-3 py-1 text-xs font-bold text-coral">
                        ASSESSMENT REPORT
                      </span>
                      <h2 className="mt-2 font-display text-2xl font-extrabold text-ink sm:text-3xl">
                        {testResult.performanceVerdict}
                      </h2>
                      <p className="mt-2 max-w-2xl text-xs leading-relaxed text-ink/70">
                        {testResult.performanceFeedback}
                      </p>
                    </div>

                    {/* Accuracy Ring */}
                    <div className="flex flex-col items-center justify-center rounded-2xl bg-muted/40 p-6 text-center border border-border">
                      <span className="font-display text-4xl font-black text-coral">
                        {testResult.scorePercentage}%
                      </span>
                      <span className="mt-1 text-[0.7rem] font-bold uppercase tracking-wider text-ink/60">
                        Overall Accuracy
                      </span>
                    </div>
                  </div>

                  {/* Summary Metric Counters */}
                  <div className="mt-6 grid grid-cols-2 gap-4 sm:grid-cols-4 border-t border-border/60 pt-6 text-center">
                    <div className="rounded-2xl bg-muted/30 p-4">
                      <p className="text-xs text-ink/60">Total Questions</p>
                      <p className="font-display text-2xl font-bold text-ink">{testResult.totalQuestions}</p>
                    </div>
                    <div className="rounded-2xl bg-emerald-50 p-4">
                      <p className="text-xs text-emerald-800 font-semibold">Correct</p>
                      <p className="font-display text-2xl font-bold text-emerald-700">{testResult.correctCount}</p>
                    </div>
                    <div className="rounded-2xl bg-coral/10 p-4">
                      <p className="text-xs text-coral font-semibold">Incorrect</p>
                      <p className="font-display text-2xl font-bold text-coral">{testResult.incorrectCount}</p>
                    </div>
                    <div className="rounded-2xl bg-muted/30 p-4">
                      <p className="text-xs text-ink/60">Time Spent</p>
                      <p className="font-display text-2xl font-bold text-ink">
                        {formatTimer(testResult.totalTimeSpentSeconds)}
                      </p>
                    </div>
                  </div>

                  <div className="mt-6 flex justify-end gap-3">
                    <button
                      onClick={handleStartMockTest}
                      className="inline-flex items-center gap-1.5 rounded-2xl bg-coral px-5 py-2.5 text-xs font-bold text-primary-foreground hover:bg-coral/90"
                    >
                      <RotateCcw className="size-3.5" /> Retake New Mock Test
                    </button>
                  </div>
                </div>

                {/* Topic Breakdown Bars */}
                {testResult.topicBreakdowns.length > 0 && (
                  <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
                    <h3 className="font-display text-base font-bold text-ink">Topic-Wise Performance</h3>
                    <div className="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                      {testResult.topicBreakdowns.map((tb) => (
                        <div key={tb.topic} className="rounded-2xl border border-border/70 p-4 bg-muted/20">
                          <div className="flex items-center justify-between text-xs">
                            <span className="font-bold text-ink">{tb.topic}</span>
                            <span className="font-semibold text-coral">{tb.accuracy}%</span>
                          </div>
                          <div className="mt-2 h-2 w-full overflow-hidden rounded-full bg-muted">
                            <div
                              className="h-full rounded-full bg-coral transition-all"
                              style={{ width: `${tb.accuracy}%` }}
                            />
                          </div>
                          <p className="mt-2 text-[0.7rem] text-ink/60">
                            {tb.correct} of {tb.total} correct
                          </p>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* Question-by-Question Review Accordion */}
                <div className="space-y-4">
                  <h3 className="font-display text-lg font-bold text-ink">Question Review & Solutions</h3>
                  {testResult.questionReviews.map((rev, idx) => (
                    <div
                      key={rev.questionId}
                      className={`rounded-3xl border p-6 transition-all ${
                        rev.isCorrect
                          ? "border-emerald-200 bg-emerald-50/20"
                          : rev.isAttempted
                          ? "border-coral/30 bg-coral/5"
                          : "border-border bg-card"
                      }`}
                    >
                      <div className="flex flex-wrap items-center justify-between gap-2">
                        <div className="flex items-center gap-2">
                          <span
                            className={`flex size-6 items-center justify-center rounded-lg text-xs font-bold ${
                              rev.isCorrect
                                ? "bg-emerald-600 text-white"
                                : rev.isAttempted
                                ? "bg-coral text-white"
                                : "bg-muted text-ink/60"
                            }`}
                          >
                            {idx + 1}
                          </span>
                          <span className="text-xs font-bold text-ink">{rev.topic}</span>
                        </div>
                        <span
                          className={`rounded-full px-2.5 py-0.5 text-xs font-bold ${
                            rev.isCorrect
                              ? "bg-emerald-100 text-emerald-800"
                              : rev.isAttempted
                              ? "bg-coral/15 text-coral"
                              : "bg-muted text-ink/60"
                          }`}
                        >
                          {rev.isCorrect ? "Correct" : rev.isAttempted ? "Incorrect" : "Unattempted"}
                        </span>
                      </div>

                      <p className="mt-3 text-sm font-semibold text-ink">{rev.question}</p>

                      <div className="mt-4 grid gap-2 sm:grid-cols-2">
                        {rev.options.map((opt, oIdx) => {
                          const isSelected = rev.selectedOptionIndex === oIdx;
                          const isCorrect = rev.correctOptionIndex === oIdx;
                          let style = "border-border/60 bg-card text-ink/70";
                          if (isCorrect) style = "border-emerald-500 bg-emerald-100 text-emerald-950 font-bold";
                          if (isSelected && !isCorrect) style = "border-coral bg-coral/20 text-coral font-bold";

                          return (
                            <div
                              key={oIdx}
                              className={`flex items-center justify-between rounded-xl border p-2.5 text-xs ${style}`}
                            >
                              <span>
                                {String.fromCharCode(65 + oIdx)}. {opt}
                              </span>
                              {isCorrect && <CheckCircle2 className="size-3.5 text-emerald-700" />}
                              {isSelected && !isCorrect && <XCircle className="size-3.5 text-coral" />}
                            </div>
                          );
                        })}
                      </div>

                      {/* Explanation box */}
                      <div className="mt-4 rounded-xl bg-card p-3.5 text-xs font-mono text-ink/80 border border-border/60 whitespace-pre-line">
                        <span className="font-sans font-bold text-ink block mb-1">Step-by-Step Derivation:</span>
                        {rev.explanation}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {/* ════════════════════════════════════════════════════════════════════ */}
        {/* MODE 3: FORMULA CHEATSHEET */}
        {/* ════════════════════════════════════════════════════════════════════ */}
        {mode === "cheatsheet" && (
          <div className="mt-8 space-y-6">
            <div className="flex flex-wrap items-center justify-between gap-4">
              <div>
                <h2 className="font-display text-2xl font-bold text-ink">
                  Placement Formula & Shortcut Cheatsheet
                </h2>
                <p className="text-xs text-ink/70">
                  Speed formulas for rapid quantitative calculation and logical deduction tricks.
                </p>
              </div>

              {/* Category pills */}
              <div className="flex gap-2">
                {["All", "Quantitative", "Logical", "Verbal"].map((cat) => (
                  <button
                    key={cat}
                    onClick={() => setFormulaFilter(cat)}
                    className={`rounded-full px-3 py-1 text-xs font-bold transition-all ${
                      formulaFilter === cat
                        ? "bg-coral text-primary-foreground"
                        : "bg-muted text-ink/70 hover:text-ink"
                    }`}
                  >
                    {cat}
                  </button>
                ))}
              </div>
            </div>

            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {formulas
                .filter((f) => formulaFilter === "All" || f.category.toLowerCase().includes(formulaFilter.toLowerCase()))
                .map((f, i) => (
                  <div
                    key={i}
                    className="flex flex-col justify-between rounded-3xl border border-border bg-card p-6 shadow-sm"
                  >
                    <div>
                      <div className="flex items-center justify-between">
                        <span className="text-[0.65rem] font-bold uppercase tracking-wider text-coral">
                          {f.category} · {f.topic}
                        </span>
                        <Zap className="size-3.5 text-amber-600" />
                      </div>
                      <h3 className="mt-2 font-display text-base font-bold text-ink">{f.title}</h3>
                      <div className="mt-3 rounded-2xl bg-muted/60 p-3 font-mono text-xs font-semibold text-coral border border-border/40 whitespace-pre-line">
                        {f.formula}
                      </div>
                      <p className="mt-3 text-xs leading-relaxed text-ink/75">{f.tip}</p>
                    </div>

                    <div className="mt-4 border-t border-border/60 pt-3 text-[0.7rem] text-ink/60">
                      <span className="font-bold text-ink/80">Example: </span>
                      {f.example}
                    </div>
                  </div>
                ))}
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

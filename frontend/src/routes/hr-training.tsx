import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import {
  Sparkles,
  CheckCircle2,
  AlertTriangle,
  Send,
  History,
  Target,
  ArrowLeft,
  Briefcase,
  Check,
  TrendingUp,
  MessageSquare,
  Award,
  Lightbulb,
} from "lucide-react";
import {
  api,
  HrCategoryType,
  HrPrompt,
  HrEvaluationResponse,
  HrHistoryItem,
} from "../lib/api";
import { SiteHeader } from "../components/SiteHeader";

export const Route = createFileRoute("/hr-training")({
  component: HrTrainingModule,
});

const CATEGORIES: { id: HrCategoryType; label: string }[] = [
  { id: "SELF_INTRODUCTION", label: "Self-Introduction" },
  { id: "LEADERSHIP", label: "Leadership & Ownership" },
  { id: "CONFLICT_RESOLUTION", label: "Conflict Resolution" },
  { id: "TEAMWORK", label: "Teamwork & Collaboration" },
  { id: "FAILURE_RESILIENCE", label: "Handling Challenges" },
  { id: "CAREER_VISION", label: "Career Goals & Fit" },
];

function HrTrainingModule() {
  const [prompts, setPrompts] = useState<HrPrompt[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<HrCategoryType>("SELF_INTRODUCTION");
  const [selectedPrompt, setSelectedPrompt] = useState<HrPrompt | null>(null);
  const [userAnswer, setUserAnswer] = useState("");
  const [loadingPrompts, setLoadingPrompts] = useState(true);
  const [evaluating, setEvaluating] = useState(false);
  const [evaluationResult, setEvaluationResult] = useState<HrEvaluationResponse | null>(null);
  const [history, setHistory] = useState<HrHistoryItem[]>([]);
  const [showHistory, setShowHistory] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  useEffect(() => {
    async function loadPrompts() {
      try {
        setLoadingPrompts(true);
        const data = await api.getHrPrompts(selectedCategory);
        setPrompts(data);
        if (data.length > 0) {
          setSelectedPrompt(data[0]);
          setUserAnswer("");
          setEvaluationResult(null);
        }
      } catch (err) {
        console.error("Failed to load HR questions:", err);
      } finally {
        setLoadingPrompts(false);
      }
    }
    loadPrompts();
  }, [selectedCategory]);

  const loadHistory = async () => {
    try {
      const hist = await api.getHrHistory();
      setHistory(hist);
    } catch (err) {
      console.error("Failed to load submission history:", err);
    }
  };

  useEffect(() => {
    loadHistory();
  }, []);

  const handleEvaluate = async () => {
    if (!userAnswer.trim()) {
      setErrorMsg("Please write your answer before submitting for evaluation.");
      return;
    }
    setErrorMsg("");
    try {
      setEvaluating(true);
      const res = await api.evaluateHrResponse({
        promptId: selectedPrompt?.id,
        category: selectedCategory,
        questionText: selectedPrompt?.question,
        userResponse: userAnswer,
      });
      setEvaluationResult(res);
      loadHistory();
    } catch (err: any) {
      console.error("Evaluation error:", err);
      setErrorMsg(err.message || "Unable to evaluate answer. Please try again.");
    } finally {
      setEvaluating(false);
    }
  };

  const handleFillSample = () => {
    if (selectedPrompt?.sampleModelAnswer) {
      setUserAnswer(selectedPrompt.sampleModelAnswer);
      setErrorMsg("");
    }
  };

  const words = userAnswer.trim() ? userAnswer.trim().split(/\s+/).length : 0;

  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />

      <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6">
        {/* Header Bar */}
        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-coral">
              <Link to="/dashboard" className="hover:underline">Dashboard</Link>
              <span>/</span>
              <span>HR Training</span>
            </div>
            <h1 className="mt-1 font-display text-3xl font-extrabold text-ink sm:text-4xl">
              HR Interview Practice
            </h1>
            <p className="mt-1 text-sm text-ink/70">
              Practice common HR and behavioral questions, get immediate feedback on your answers, and learn how to present your experiences effectively.
            </p>
          </div>

          <button
            onClick={() => setShowHistory(!showHistory)}
            className="inline-flex items-center gap-2 rounded-2xl border border-border bg-card px-4 py-2.5 text-xs font-bold text-ink shadow-2xs hover:bg-muted"
          >
            <History className="size-4 text-coral" />
            {showHistory ? "Back to Practice" : `Past Submissions (${history.length})`}
          </button>
        </div>

        {/* ════════════════════════════════════════════════════════════════════ */}
        {/* VIEW 1: PAST SUBMISSIONS */}
        {/* ════════════════════════════════════════════════════════════════════ */}
        {showHistory ? (
          <div className="mt-8 space-y-6">
            <div className="flex items-center justify-between">
              <div>
                <h2 className="font-display text-xl font-bold text-ink">
                  Past Submissions ({history.length})
                </h2>
                <p className="text-xs text-ink/60">Review your previous answers and scores.</p>
              </div>
              <button
                onClick={() => setShowHistory(false)}
                className="inline-flex items-center gap-1.5 text-xs font-bold text-coral hover:underline"
              >
                <ArrowLeft className="size-3.5" /> Back to Practice
              </button>
            </div>

            {history.length === 0 ? (
              <div className="rounded-3xl border border-dashed border-border bg-card/40 p-12 text-center">
                <MessageSquare className="mx-auto size-8 text-coral/70" />
                <h3 className="mt-2 font-display text-lg font-bold text-ink">No saved answers yet</h3>
                <p className="mt-1 text-xs text-ink/60">
                  Select a question, write your answer, and click "Evaluate Answer" to see your score.
                </p>
              </div>
            ) : (
              <div className="grid gap-4 md:grid-cols-2">
                {history.map((item) => (
                  <div
                    key={item.id}
                    className="flex flex-col justify-between rounded-3xl border border-border bg-card p-6 shadow-sm"
                  >
                    <div>
                      <div className="flex items-center justify-between">
                        <span className="rounded-full bg-peach/50 px-2.5 py-0.5 text-[0.7rem] font-bold text-ink">
                          {item.categoryTitle}
                        </span>
                        <span className="font-display text-lg font-black text-coral">
                          {item.overallScore}/100
                        </span>
                      </div>
                      <h3 className="mt-3 font-display text-sm font-bold text-ink">
                        {item.questionText}
                      </h3>
                      <p className="mt-2 text-xs text-ink/75 line-clamp-3 bg-muted/40 p-3 rounded-xl border border-border/40">
                        "{item.userResponseSnippet}"
                      </p>
                    </div>

                    <div className="mt-4 flex items-center justify-between border-t border-border/60 pt-3 text-[0.7rem] text-ink/60">
                      <span className="font-semibold text-ink">{item.verdict}</span>
                      <span>{new Date(item.createdAt).toLocaleDateString()}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        ) : (
          /* ════════════════════════════════════════════════════════════════════ */
          /* VIEW 2: MAIN PRACTICE INTERFACE */
          /* ════════════════════════════════════════════════════════════════════ */
          <div className="mt-8 space-y-6">
            {/* Category Navigation Tabs */}
            <div className="flex overflow-x-auto gap-2.5 pb-2 scrollbar-none">
              {CATEGORIES.map((cat) => {
                const isActive = selectedCategory === cat.id;
                return (
                  <button
                    key={cat.id}
                    onClick={() => setSelectedCategory(cat.id)}
                    className={`shrink-0 rounded-2xl border px-4 py-2.5 text-xs font-bold transition-all ${
                      isActive
                        ? "border-coral bg-coral text-primary-foreground shadow-sm ring-2 ring-coral/20"
                        : "border-border bg-card text-ink/80 hover:bg-card/80 hover:border-coral/40"
                    }`}
                  >
                    {cat.label}
                  </button>
                );
              })}
            </div>

            {/* Questions List */}
            <div className="flex flex-wrap items-center gap-2 rounded-2xl border border-border bg-card p-4">
              <span className="text-xs font-bold text-ink/60 mr-2 flex items-center gap-1">
                <Target className="size-3.5" /> Questions:
              </span>
              {loadingPrompts ? (
                <span className="text-xs text-ink/60">Loading questions...</span>
              ) : (
                prompts.map((p, idx) => (
                  <button
                    key={p.id}
                    onClick={() => {
                      setSelectedPrompt(p);
                      setUserAnswer("");
                      setEvaluationResult(null);
                      setErrorMsg("");
                    }}
                    className={`rounded-full px-3.5 py-1.5 text-xs font-bold transition-all ${
                      selectedPrompt?.id === p.id
                        ? "bg-ink text-background shadow-xs"
                        : "bg-muted text-ink/70 hover:text-ink"
                    }`}
                  >
                    Question {idx + 1}
                  </button>
                ))
              )}
            </div>

            {/* Question Card & Answer Studio */}
            {selectedPrompt && (
              <div className="grid gap-6 lg:grid-cols-12">
                {/* Left Column: Question & Input Area (7 cols) */}
                <div className="space-y-6 lg:col-span-7">
                  <div className="rounded-3xl border border-border bg-card p-6 shadow-sm sm:p-8">
                    {/* Header of Question Card */}
                    <div className="flex flex-wrap items-center justify-between gap-2 border-b border-border/60 pb-3">
                      <span className="text-xs font-bold uppercase tracking-wider text-coral">
                        {selectedPrompt.categoryTitle}
                      </span>
                      {selectedPrompt.companyTags && (
                        <div className="flex items-center gap-1 text-[0.65rem] text-ink/60">
                          <span className="font-semibold text-ink">Asked by:</span>
                          {selectedPrompt.companyTags.map((co) => (
                            <span key={co} className="rounded bg-muted px-1.5 py-0.5 font-bold text-ink">
                              {co}
                            </span>
                          ))}
                        </div>
                      )}
                    </div>

                    {/* Question Heading */}
                    <h2 className="mt-4 font-display text-xl font-bold leading-snug text-ink sm:text-2xl">
                      "{selectedPrompt.question}"
                    </h2>

                    {/* Interview Tips Card */}
                    <div className="mt-4 rounded-2xl bg-mint/25 p-4 border border-mint/50">
                      <div className="flex items-center gap-2 text-xs font-bold text-emerald-950">
                        <Lightbulb className="size-3.5 text-emerald-700" />
                        <span>Tips for Answering:</span>
                      </div>
                      <p className="mt-1 text-xs text-ink/80 leading-relaxed">
                        {selectedPrompt.recruiterIntent}
                      </p>
                    </div>

                    {/* Talking Points & Things to Avoid */}
                    <div className="mt-4 grid gap-3 sm:grid-cols-2">
                      <div className="rounded-2xl border border-border/70 bg-muted/20 p-3.5">
                        <p className="text-[0.7rem] font-bold text-emerald-800 flex items-center gap-1.5">
                          <CheckCircle2 className="size-3 text-emerald-600" /> Key Points to Cover
                        </p>
                        <ul className="mt-2 space-y-1.5 text-[0.7rem] text-ink/75">
                          {selectedPrompt.keyPointsToInclude.map((pt, i) => (
                            <li key={i} className="flex items-start gap-1.5">
                              <span className="text-coral font-bold">•</span>
                              <span>{pt}</span>
                            </li>
                          ))}
                        </ul>
                      </div>

                      <div className="rounded-2xl border border-border/70 bg-peach/20 p-3.5">
                        <p className="text-[0.7rem] font-bold text-amber-900 flex items-center gap-1.5">
                          <AlertTriangle className="size-3 text-amber-600" /> Things to Avoid
                        </p>
                        <ul className="mt-2 space-y-1.5 text-[0.7rem] text-ink/75">
                          {selectedPrompt.commonPitfalls.map((pf, i) => (
                            <li key={i} className="flex items-start gap-1.5">
                              <span className="text-amber-700 font-bold">•</span>
                              <span>{pf}</span>
                            </li>
                          ))}
                        </ul>
                      </div>
                    </div>

                    {/* Answer Workspace */}
                    <div className="mt-6">
                      <div className="flex items-center justify-between mb-2">
                        <label className="text-xs font-bold text-ink">
                          Your Answer
                        </label>
                        <div className="flex items-center gap-3">
                          <button
                            onClick={handleFillSample}
                            className="text-[0.7rem] font-bold text-coral hover:underline"
                          >
                            Fill Sample Answer
                          </button>
                          <span
                            className={`text-xs font-semibold ${
                              words >= 60 && words <= 250
                                ? "text-emerald-700"
                                : words < 60
                                ? "text-amber-700"
                                : "text-ink/60"
                            }`}
                          >
                            {words} words (recommended: 80–200)
                          </span>
                        </div>
                      </div>

                      <textarea
                        rows={7}
                        value={userAnswer}
                        onChange={(e) => setUserAnswer(e.target.value)}
                        placeholder="Write your answer here. Provide context on the challenge, what you were personally responsible for, what concrete actions you took, and the final results achieved..."
                        className="w-full rounded-2xl border border-border bg-background p-4 text-sm text-ink leading-relaxed placeholder:text-ink/40 focus:border-coral focus:outline-none focus:ring-2 focus:ring-coral/20"
                      />

                      {errorMsg && (
                        <p className="mt-2 text-xs font-semibold text-coral flex items-center gap-1">
                          <AlertTriangle className="size-3.5" /> {errorMsg}
                        </p>
                      )}

                      <div className="mt-4 flex flex-wrap items-center justify-between gap-3">
                        <button
                          onClick={() => {
                            setUserAnswer("");
                            setEvaluationResult(null);
                          }}
                          className="text-xs font-bold text-ink/60 hover:text-ink"
                        >
                          Clear Text
                        </button>

                        <button
                          onClick={handleEvaluate}
                          disabled={evaluating}
                          className="inline-flex items-center gap-2 rounded-2xl bg-coral px-6 py-3 text-xs font-bold text-primary-foreground shadow-sm hover:bg-coral/90 disabled:opacity-50"
                        >
                          {evaluating ? (
                            <>
                              <div className="size-3.5 animate-spin rounded-full border-2 border-primary-foreground border-t-transparent" />
                              Evaluating your response...
                            </>
                          ) : (
                            <>
                              <Send className="size-3.5" /> Evaluate Answer
                            </>
                          )}
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Right Column: Guidance & Feedback (5 cols) */}
                <div className="space-y-6 lg:col-span-5">
                  {evaluationResult ? (
                    /* Evaluated Scorecard */
                    <div className="space-y-6">
                      <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
                        <div className="flex items-center justify-between">
                          <div>
                            <span className="text-[0.65rem] font-bold uppercase tracking-wider text-coral">
                              INTERVIEW SCORE
                            </span>
                            <h3 className="mt-1 font-display text-lg font-bold text-ink">
                              {evaluationResult.verdict}
                            </h3>
                          </div>
                          <div className="flex flex-col items-center justify-center size-16 rounded-2xl bg-coral/15 border border-coral/30">
                            <span className="font-display text-2xl font-black text-coral">
                              {evaluationResult.overallScore}
                            </span>
                            <span className="text-[0.6rem] font-bold text-ink/60">/100</span>
                          </div>
                        </div>

                        <p className="mt-3 text-xs leading-relaxed text-ink/75 bg-muted/30 p-3 rounded-2xl border border-border/40">
                          {evaluationResult.executiveSummary}
                        </p>

                        {/* Four Scoring Criteria */}
                        <div className="mt-5 grid grid-cols-2 gap-2.5">
                          {/* 1. Context */}
                          <div className="rounded-2xl border border-border bg-muted/20 p-3">
                            <div className="flex items-center justify-between text-xs">
                              <span className="font-bold text-ink">Context & Setting</span>
                              <span className="font-mono font-extrabold text-coral">
                                {evaluationResult.situationScore}%
                              </span>
                            </div>
                            <span className="mt-1 inline-block text-[0.65rem] font-semibold text-ink/70">
                              {evaluationResult.starBreakdown.situation.status}
                            </span>
                            <p className="mt-1 text-[0.65rem] text-ink/60 leading-tight">
                              {evaluationResult.starBreakdown.situation.feedback}
                            </p>
                          </div>

                          {/* 2. Responsibility */}
                          <div className="rounded-2xl border border-border bg-muted/20 p-3">
                            <div className="flex items-center justify-between text-xs">
                              <span className="font-bold text-ink">Your Ownership</span>
                              <span className="font-mono font-extrabold text-coral">
                                {evaluationResult.taskScore}%
                              </span>
                            </div>
                            <span className="mt-1 inline-block text-[0.65rem] font-semibold text-ink/70">
                              {evaluationResult.starBreakdown.task.status}
                            </span>
                            <p className="mt-1 text-[0.65rem] text-ink/60 leading-tight">
                              {evaluationResult.starBreakdown.task.feedback}
                            </p>
                          </div>

                          {/* 3. Action */}
                          <div className="rounded-2xl border border-border bg-muted/20 p-3">
                            <div className="flex items-center justify-between text-xs">
                              <span className="font-bold text-ink">Actions Taken</span>
                              <span className="font-mono font-extrabold text-coral">
                                {evaluationResult.actionScore}%
                              </span>
                            </div>
                            <span className="mt-1 inline-block text-[0.65rem] font-semibold text-ink/70">
                              {evaluationResult.starBreakdown.action.status}
                            </span>
                            <p className="mt-1 text-[0.65rem] text-ink/60 leading-tight">
                              {evaluationResult.starBreakdown.action.feedback}
                            </p>
                          </div>

                          {/* 4. Results */}
                          <div className="rounded-2xl border border-border bg-muted/20 p-3">
                            <div className="flex items-center justify-between text-xs">
                              <span className="font-bold text-ink">Results & Impact</span>
                              <span className="font-mono font-extrabold text-coral">
                                {evaluationResult.resultScore}%
                              </span>
                            </div>
                            <span className="mt-1 inline-block text-[0.65rem] font-semibold text-ink/70">
                              {evaluationResult.starBreakdown.result.status}
                            </span>
                            <p className="mt-1 text-[0.65rem] text-ink/60 leading-tight">
                              {evaluationResult.starBreakdown.result.feedback}
                            </p>
                          </div>
                        </div>

                        {/* Power Verbs and Metrics */}
                        {(evaluationResult.actionVerbsDetected.length > 0 ||
                          evaluationResult.metricsDetected.length > 0) && (
                          <div className="mt-4 border-t border-border/60 pt-3 space-y-2">
                            {evaluationResult.actionVerbsDetected.length > 0 && (
                              <div className="flex flex-wrap items-center gap-1 text-[0.65rem]">
                                <span className="font-bold text-ink">Effective Verbs:</span>
                                {evaluationResult.actionVerbsDetected.map((v) => (
                                  <span
                                    key={v}
                                    className="rounded-full bg-emerald-100 px-2 py-0.5 font-semibold text-emerald-800"
                                  >
                                    {v}
                                  </span>
                                ))}
                              </div>
                            )}
                            {evaluationResult.metricsDetected.length > 0 && (
                              <div className="flex flex-wrap items-center gap-1 text-[0.65rem]">
                                <span className="font-bold text-ink">Metrics Mentioned:</span>
                                {evaluationResult.metricsDetected.map((m) => (
                                  <span
                                    key={m}
                                    className="rounded-full bg-peach px-2 py-0.5 font-semibold text-amber-950"
                                  >
                                    {m}
                                  </span>
                                ))}
                              </div>
                            )}
                          </div>
                        )}
                      </div>

                      {/* Strengths & Recommendations */}
                      <div className="rounded-3xl border border-border bg-card p-6 shadow-sm space-y-4">
                        <div>
                          <p className="text-xs font-bold text-emerald-800 flex items-center gap-1.5">
                            <CheckCircle2 className="size-3.5 text-emerald-600" /> Key Strengths
                          </p>
                          <ul className="mt-2 space-y-1.5 text-xs text-ink/80">
                            {evaluationResult.strengths.map((s, idx) => (
                              <li key={idx} className="flex items-start gap-1.5">
                                <span className="text-emerald-600 font-bold">•</span>
                                <span>{s}</span>
                              </li>
                            ))}
                          </ul>
                        </div>

                        <div className="border-t border-border/60 pt-3">
                          <p className="text-xs font-bold text-coral flex items-center gap-1.5">
                            <TrendingUp className="size-3.5 text-coral" /> Recommendations to Improve
                          </p>
                          <ul className="mt-2 space-y-1.5 text-xs text-ink/80">
                            {evaluationResult.areasForImprovement.map((imp, idx) => (
                              <li key={idx} className="flex items-start gap-1.5">
                                <span className="text-coral font-bold">•</span>
                                <span>{imp}</span>
                              </li>
                            ))}
                          </ul>
                        </div>
                      </div>

                      {/* Sample High-Scoring Answer */}
                      <div className="rounded-3xl border border-peach bg-peach/30 p-6">
                        <div className="flex items-center gap-2 text-ink">
                          <Award className="size-4 text-coral" />
                          <h4 className="font-display text-sm font-bold">Recommended Sample Answer</h4>
                        </div>
                        <p className="mt-1 text-xs text-ink/65">
                          How an effective response is phrased for this question:
                        </p>
                        <p className="mt-3 text-xs leading-relaxed text-ink/85 italic bg-card/80 p-4 rounded-2xl border border-peach">
                          "{evaluationResult.barRaiserModelAnswer}"
                        </p>
                      </div>
                    </div>
                  ) : (
                    /* Initial Guidance Card (Before Evaluation) */
                    <div className="space-y-6">
                      <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
                        <div className="flex items-center gap-2 text-coral">
                          <Lightbulb className="size-4" />
                          <h3 className="font-display text-base font-bold text-ink">
                            How Answers Are Evaluated
                          </h3>
                        </div>
                        <p className="mt-1 text-xs text-ink/70">
                          Interviews assess how clearly you structure your real-world experiences:
                        </p>

                        <div className="mt-4 space-y-3">
                          <div className="rounded-2xl bg-muted/40 p-3 border border-border/50">
                            <p className="text-xs font-bold text-ink">1. Context & Setting</p>
                            <p className="mt-1 text-[0.7rem] text-ink/70">
                              Describe the background: the project, team setting, and the specific hurdle or situation.
                            </p>
                          </div>

                          <div className="rounded-2xl bg-muted/40 p-3 border border-border/50">
                            <p className="text-xs font-bold text-ink">2. Your Responsibility</p>
                            <p className="mt-1 text-[0.7rem] text-ink/70">
                              Specify what you personally owned and were responsible for solving.
                            </p>
                          </div>

                          <div className="rounded-2xl bg-muted/40 p-3 border border-border/50">
                            <p className="text-xs font-bold text-ink">3. Actions Taken</p>
                            <p className="mt-1 text-[0.7rem] text-ink/70">
                              Detail the exact steps, technical choices, or collaborative actions you executed.
                            </p>
                          </div>

                          <div className="rounded-2xl bg-muted/40 p-3 border border-border/50">
                            <p className="text-xs font-bold text-ink">4. Results & Impact</p>
                            <p className="mt-1 text-[0.7rem] text-ink/70">
                              Conclude with the measurable outcome, time or efficiency saved, and lessons learned.
                            </p>
                          </div>
                        </div>
                      </div>

                      {/* Helpful Tip */}
                      <div className="rounded-3xl border border-border bg-peach/30 p-6">
                        <h4 className="font-display text-sm font-bold text-amber-950">
                          Interview Tip
                        </h4>
                        <p className="mt-2 text-xs leading-relaxed text-ink/80">
                          Keep your focus on your personal initiative and problem solving. Mention concrete numbers or percentages when possible to show measurable results.
                        </p>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>
        )}
      </main>
    </div>
  );
}

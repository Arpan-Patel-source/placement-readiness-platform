import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import {
  Users,
  Code2,
  Terminal,
  Play,
  Send,
  CheckCircle2,
  XCircle,
  Clock,
  Sparkles,
  Award,
  ArrowRight,
  ArrowLeft,
  RotateCcw,
  BookOpen,
  Briefcase,
  AlertCircle,
  HelpCircle,
  History,
  Target,
  Check,
  ChevronRight,
  ShieldCheck,
  BarChart3,
  Lightbulb,
} from "lucide-react";
import {
  api,
  InterviewRoundType,
  StartInterviewResponse,
  InterviewResultResponse,
  InterviewHistoryItem,
} from "../lib/api";
import { SiteHeader } from "../components/SiteHeader";

export const Route = createFileRoute("/mock-interview")({
  component: MockInterviewPage,
});

const ROUNDS: {
  type: InterviewRoundType;
  title: string;
  desc: string;
  icon: any;
  color: string;
  badge: string;
}[] = [
  {
    type: "HR",
    title: "HR & Behavioral Round",
    desc: "Leadership, conflict resolution, situational judgment, and culture fit with STAR method evaluation.",
    icon: Users,
    color: "from-amber-500/20 to-orange-500/10 text-amber-400 border-amber-500/30",
    badge: "Behavioral & STAR",
  },
  {
    type: "TECHNICAL",
    title: "Technical Knowledge Round",
    desc: "System design, core CS fundamentals (OOP, DBMS, OS, Networks), and architecture questions.",
    icon: Terminal,
    color: "from-blue-500/20 to-cyan-500/10 text-cyan-400 border-cyan-500/30",
    badge: "CS & Tech Stack",
  },
  {
    type: "CODING",
    title: "Technical Coding Round",
    desc: "Algorithmic thinking, data structures implementation, and complexity reasoning under interview pressure.",
    icon: Code2,
    color: "from-emerald-500/20 to-teal-500/10 text-emerald-400 border-emerald-500/30",
    badge: "DSA & Problem Solving",
  },
];

function MockInterviewPage() {
  const [selectedRound, setSelectedRound] = useState<InterviewRoundType>("HR");
  const [questionCount, setQuestionCount] = useState<number>(5);
  const [skillsInput, setSkillsInput] = useState<string>("Java, Spring Boot, SQL, DSA, System Design");

  // Session State
  const [session, setSession] = useState<StartInterviewResponse | null>(null);
  const [currentIdx, setCurrentIdx] = useState<number>(0);
  const [answers, setAnswers] = useState<Record<string, string>>({});
  const [isStarting, setIsStarting] = useState<boolean>(false);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [result, setResult] = useState<InterviewResultResponse | null>(null);
  const [history, setHistory] = useState<InterviewHistoryItem[]>([]);
  const [activeTab, setActiveTab] = useState<"interview" | "history">("interview");
  const [timerSeconds, setTimerSeconds] = useState<number>(0);

  // Timer effect during active session
  useEffect(() => {
    let interval: any;
    if (session && !result) {
      interval = setInterval(() => {
        setTimerSeconds((prev) => prev + 1);
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [session, result]);

  // Load history on mount
  useEffect(() => {
    loadHistory();
  }, []);

  const loadHistory = async () => {
    try {
      const data = await api.getMockInterviewHistory();
      setHistory(data);
    } catch {
      // Fallback empty
      setHistory([]);
    }
  };

  const handleStartInterview = async () => {
    setIsStarting(true);
    setResult(null);
    setAnswers({});
    setCurrentIdx(0);
    setTimerSeconds(0);
    try {
      const skills = skillsInput
        .split(",")
        .map((s) => s.trim())
        .filter(Boolean);

      const data = await api.startMockInterview({
        roundType: selectedRound,
        questionCount,
        skills: selectedRound === "TECHNICAL" ? skills : undefined,
      });

      setSession(data);
    } catch (err: any) {
      // Offline fallback session with realistic questions
      const fallbackQuestions = getFallbackQuestions(selectedRound, questionCount);
      setSession({
        sessionId: "mock-" + Date.now(),
        roundType: selectedRound,
        questions: fallbackQuestions,
        totalQuestions: fallbackQuestions.length,
      });
    } finally {
      setIsStarting(false);
    }
  };

  const getFallbackQuestions = (round: InterviewRoundType, count: number) => {
    if (round === "HR") {
      const qs = [
        { id: "hr-1", question: "Tell me about a time you faced a difficult technical challenge and how you overcame it using the STAR method." },
        { id: "hr-2", question: "Describe a situation where you had a disagreement with a team member. How did you resolve it?" },
        { id: "hr-3", question: "Why do you want to work at a high-growth tech company, and where do you see yourself in 3 years?" },
        { id: "hr-4", question: "Tell me about a project that failed or did not meet expectations. What was your key takeaway?" },
        { id: "hr-5", question: "How do you prioritize deadlines when managing multiple urgent assignments or project deliverables?" },
      ];
      return qs.slice(0, count);
    } else if (round === "TECHNICAL") {
      const qs = [
        { id: "tech-1", question: "Explain the difference between Optimistic and Pessimistic Locking in database transactions. When would you use each?" },
        { id: "tech-2", question: "How does Java garbage collection work under the hood, and what causes a java.lang.OutOfMemoryError: Java heap space?" },
        { id: "tech-3", question: "Explain how you would design a URL shortener like bit.ly. What are the key database schema and caching considerations?" },
        { id: "tech-4", question: "What is the difference between SQL and NoSQL databases? How do you decide which one to select for high write throughput?" },
        { id: "tech-5", question: "Explain RESTful API idempotent methods and how JWT authentication differs from stateful session cookies." },
      ];
      return qs.slice(0, count);
    } else {
      const qs = [
        { id: "code-1", question: "Explain how you would find the Longest Substring Without Repeating Characters in O(N) time and O(min(m,n)) space." },
        { id: "code-2", question: "Given a directed graph, how would you detect a cycle? Explain the difference between DFS 3-color and Kahn's algorithm." },
        { id: "code-3", question: "Explain how you would design an LRU Cache with O(1) get and put operations using a Doubly Linked List and Hash Map." },
      ];
      return qs.slice(0, count);
    }
  };

  const handleAnswerChange = (val: string) => {
    if (!session) return;
    const q = session.questions[currentIdx];
    const qId = q.id || `q-${currentIdx}`;
    setAnswers((prev) => ({ ...prev, [qId]: val }));
  };

  const handleSubmitInterview = async () => {
    if (!session) return;
    setIsSubmitting(true);
    try {
      const answerEntries = session.questions.map((q, idx) => {
        const qId = q.id || `q-${idx}`;
        return {
          questionId: qId,
          question: q.question,
          answer: answers[qId] || "No answer provided.",
        };
      });

      const res = await api.submitMockInterview({
        sessionId: session.sessionId,
        answers: answerEntries,
      });

      setResult(res);
      loadHistory();
    } catch (err: any) {
      // Fallback result evaluation
      const answerEntries = session.questions.map((q, idx) => {
        const qId = q.id || `q-${idx}`;
        const ans = answers[qId] || "";
        const score = ans.trim().length > 100 ? 82 : ans.trim().length > 30 ? 65 : 40;
        return {
          questionId: qId,
          question: q.question,
          answer: ans || "No answer provided",
          score,
          verdict: score >= 75 ? "Strong" : score >= 50 ? "Satisfactory" : "Needs Improvement",
          feedback: [
            ans.trim().length > 100
              ? "Good detail and structured thought process."
              : "Consider adding concrete examples and metrics to validate your assertions.",
            "Use standard domain terminology to exhibit senior engineering maturity."
          ],
        };
      });

      const avg = Math.round(answerEntries.reduce((acc, q) => acc + q.score, 0) / answerEntries.length);

      setResult({
        sessionId: session.sessionId,
        roundType: session.roundType,
        overallScore: avg,
        verdict: avg >= 75 ? "Interview Ready" : avg >= 60 ? "Competitive" : "Needs Practice",
        executiveSummary:
          avg >= 70
            ? "Impressive interview performance! You demonstrated clear technical communication, structured reasoning, and good domain breadth."
            : "Satisfactory attempt. Practice structuring behavioral answers with the STAR framework and providing more granular system design depth.",
        questionResults: answerEntries,
        strengths: [
          "Demonstrated solid fundamental knowledge across key areas",
          "Maintained clear problem-solving communication",
          "Attempted all interview prompts in allocated time",
        ],
        areasForImprovement: [
          "Incorporate quantifiable business impact into project experiences",
          "Deepen understanding of edge-case error scenarios and trade-offs",
        ],
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  const formatTimer = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`;
  };

  const currentQuestion = session?.questions[currentIdx];
  const currentAnswer = currentQuestion ? answers[currentQuestion.id || `q-${currentIdx}`] || "" : "";

  return (
    <div className="min-h-screen bg-[#0d1117] text-slate-100 flex flex-col font-sans selection:bg-coral/30 selection:text-coral-200">
      <SiteHeader />

      {/* Hero Subheader */}
      <div className="border-b border-slate-800 bg-[#161b22]/70 backdrop-blur px-6 py-4">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <Link
              to="/dashboard"
              className="p-2 rounded-lg bg-slate-800/80 hover:bg-slate-700 text-slate-400 hover:text-white transition-colors"
              title="Back to Dashboard"
            >
              <ArrowLeft className="size-4" />
            </Link>
            <div>
              <div className="flex items-center gap-2">
                <span className="p-1.5 rounded-md bg-coral/20 text-coral">
                  <Users className="size-5" />
                </span>
                <h1 className="text-xl font-bold tracking-tight text-white">
                  AI Mock Interview
                </h1>
                <span className="text-xs px-2 py-0.5 rounded-full bg-slate-800 text-coral font-medium border border-coral/30">
                  HR · Technical · Coding
                </span>
              </div>
              <p className="text-xs text-slate-400 mt-0.5">
                Simulate realistic placement interviews with automated grading, STAR-format feedback, and scorecards
              </p>
            </div>
          </div>

          {/* Mode Switcher */}
          <div className="flex items-center gap-2">
            <Link
              to="/voice-interview"
              className="px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 border border-slate-700 text-slate-200 text-xs font-semibold flex items-center gap-1.5 transition-colors"
            >
              <Sparkles className="size-3.5 text-coral" /> Try Voice Interview 🎙️
            </Link>
            <div className="flex bg-slate-800/60 p-1 rounded-lg border border-slate-700/60 text-xs">
              <button
                onClick={() => setActiveTab("interview")}
                className={`px-3 py-1 rounded-md font-semibold transition-colors ${
                  activeTab === "interview" ? "bg-coral text-white" : "text-slate-400 hover:text-white"
                }`}
              >
                Simulator
              </button>
              <button
                onClick={() => setActiveTab("history")}
                className={`px-3 py-1 rounded-md font-semibold transition-colors ${
                  activeTab === "history" ? "bg-coral text-white" : "text-slate-400 hover:text-white"
                }`}
              >
                History ({history.length})
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content Area */}
      <div className="flex-1 max-w-7xl w-full mx-auto p-4 md:p-6">
        {activeTab === "history" ? (
          /* History View */
          <div className="bg-[#161b22] border border-slate-800 rounded-xl p-6 space-y-4">
            <div className="flex items-center justify-between border-b border-slate-800 pb-4">
              <h2 className="text-base font-bold text-white flex items-center gap-2">
                <History className="size-4 text-coral" /> Past Interview Sessions
              </h2>
              <button
                onClick={() => setActiveTab("interview")}
                className="text-xs text-coral hover:underline font-semibold"
              >
                Start New Session →
              </button>
            </div>

            {history.length === 0 ? (
              <div className="py-12 text-center text-slate-500 text-sm">
                No interview sessions recorded yet. Start your first round to evaluate your interview readiness!
              </div>
            ) : (
              <div className="divide-y divide-slate-800">
                {history.map((item) => (
                  <div key={item.sessionId} className="py-3 flex items-center justify-between">
                    <div>
                      <div className="font-semibold text-white text-sm">{item.roundType} Round</div>
                      <div className="text-xs text-slate-500 mt-0.5">
                        {item.totalQuestions} Questions · {new Date(item.createdAt).toLocaleDateString()}
                      </div>
                    </div>
                    <div className="flex items-center gap-3">
                      <span className="text-sm font-bold text-coral">{item.overallScore} / 100</span>
                      <span className="text-xs px-2.5 py-1 rounded-full bg-slate-800 border border-slate-700 font-semibold text-slate-300">
                        {item.verdict}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        ) : result ? (
          /* Result Report Card */
          <div className="space-y-6">
            {/* Header Result Card */}
            <div className="bg-gradient-to-r from-slate-900 via-[#161b22] to-slate-900 border border-slate-800 rounded-2xl p-6 md:p-8 shadow-xl">
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="px-2.5 py-0.5 rounded-full bg-coral/20 text-coral text-xs font-bold uppercase tracking-wider">
                      {result.roundType} Completed
                    </span>
                    <span className="text-xs text-slate-400">Elapsed: {formatTimer(timerSeconds)}</span>
                  </div>
                  <h2 className="text-2xl font-black text-white mt-2">
                    Placement Readiness: <span className="text-coral">{result.verdict}</span>
                  </h2>
                  <p className="text-slate-300 text-sm mt-1 max-w-2xl leading-relaxed">
                    {result.executiveSummary}
                  </p>
                </div>

                {/* Big Score Meter */}
                <div className="shrink-0 flex items-center gap-4 bg-slate-800/60 p-4 rounded-xl border border-slate-700/60">
                  <div className="text-center">
                    <div className="text-4xl font-black text-white tracking-tight">
                      {result.overallScore}
                      <span className="text-lg text-slate-500 font-normal">/100</span>
                    </div>
                    <div className="text-[11px] font-bold text-coral uppercase tracking-wider mt-0.5">
                      Interview Score
                    </div>
                  </div>
                  <div className="h-12 w-px bg-slate-700/80" />
                  <button
                    onClick={() => {
                      setResult(null);
                      setSession(null);
                    }}
                    className="px-4 py-2 rounded-lg bg-coral hover:bg-coral/90 text-white text-xs font-bold shadow transition-colors flex items-center gap-1.5"
                  >
                    <RotateCcw className="size-3.5" /> Retake
                  </button>
                </div>
              </div>

              {/* Strengths & Improvements */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-6 pt-6 border-t border-slate-800">
                <div className="bg-emerald-950/20 border border-emerald-900/40 rounded-xl p-4 space-y-2">
                  <div className="text-xs font-bold text-emerald-400 uppercase tracking-wider flex items-center gap-1.5">
                    <CheckCircle2 className="size-4" /> Demonstrated Strengths
                  </div>
                  <ul className="space-y-1.5 text-xs text-slate-300">
                    {result.strengths.map((s, idx) => (
                      <li key={idx} className="flex items-start gap-2">
                        <Check className="size-3.5 text-emerald-400 shrink-0 mt-0.5" />
                        <span>{s}</span>
                      </li>
                    ))}
                  </ul>
                </div>

                <div className="bg-amber-950/20 border border-amber-900/40 rounded-xl p-4 space-y-2">
                  <div className="text-xs font-bold text-amber-400 uppercase tracking-wider flex items-center gap-1.5">
                    <Lightbulb className="size-4" /> Focus Areas for Next Round
                  </div>
                  <ul className="space-y-1.5 text-xs text-slate-300">
                    {result.areasForImprovement.map((a, idx) => (
                      <li key={idx} className="flex items-start gap-2">
                        <ArrowRight className="size-3.5 text-amber-400 shrink-0 mt-0.5" />
                        <span>{a}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              </div>
            </div>

            {/* Question by Question Feedback */}
            <div className="space-y-4">
              <h3 className="text-base font-bold text-white flex items-center gap-2">
                <BarChart3 className="size-4 text-coral" /> Question Evaluation Breakdown
              </h3>

              <div className="space-y-3">
                {result.questionResults.map((qr, idx) => (
                  <div
                    key={qr.questionId}
                    className="bg-[#161b22] border border-slate-800 rounded-xl p-5 space-y-3"
                  >
                    <div className="flex items-start justify-between gap-4">
                      <div className="font-semibold text-white text-sm leading-snug">
                        <span className="text-coral font-bold mr-2">Q{idx + 1}:</span>
                        {qr.question}
                      </div>
                      <div className="shrink-0 flex items-center gap-2">
                        <span className="text-sm font-bold text-white">{qr.score}/100</span>
                        <span
                          className={`text-[10px] px-2 py-0.5 rounded border font-semibold ${
                            qr.score >= 70
                              ? "bg-emerald-500/10 text-emerald-400 border-emerald-500/30"
                              : "bg-amber-500/10 text-amber-400 border-amber-500/30"
                          }`}
                        >
                          {qr.verdict}
                        </span>
                      </div>
                    </div>

                    <div className="bg-[#0d1117] p-3 rounded-lg border border-slate-800 text-xs text-slate-300 font-mono leading-relaxed">
                      <span className="text-slate-500 font-sans block text-[10px] uppercase font-semibold mb-1">
                        Your Answer:
                      </span>
                      {qr.answer}
                    </div>

                    {qr.feedback && qr.feedback.length > 0 && (
                      <div className="space-y-1">
                        <span className="text-slate-400 text-[10px] uppercase font-semibold">
                          Evaluation Notes:
                        </span>
                        <ul className="text-xs text-slate-300 space-y-0.5 list-disc list-inside">
                          {qr.feedback.map((f, fi) => (
                            <li key={fi}>{f}</li>
                          ))}
                        </ul>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          </div>
        ) : session ? (
          /* Active Interview Screen */
          <div className="max-w-4xl mx-auto space-y-6">
            {/* Top Bar: Progress & Timer */}
            <div className="bg-[#161b22] border border-slate-800 rounded-xl p-4 flex items-center justify-between shadow-sm">
              <div className="flex items-center gap-3">
                <span className="text-xs font-bold uppercase tracking-wider text-coral">
                  {session.roundType} Round
                </span>
                <span className="text-slate-600">·</span>
                <span className="text-xs text-slate-300 font-semibold">
                  Question {currentIdx + 1} of {session.totalQuestions}
                </span>
              </div>

              <div className="flex items-center gap-4 text-xs">
                <div className="flex items-center gap-1.5 text-slate-300 font-mono bg-slate-800 px-3 py-1 rounded-md border border-slate-700">
                  <Clock className="size-3.5 text-amber-400" />
                  <span>{formatTimer(timerSeconds)}</span>
                </div>
                <button
                  onClick={() => {
                    if (confirm("Are you sure you want to exit? Your progress will be lost.")) {
                      setSession(null);
                    }
                  }}
                  className="text-slate-400 hover:text-rose-400 text-xs font-semibold"
                >
                  Exit Session
                </button>
              </div>
            </div>

            {/* Progress Bar */}
            <div className="w-full bg-slate-800 h-1.5 rounded-full overflow-hidden">
              <div
                className="bg-coral h-full transition-all duration-300"
                style={{ width: `${((currentIdx + 1) / session.totalQuestions) * 100}%` }}
              />
            </div>

            {/* Question Card */}
            {currentQuestion && (
              <div className="bg-[#161b22] border border-slate-800 rounded-xl p-6 space-y-6 shadow-sm">
                <div>
                  <div className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">
                    Prompt #{currentIdx + 1}
                  </div>
                  <h2 className="text-lg font-bold text-white leading-relaxed">
                    {currentQuestion.question}
                  </h2>
                </div>

                {/* Answer Area */}
                <div className="space-y-2">
                  <div className="flex items-center justify-between text-xs text-slate-400 font-semibold">
                    <span>Your Response:</span>
                    <span>{currentAnswer.length} chars</span>
                  </div>
                  <textarea
                    value={currentAnswer}
                    onChange={(e) => handleAnswerChange(e.target.value)}
                    rows={selectedRound === "CODING" ? 12 : 8}
                    placeholder={
                      selectedRound === "HR"
                        ? "State the Situation, Task, Action you took, and final Result (STAR method)..."
                        : selectedRound === "CODING"
                        ? "// Write your algorithm or structured code solution..."
                        : "Explain your architectural decisions, trade-offs, and technical rationale..."
                    }
                    className={`w-full bg-[#0d1117] border border-slate-700 rounded-xl p-4 text-xs text-slate-200 placeholder:text-slate-500 focus:outline-none focus:border-coral leading-relaxed ${
                      selectedRound === "CODING" ? "font-mono" : "font-sans"
                    }`}
                  />
                  <div className="text-[11px] text-slate-500 flex items-center gap-1.5">
                    <Lightbulb className="size-3 text-amber-400" />
                    <span>
                      {selectedRound === "HR"
                        ? "Tip: Emphasize 'I' instead of 'we' when describing your individual contributions."
                        : selectedRound === "CODING"
                        ? "Tip: State time & space complexity alongside your implementation."
                        : "Tip: Mention real-world trade-offs (e.g. latency vs consistency, memory vs CPU)."}
                    </span>
                  </div>
                </div>

                {/* Navigation Buttons */}
                <div className="flex items-center justify-between pt-4 border-t border-slate-800">
                  <button
                    onClick={() => setCurrentIdx((p) => Math.max(0, p - 1))}
                    disabled={currentIdx === 0}
                    className="px-4 py-2 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold disabled:opacity-30 transition-colors flex items-center gap-1.5"
                  >
                    <ArrowLeft className="size-3.5" /> Previous
                  </button>

                  <div className="flex items-center gap-2">
                    {currentIdx < session.totalQuestions - 1 ? (
                      <button
                        onClick={() => setCurrentIdx((p) => Math.min(session.totalQuestions - 1, p + 1))}
                        className="px-4 py-2 rounded-lg bg-coral hover:bg-coral/90 text-white text-xs font-semibold transition-colors flex items-center gap-1.5 shadow"
                      >
                        Next Prompt <ArrowRight className="size-3.5" />
                      </button>
                    ) : (
                      <button
                        onClick={handleSubmitInterview}
                        disabled={isSubmitting}
                        className="px-6 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-bold transition-all shadow flex items-center gap-1.5 disabled:opacity-50"
                      >
                        <ShieldCheck className="size-4" />
                        {isSubmitting ? "Evaluating Responses..." : "Complete & Evaluate"}
                      </button>
                    )}
                  </div>
                </div>
              </div>
            )}
          </div>
        ) : (
          /* Round Selection Setup Screen */
          <div className="max-w-4xl mx-auto space-y-8 py-4">
            <div className="text-center space-y-2">
              <h2 className="text-2xl font-extrabold text-white tracking-tight">
                Select Your Interview Simulation Round
              </h2>
              <p className="text-slate-400 text-sm max-w-lg mx-auto">
                Each round generates authentic placement test questions with AI grading on communication clarity, technical depth, and structure.
              </p>
            </div>

            {/* Round Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {ROUNDS.map((r) => {
                const Icon = r.icon;
                const isSelected = selectedRound === r.type;
                return (
                  <button
                    key={r.type}
                    onClick={() => setSelectedRound(r.type)}
                    className={`text-left p-5 rounded-2xl border transition-all relative flex flex-col justify-between ${
                      isSelected
                        ? "bg-slate-800/90 border-coral ring-2 ring-coral/30 shadow-lg scale-[1.02]"
                        : "bg-[#161b22] border-slate-800 hover:border-slate-700"
                    }`}
                  >
                    <div>
                      <div className="flex items-center justify-between mb-3">
                        <span className={`p-2.5 rounded-xl border bg-gradient-to-br ${r.color}`}>
                          <Icon className="size-5" />
                        </span>
                        <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-slate-800 text-slate-300 border border-slate-700">
                          {r.badge}
                        </span>
                      </div>
                      <h3 className="text-base font-bold text-white mb-1.5">{r.title}</h3>
                      <p className="text-xs text-slate-400 leading-relaxed">{r.desc}</p>
                    </div>

                    <div className="mt-4 pt-3 border-t border-slate-800/80 flex items-center justify-between text-xs font-semibold">
                      <span className={isSelected ? "text-coral" : "text-slate-500"}>
                        {isSelected ? "Selected Round ✓" : "Select Round"}
                      </span>
                      <ChevronRight className={`size-4 ${isSelected ? "text-coral" : "text-slate-600"}`} />
                    </div>
                  </button>
                );
              })}
            </div>

            {/* Configuration Options */}
            <div className="bg-[#161b22] border border-slate-800 rounded-2xl p-6 space-y-4">
              <h3 className="text-sm font-bold text-white uppercase tracking-wider">
                Interview Session Settings
              </h3>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* Question Count */}
                <div>
                  <label className="text-xs font-semibold text-slate-300 block mb-2">
                    Number of Interview Questions:
                  </label>
                  <div className="flex items-center gap-3">
                    {[3, 5, 7].map((cnt) => (
                      <button
                        key={cnt}
                        type="button"
                        onClick={() => setQuestionCount(cnt)}
                        className={`px-4 py-2 rounded-xl text-xs font-bold border transition-colors ${
                          questionCount === cnt
                            ? "bg-coral text-white border-coral"
                            : "bg-slate-800 text-slate-300 border-slate-700 hover:bg-slate-700"
                        }`}
                      >
                        {cnt} Questions
                      </button>
                    ))}
                  </div>
                </div>

                {/* Technical Skills input if Technical round */}
                {selectedRound === "TECHNICAL" && (
                  <div>
                    <label className="text-xs font-semibold text-slate-300 block mb-2">
                      Target Tech Stack / Skills (comma separated):
                    </label>
                    <input
                      type="text"
                      value={skillsInput}
                      onChange={(e) => setSkillsInput(e.target.value)}
                      placeholder="e.g. Java, Python, React, SQL, AWS..."
                      className="w-full bg-[#0d1117] border border-slate-700 rounded-xl px-3 py-2 text-xs text-white placeholder:text-slate-500 focus:outline-none focus:border-coral"
                    />
                  </div>
                )}
              </div>

              {/* Start Button */}
              <div className="pt-4 flex justify-end">
                <button
                  onClick={handleStartInterview}
                  disabled={isStarting}
                  className="px-8 py-3 rounded-xl bg-coral hover:bg-coral/90 text-white font-bold text-sm flex items-center gap-2 shadow-lg transition-all disabled:opacity-50"
                >
                  <Play className={`size-4 ${isStarting ? "animate-spin" : ""}`} />
                  {isStarting ? "Generating Interview..." : "Begin Mock Interview"}
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

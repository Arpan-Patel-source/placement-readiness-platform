import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import {
  FileText,
  Code2,
  Bot,
  Calculator,
  Users,
  Mic,
  AudioLines,
  BookOpen,
  BarChart3,
  Map,
  Target,
  ArrowRight,
  Upload,
  CheckCircle2,
  AlertTriangle,
  Sparkles,
  TrendingUp,
  ShieldAlert,
  HelpCircle,
} from "lucide-react";

import { SiteHeader } from "@/components/SiteHeader";
import {
  api,
  authStorage,
  ResumeAnalysisResult,
  ReadinessScoreResponse,
  RoadmapResponse,
  CompanyPredictionDto,
} from "@/lib/api";

export const Route = createFileRoute("/dashboard")({
  head: () => ({
    meta: [
      { title: "Student Dashboard — PlacementAI" },
      {
        name: "description",
        content:
          "Track resume ATS score, coding practice, aptitude, HR and mock interview progress with your placement readiness score and weekly roadmap.",
      },
      { property: "og:title", content: "Student Dashboard — PlacementAI" },
      {
        property: "og:description",
        content: "All your placement preparation modules, readiness score and personalised roadmap in one place.",
      },
    ],
  }),
  component: Dashboard,
});

const MODULE_DEFINITIONS = [
  {
    icon: FileText,
    title: "AI Resume Analyzer",
    text: "Upload, ATS score, skill gaps, keywords & suggestions",
    bg: "bg-mint",
    route: "/resume-analyzer",
    scoreKey: "resumeScore",
  },
  {
    icon: Code2,
    title: "Competitive Coding Arena",
    text: "Question bank, compiler, test cases, auto-evaluation",
    bg: "bg-sky",
    route: "/coding-arena",
    scoreKey: "codingScore",
  },
  {
    icon: Bot,
    title: "AI Coding Mentor",
    text: "Code analysis, optimization & complexity feedback",
    bg: "bg-blush",
    route: "/coding-arena",
    scoreKey: "codingScore",
  },
  {
    icon: Calculator,
    title: "Aptitude Training",
    text: "Quant, reasoning & verbal with scoring",
    bg: "bg-peach",
    route: "/aptitude",
    scoreKey: "aptitudeScore",
  },
  {
    icon: Users,
    title: "HR Training",
    text: "HR question sets with instant AI feedback",
    bg: "bg-sage",
    route: "/hr-training",
    scoreKey: "hrScore",
  },
  {
    icon: Mic,
    title: "AI Mock Interview",
    text: "HR, technical and coding interview rounds",
    bg: "bg-mint",
    route: "/mock-interview",
    scoreKey: "interviewScore",
  },
  {
    icon: AudioLines,
    title: "Voice-Based Interview",
    text: "Speech-to-text with fluency & confidence analysis",
    bg: "bg-sky",
    route: "/voice-interview",
    scoreKey: "interviewScore",
  },
  {
    icon: BookOpen,
    title: "Technical Training",
    text: "Personalised tracks, notes, MCQs & assignments",
    bg: "bg-blush",
    route: "/technical-training",
    scoreKey: "technicalScore",
  },
];

function Ring({ value }: { value: number }) {
  return (
    <div
      className="grid size-36 place-items-center rounded-full"
      style={{
        background: `conic-gradient(var(--coral) ${value * 3.6}deg, var(--muted) 0deg)`,
      }}
    >
      <div className="grid size-28 place-items-center rounded-full bg-card">
        <span className="font-display text-3xl font-extrabold text-ink">{Math.round(value)}</span>
        <span className="text-[0.6rem] tracking-[0.2em] text-muted-foreground">READY</span>
      </div>
    </div>
  );
}

function Bar({ value, className = "" }: { value: number; className?: string }) {
  const safeVal = Math.min(100, Math.max(0, value));
  return (
    <div className={`h-2 w-full overflow-hidden rounded-full bg-muted ${className}`}>
      <div className="h-full rounded-full bg-coral transition-all duration-500" style={{ width: `${safeVal}%` }} />
    </div>
  );
}

function Dashboard() {
  const [userName, setUserName] = useState("Student");
  const [latestResume, setLatestResume] = useState<ResumeAnalysisResult | null>(null);
  const [readinessData, setReadinessData] = useState<ReadinessScoreResponse | null>(null);
  const [roadmapData, setRoadmapData] = useState<RoadmapResponse | null>(null);
  const [companyPredictions, setCompanyPredictions] = useState<CompanyPredictionDto[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const user = authStorage.getUser();
    if (user?.name) {
      setUserName(user.name);
    }

    async function loadDashboardData() {
      setLoading(true);
      try {
        const [resumeRes, readinessRes, roadmapRes, predictionsRes] = await Promise.all([
          api.getLatestResumeAnalysis().catch(() => null),
          api.getReadinessScore().catch(() => null),
          api.getRoadmap().catch(() => null),
          api.getCompanyPredictions().catch(() => []),
        ]);

        if (resumeRes) setLatestResume(resumeRes);
        if (readinessRes) setReadinessData(readinessRes);
        if (roadmapRes) setRoadmapData(roadmapRes);
        if (predictionsRes && predictionsRes.length > 0) {
          setCompanyPredictions(predictionsRes);
        }
      } catch (err) {
        console.error("Dashboard fetch error:", err);
      } finally {
        setLoading(false);
      }
    }

    loadDashboardData();
  }, []);

  // 100% Dynamic Scores — No static numbers
  const overallScore = readinessData?.overallScore
    ? Math.round(readinessData.overallScore)
    : latestResume
    ? latestResume.readinessScore
    : 0;

  const atsScore = latestResume
    ? latestResume.atsScore
    : readinessData?.resumeScore
    ? Math.round(readinessData.resumeScore)
    : 0;

  const breakdownItems = [
    { label: "Resume ATS", value: atsScore },
    { label: "Coding Arena", value: readinessData ? Math.round(readinessData.codingScore) : 0 },
    { label: "Aptitude", value: readinessData ? Math.round(readinessData.aptitudeScore) : 0 },
    { label: "HR Training", value: readinessData ? Math.round(readinessData.hrScore) : 0 },
    { label: "Mock Interview", value: readinessData ? Math.round(readinessData.interviewScore) : 0 },
    { label: "Technical", value: readinessData ? Math.round(readinessData.technicalScore) : 0 },
  ];

  const getModuleTag = (title: string, score: number) => {
    switch (title) {
      case "AI Resume Analyzer":
        return atsScore > 0 ? `ATS ${atsScore}/100` : "Not scanned yet";
      case "Competitive Coding Arena":
        return `${readinessData?.solvedProblems || 0} Solved`;
      case "AI Coding Mentor":
        return `${readinessData?.totalCodingSubmissions || 0} Submissions`;
      case "Aptitude Training":
        return score > 0 ? `${score}% Score` : "0% Completed";
      case "HR Training":
        return score > 0 ? `${score}% Score` : "0% Completed";
      case "AI Mock Interview":
        return score > 0 ? `${score}% Avg` : "0 Rounds";
      case "Voice-Based Interview":
        return score > 0 ? `${score}% Fluency` : "0 Sessions";
      case "Technical Training":
        return score > 0 ? `${score}% Score` : "0% Completed";
      default:
        return `${score}%`;
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />

      <main className="mx-auto max-w-7xl px-5 py-8">
        {/* Dynamic Greeting */}
        <section className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <p className="text-xs font-semibold tracking-[0.22em] text-coral">YOUR DASHBOARD</p>
            <h1 className="mt-2 font-display text-3xl font-extrabold text-ink sm:text-4xl">
              Hey {userName}, you're <span className="text-coral">{overallScore}% placement ready</span>
            </h1>
            <p className="mt-2 max-w-xl text-sm text-ink/70">
              {readinessData?.readinessVerdict ? (
                <>
                  <span className="font-semibold text-ink">{readinessData.readinessVerdict}</span> · {readinessData.solvedProblems} coding problems solved.
                </>
              ) : latestResume ? (
                `Latest resume scan for ${latestResume.targetRole}: ATS score is ${atsScore}/100.`
              ) : (
                "Complete practice questions, coding problems, and mock interviews to dynamically increase your placement readiness score."
              )}
            </p>
          </div>
          <div className="flex items-center gap-3">
            <Link
              to="/coding-arena"
              className="inline-flex items-center gap-2 rounded-full bg-slate-900 px-4 py-2 text-xs font-semibold text-white shadow-sm transition-colors hover:bg-slate-800"
            >
              <Code2 className="size-3.5 text-coral" /> Practice Coding
            </Link>
            <Link
              to="/resume-analyzer"
              className="inline-flex items-center gap-2 rounded-full bg-coral px-4 py-2 text-xs font-semibold text-primary-foreground shadow-sm transition-colors hover:bg-coral/90"
            >
              <Upload className="size-3.5" /> {latestResume ? "Re-scan resume" : "Upload resume"}
            </Link>
          </div>
        </section>

        {/* Readiness + prediction */}
        <section className="mt-8 grid gap-5 lg:grid-cols-3">
          {/* Placement Readiness Ring Card */}
          <div className="rounded-3xl border border-border bg-card p-6 shadow-sm flex flex-col justify-between">
            <div>
              <div className="flex items-center justify-between">
                <h2 className="font-display text-lg font-bold text-ink">Placement Readiness</h2>
                <span className="text-[10px] font-bold px-2.5 py-0.5 rounded-full bg-coral/10 text-coral border border-coral/30">
                  {readinessData?.readinessVerdict || (overallScore > 0 ? "In Progress" : "Getting Started")}
                </span>
              </div>
              <div className="mt-5 flex items-center gap-6">
                <Ring value={overallScore} />
                <div className="flex-1 space-y-2.5">
                  {breakdownItems.map((r) => (
                    <div key={r.label}>
                      <div className="flex justify-between text-xs text-ink/70">
                        <span>{r.label}</span>
                        <span className="font-semibold text-ink">{r.value}%</span>
                      </div>
                      <Bar value={r.value} className="mt-1" />
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {readinessData?.weakAreas && readinessData.weakAreas.length > 0 && (
              <div className="mt-5 pt-4 border-t border-border/60">
                <span className="text-[11px] font-bold text-coral flex items-center gap-1">
                  <AlertTriangle className="size-3" /> Areas Requiring Practice:
                </span>
                <div className="flex flex-wrap gap-1.5 mt-1.5">
                  {readinessData.weakAreas.map((w, idx) => (
                    <span key={idx} className="text-[10px] px-2 py-0.5 rounded bg-peach/70 text-ink font-medium">
                      {w}
                    </span>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Resume Snapshot Card */}
          <div className="rounded-3xl border border-border bg-mint/50 p-6 flex flex-col justify-between">
            <div>
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <BarChart3 className="size-4 text-ink" />
                  <h2 className="font-display text-lg font-bold text-ink">Resume Snapshot</h2>
                </div>
                <Link
                  to="/resume-analyzer"
                  className="text-xs font-semibold text-coral hover:underline"
                >
                  Open Analyzer →
                </Link>
              </div>

              <p className="mt-4 font-display text-4xl font-extrabold text-ink">
                {atsScore}
                <span className="text-lg font-normal text-muted-foreground">/100</span>
              </p>
              <p className="text-xs text-ink/60">
                {latestResume
                  ? `ATS score (${latestResume.targetRole})`
                  : atsScore > 0
                  ? "ATS compatibility score"
                  : "No resume scanned yet"}
              </p>

              <div className="mt-5 space-y-2 text-sm">
                {latestResume ? (
                  <>
                    {latestResume.strengths.slice(0, 2).map((s, idx) => (
                      <p key={idx} className="flex items-start gap-2 text-ink/80 text-xs">
                        <CheckCircle2 className="mt-0.5 size-3.5 shrink-0 text-emerald-700" />
                        <span>{s}</span>
                      </p>
                    ))}
                    {latestResume.missingSkills.length > 0 && (
                      <p className="flex items-start gap-2 text-ink/80 text-xs">
                        <AlertTriangle className="mt-0.5 size-3.5 shrink-0 text-coral" />
                        <span>Missing: {latestResume.missingSkills.slice(0, 3).join(", ")}</span>
                      </p>
                    )}
                  </>
                ) : (
                  <div className="py-2 text-xs text-ink/70 space-y-2">
                    <p className="flex items-start gap-2">
                      <HelpCircle className="mt-0.5 size-3.5 shrink-0 text-coral" />
                      <span>Upload your resume to calculate your exact ATS score and identify role skill gaps.</span>
                    </p>
                    <Link
                      to="/resume-analyzer"
                      className="inline-block mt-2 font-bold text-xs text-coral hover:underline"
                    >
                      + Scan Resume Now
                    </Link>
                  </div>
                )}
              </div>
            </div>

            <div className="mt-5 flex flex-wrap gap-2">
              {(latestResume && latestResume.skillsFound.length > 0
                ? latestResume.skillsFound.slice(0, 5)
                : ["Java", "DSA", "SQL", "Spring Boot", "React"]
              ).map((s) => (
                <span key={s} className="rounded-full bg-card px-3 py-1 text-xs font-medium text-ink shadow-2xs">
                  {s}
                </span>
              ))}
            </div>
          </div>

          {/* Placement Prediction Card */}
          <div className="rounded-3xl border border-border bg-card p-6 shadow-sm flex flex-col justify-between">
            <div>
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Target className="size-4 text-coral" />
                  <h2 className="font-display text-lg font-bold text-ink">Placement Predictions</h2>
                </div>
                <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-slate-100 text-slate-600">
                  Dynamic Odds
                </span>
              </div>
              <p className="mt-1 text-xs text-ink/60">Real-time probabilities calculated from your active scores</p>

              <ul className="mt-4 space-y-3.5">
                {companyPredictions.slice(0, 4).map((c) => (
                  <li key={c.companyName}>
                    <div className="flex items-center justify-between text-xs">
                      <span className="font-bold text-ink">{c.companyName}</span>
                      <span className="font-bold text-coral">{Math.round(c.placementProbability)}%</span>
                    </div>
                    <Bar value={c.placementProbability} className="mt-1" />
                    <p className="mt-1 text-[11px] text-ink/60 line-clamp-1">{c.prepAdvice}</p>
                  </li>
                ))}
              </ul>
            </div>

            <div className="mt-4 pt-3 border-t border-border/60 flex items-center justify-between text-[11px]">
              <span className="text-muted-foreground">TCS, Infosys, Wipro, Amazon</span>
              <Link to="/coding-arena" className="text-coral font-bold hover:underline">
                Practice Company Sets →
              </Link>
            </div>
          </div>
        </section>

        {/* Modules Grid */}
        <section className="mt-12">
          <div className="flex items-center justify-between">
            <h2 className="font-display text-2xl font-extrabold text-ink">Your preparation modules</h2>
            <span className="text-xs text-muted-foreground font-semibold">8 Interactive Modules</span>
          </div>

          <div className="mt-5 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {MODULE_DEFINITIONS.map((m) => {
              const score =
                readinessData && (readinessData as any)[m.scoreKey] != null
                  ? Math.round((readinessData as any)[m.scoreKey])
                  : m.title === "AI Resume Analyzer" && latestResume
                  ? latestResume.atsScore
                  : 0;

              const tag = getModuleTag(m.title, score);

              return (
                <Link
                  to={m.route}
                  key={m.title}
                  className="group rounded-3xl border border-border bg-card p-5 shadow-sm transition-all hover:shadow-md hover:border-coral/40 flex flex-col justify-between"
                >
                  <div>
                    <div className={`grid size-11 place-items-center rounded-2xl ${m.bg}`}>
                      <m.icon className="size-5 text-ink" />
                    </div>
                    <h3 className="mt-4 font-display text-base font-bold text-ink">{m.title}</h3>
                    <p className="mt-1 text-xs leading-relaxed text-ink/65">{m.text}</p>
                  </div>

                  <div className="mt-4 pt-3 border-t border-border/40">
                    <div className="flex items-center justify-between text-xs text-ink/70">
                      <span className="font-medium text-[11px]">{tag}</span>
                      <span className="font-semibold text-ink">{score}%</span>
                    </div>
                    <Bar value={score} className="mt-1.5" />
                    <span className="mt-3 inline-flex items-center gap-1 text-xs font-semibold text-coral">
                      Open Module <ArrowRight className="size-3.5 transition-transform group-hover:translate-x-0.5" />
                    </span>
                  </div>
                </Link>
              );
            })}
          </div>
        </section>

        {/* Personalized 4-Week Roadmap */}
        <section className="mt-12 rounded-3xl border border-border bg-peach/40 p-6 sm:p-8">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Map className="size-5 text-ink" />
              <h2 className="font-display text-2xl font-extrabold text-ink">Personalized 4-Week Roadmap</h2>
            </div>
            <span className="text-xs px-3 py-1 rounded-full bg-card font-semibold text-coral shadow-2xs">
              AI Generated
            </span>
          </div>
          <p className="mt-2 text-sm text-ink/70">
            Dynamically prioritized from your weakest preparation areas to maximize placement probability.
          </p>

          <div className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-4">
            {roadmapData?.weeks?.map((w, idx) => (
              <div key={idx} className="rounded-2xl bg-card p-5 shadow-sm flex flex-col justify-between">
                <div>
                  <p className="text-[0.6rem] font-semibold tracking-[0.2em] text-coral">
                    {(w.week || `Week ${idx + 1}`).toUpperCase()}
                  </p>
                  <h3 className="mt-2 font-display text-base font-bold text-ink">{w.focus}</h3>
                  {w.rationale && (
                    <p className="mt-1 text-[11px] text-muted-foreground italic line-clamp-2">{w.rationale}</p>
                  )}
                  <ul className="mt-3 space-y-2 text-xs text-ink/70">
                    {w.tasks?.map((i: string, itemIdx: number) => (
                      <li key={itemIdx} className="flex items-start gap-2">
                        <span className="mt-1.5 size-1.5 shrink-0 rounded-full bg-coral" />
                        <span>{i}</span>
                      </li>
                    ))}
                  </ul>
                </div>

                <div className="mt-4 pt-3 border-t border-border/40 text-[11px] text-coral font-semibold">
                  Target: Week {idx + 1} Milestone →
                </div>
              </div>
            ))}
          </div>
        </section>

        <p className="mt-10 text-center text-xs text-muted-foreground">
          <Link to="/" className="hover:text-coral">← Back to home</Link>
        </p>
      </main>
    </div>
  );
}

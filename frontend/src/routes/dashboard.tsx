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
} from "lucide-react";

import { SiteHeader } from "@/components/SiteHeader";
import { api, authStorage, ResumeAnalysisResult } from "@/lib/api";


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

const modules = [
  {
    icon: FileText,
    title: "AI Resume Analyzer",
    text: "Upload, ATS score, skill gaps, keywords & suggestions",
    bg: "bg-mint",
    progress: 78,
    tag: "ATS 78/100",
  },
  {
    icon: Code2,
    title: "Competitive Coding Arena",
    text: "Question bank, compiler, test cases, auto-evaluation",
    bg: "bg-sky",
    progress: 62,
    tag: "124 solved",
  },
  {
    icon: Bot,
    title: "AI Coding Mentor",
    text: "Code analysis, optimization & complexity feedback",
    bg: "bg-blush",
    progress: 54,
    tag: "18 reviews",
  },
  {
    icon: Calculator,
    title: "Aptitude Training",
    text: "Quant, reasoning & verbal with scoring",
    bg: "bg-peach",
    progress: 71,
    tag: "Avg 71%",
  },
  {
    icon: Users,
    title: "HR Training",
    text: "HR question sets with instant AI feedback",
    bg: "bg-sage",
    progress: 45,
    tag: "9 answers",
  },
  {
    icon: Mic,
    title: "AI Mock Interview",
    text: "HR, technical and coding interview rounds",
    bg: "bg-mint",
    progress: 38,
    tag: "3 rounds",
  },
  {
    icon: AudioLines,
    title: "Voice-Based Interview",
    text: "Speech-to-text with fluency & confidence analysis",
    bg: "bg-sky",
    progress: 30,
    tag: "Fluency 6.8",
  },
  {
    icon: BookOpen,
    title: "Technical Training",
    text: "Personalised tracks, notes, MCQs & assignments",
    bg: "bg-blush",
    progress: 66,
    tag: "4 tracks",
  },
];

const readinessBreakdown = [
  { label: "Resume", value: 78 },
  { label: "Coding", value: 62 },
  { label: "Aptitude", value: 71 },
  { label: "HR", value: 45 },
  { label: "Interview", value: 38 },
  { label: "Technical", value: 66 },
];

const roadmap = [
  { week: "Week 1", focus: "Interview confidence", items: ["2 HR mock rounds", "Voice fluency drills", "STAR answer bank"] },
  { week: "Week 2", focus: "Coding depth", items: ["15 medium DP problems", "Complexity review with mentor", "1 timed contest"] },
  { week: "Week 3", focus: "Aptitude speed", items: ["Time & work sets", "Verbal reasoning daily 20", "Full mock aptitude test"] },
  { week: "Week 4", focus: "Company sprint", items: ["Resume ATS re-check", "TCS + Infosys pattern papers", "Full technical mock"] },
];

const companies = [
  { name: "TCS", chance: 82, note: "Aptitude aligned, keep coding steady" },
  { name: "Infosys", chance: 74, note: "Strengthen verbal reasoning" },
  { name: "Wipro", chance: 69, note: "More HR practice needed" },
  { name: "Amazon", chance: 41, note: "Focus on DSA + system basics" },
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
        <span className="font-display text-3xl font-extrabold text-ink">{value}</span>
        <span className="text-[0.6rem] tracking-[0.2em] text-muted-foreground">READY</span>
      </div>
    </div>
  );
}

function Bar({ value, className = "" }: { value: number; className?: string }) {
  return (
    <div className={`h-2 w-full overflow-hidden rounded-full bg-muted ${className}`}>
      <div className="h-full rounded-full bg-coral" style={{ width: `${value}%` }} />
    </div>
  );
}

function Dashboard() {
  const [userName, setUserName] = useState("Student");
  const [latestResume, setLatestResume] = useState<ResumeAnalysisResult | null>(null);

  useEffect(() => {
    const user = authStorage.getUser();
    if (user?.name) {
      setUserName(user.name);
    }

    api
      .getLatestResumeAnalysis()
      .then((data) => {
        if (data) setLatestResume(data);
      })
      .catch(() => {});
  }, []);

  const readinessScore = latestResume ? latestResume.readinessScore : 64;
  const atsScore = latestResume ? latestResume.atsScore : 78;

  const activeBreakdown = readinessBreakdown.map((r) =>
    r.label === "Resume" && latestResume ? { ...r, value: latestResume.atsScore } : r
  );

  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />

      <main className="mx-auto max-w-7xl px-5 py-8">
        {/* Greeting */}
        <section className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <p className="text-xs font-semibold tracking-[0.22em] text-coral">YOUR DASHBOARD</p>
            <h1 className="mt-2 font-display text-3xl font-extrabold text-ink sm:text-4xl">
              Hey {userName}, you're <span className="text-coral">{readinessScore}% placement ready</span>
            </h1>
            <p className="mt-2 max-w-xl text-sm text-ink/70">
              {latestResume
                ? `Latest resume scan for ${latestResume.targetRole}: ATS score is ${atsScore}/100.`
                : "Upload your resume to calculate your true ATS score and identify role skill gaps."}
            </p>
          </div>
          <Link
            to="/resume-analyzer"
            className="inline-flex items-center gap-2 rounded-full bg-coral px-5 py-2.5 text-sm font-semibold text-primary-foreground shadow-sm transition-colors hover:bg-coral/90"
          >
            <Upload className="size-4" /> {latestResume ? "Re-scan resume" : "Upload resume"}
          </Link>
        </section>

        {/* Readiness + prediction */}
        <section className="mt-8 grid gap-5 lg:grid-cols-3">
          <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
            <h2 className="font-display text-lg font-bold text-ink">Placement Readiness</h2>
            <div className="mt-5 flex items-center gap-6">
              <Ring value={readinessScore} />
              <div className="flex-1 space-y-3">
                {activeBreakdown.map((r) => (
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
                <span className="text-lg">/100</span>
              </p>
              <p className="text-xs text-ink/60">
                {latestResume
                  ? `ATS compatibility (${latestResume.targetRole})`
                  : "ATS compatibility score (Baseline sample)"}
              </p>

              <div className="mt-5 space-y-2 text-sm">
                {latestResume ? (
                  <>
                    {latestResume.strengths.slice(0, 1).map((s, idx) => (
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
                    {latestResume.actionableSuggestions.slice(0, 1).map((sug, idx) => (
                      <p key={idx} className="flex items-start gap-2 text-ink/80 text-xs">
                        <AlertTriangle className="mt-0.5 size-3.5 shrink-0 text-coral" />
                        <span className="line-clamp-2">{sug}</span>
                      </p>
                    ))}
                  </>
                ) : (
                  <>
                    <p className="flex items-start gap-2 text-ink/80 text-xs">
                      <CheckCircle2 className="mt-0.5 size-3.5 shrink-0 text-ink" /> Strong project section & clear formatting
                    </p>
                    <p className="flex items-start gap-2 text-ink/80 text-xs">
                      <AlertTriangle className="mt-0.5 size-3.5 shrink-0 text-coral" /> Missing keywords: REST API, Docker, SQL joins
                    </p>
                    <p className="flex items-start gap-2 text-ink/80 text-xs">
                      <AlertTriangle className="mt-0.5 size-3.5 shrink-0 text-coral" /> Add measurable impact to internship bullets
                    </p>
                  </>
                )}
              </div>
            </div>

            <div className="mt-5 flex flex-wrap gap-2">
              {(latestResume && latestResume.skillsFound.length > 0
                ? latestResume.skillsFound.slice(0, 5)
                : ["Java", "DSA", "SQL", "React", "Aptitude"]
              ).map((s) => (
                <span key={s} className="rounded-full bg-card px-3 py-1 text-xs font-medium text-ink shadow-2xs">
                  {s}
                </span>
              ))}
            </div>
          </div>

          <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
            <div className="flex items-center gap-2">
              <Target className="size-4 text-coral" />
              <h2 className="font-display text-lg font-bold text-ink">Placement Prediction</h2>
            </div>
            <p className="mt-2 text-xs text-ink/60">Company-specific chances & prep focus</p>
            <ul className="mt-5 space-y-4">
              {companies.map((c) => (
                <li key={c.name}>
                  <div className="flex items-center justify-between text-sm">
                    <span className="font-semibold text-ink">{c.name}</span>
                    <span className="text-ink/70">{c.chance}%</span>
                  </div>
                  <Bar value={c.chance} className="mt-1.5" />
                  <p className="mt-1 text-xs text-ink/60">{c.note}</p>
                </li>
              ))}
            </ul>
          </div>
        </section>

        {/* Modules */}
        <section className="mt-12">
          <h2 className="font-display text-2xl font-extrabold text-ink">Your preparation modules</h2>
          <div className="mt-5 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {modules.map((m) => {
              const linkTarget = m.title === "AI Resume Analyzer" ? "/resume-analyzer" : "/dashboard";
              return (
                <Link
                  to={linkTarget}
                  key={m.title}
                  className="group rounded-3xl border border-border bg-card p-5 shadow-sm transition-all hover:shadow-md hover:border-coral/40"
                >
                  <div className={`grid size-11 place-items-center rounded-2xl ${m.bg}`}>
                    <m.icon className="size-5 text-ink" />
                  </div>
                  <h3 className="mt-4 font-display text-base font-bold text-ink">{m.title}</h3>
                  <p className="mt-1 text-xs leading-relaxed text-ink/65">{m.text}</p>
                  <div className="mt-4 flex items-center justify-between text-xs text-ink/70">
                    <span>{m.title === "AI Resume Analyzer" && latestResume ? `ATS ${latestResume.atsScore}/100` : m.tag}</span>
                    <span className="font-semibold text-ink">
                      {m.title === "AI Resume Analyzer" && latestResume ? latestResume.readinessScore : m.progress}%
                    </span>
                  </div>
                  <Bar
                    value={m.title === "AI Resume Analyzer" && latestResume ? latestResume.readinessScore : m.progress}
                    className="mt-2"
                  />
                  <span className="mt-4 inline-flex items-center gap-1 text-xs font-semibold text-coral">
                    Open <ArrowRight className="size-3.5 transition-transform group-hover:translate-x-0.5" />
                  </span>
                </Link>
              );
            })}
          </div>
        </section>

        {/* Roadmap */}
        <section className="mt-12 rounded-3xl border border-border bg-peach/40 p-6 sm:p-8">
          <div className="flex items-center gap-2">
            <Map className="size-5 text-ink" />
            <h2 className="font-display text-2xl font-extrabold text-ink">Your 4-week roadmap</h2>
          </div>
          <p className="mt-2 text-sm text-ink/70">Generated from your weakest areas: interviews, HR answers and DSA depth.</p>
          <div className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-4">
            {roadmap.map((w) => (
              <div key={w.week} className="rounded-2xl bg-card p-5 shadow-sm">
                <p className="text-[0.6rem] font-semibold tracking-[0.2em] text-coral">{w.week.toUpperCase()}</p>
                <h3 className="mt-2 font-display text-base font-bold text-ink">{w.focus}</h3>
                <ul className="mt-3 space-y-2 text-xs text-ink/70">
                  {w.items.map((i) => (
                    <li key={i} className="flex items-start gap-2">
                      <span className="mt-1.5 size-1.5 shrink-0 rounded-full bg-coral" />
                      {i}
                    </li>
                  ))}
                </ul>
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

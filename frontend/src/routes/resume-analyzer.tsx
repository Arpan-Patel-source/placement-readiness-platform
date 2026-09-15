import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useEffect, useState, useRef, ChangeEvent, DragEvent } from "react";
import {
  FileText,
  Upload,
  CheckCircle2,
  AlertTriangle,
  Sparkles,
  ArrowRight,
  RefreshCw,
  Clock,
  ShieldCheck,
  Target,
  BarChart3,
  Layers,
  ChevronRight,
  Trash2,
  ExternalLink,
  Printer,
  Info,
} from "lucide-react";

import { SiteHeader } from "@/components/SiteHeader";
import { api, authStorage, ResumeAnalysisResult, ResumeHistoryItem } from "@/lib/api";

export const Route = createFileRoute("/resume-analyzer")({
  head: () => ({
    meta: [
      { title: "AI Resume Analyzer & ATS Checker — PlacementAI" },
      {
        name: "description",
        content:
          "Upload your PDF or DOCX resume to check ATS compatibility, identify skill gaps, detect missing keywords, and get AI-powered recommendations.",
      },
    ],
  }),
  component: ResumeAnalyzerPage,
});

const ROLE_OPTIONS = [
  "Java Backend Developer",
  "Full Stack Developer",
  "Frontend Developer",
  "Data Analyst",
  "Machine Learning Engineer",
  "DevOps Engineer",
  "Software Development Engineer",
];

function ScoreRing({
  value,
  label,
  colorClass = "bg-coral",
  gradientColor = "var(--coral)",
}: {
  value: number;
  label: string;
  colorClass?: string;
  gradientColor?: string;
}) {
  return (
    <div className="flex flex-col items-center">
      <div
        className="grid size-32 place-items-center rounded-full transition-transform hover:scale-105"
        style={{
          background: `conic-gradient(${gradientColor} ${value * 3.6}deg, var(--muted) 0deg)`,
        }}
      >
        <div className="grid size-24 place-items-center rounded-full bg-card shadow-inner">
          <span className="font-display text-2xl font-black text-ink">{value}%</span>
          <span className="text-[0.6rem] font-bold tracking-widest text-muted-foreground uppercase">
            {label}
          </span>
        </div>
      </div>
    </div>
  );
}

function SectionProgressBar({ label, score }: { label: string; score: number }) {
  const getBarColor = (s: number) => {
    if (s >= 80) return "bg-emerald-500";
    if (s >= 60) return "bg-coral";
    return "bg-amber-500";
  };

  return (
    <div className="space-y-1.5">
      <div className="flex justify-between text-xs">
        <span className="font-medium text-ink/80">{label}</span>
        <span className="font-bold text-ink">{score}/100</span>
      </div>
      <div className="h-2 w-full overflow-hidden rounded-full bg-muted">
        <div
          className={`h-full rounded-full transition-all duration-700 ${getBarColor(score)}`}
          style={{ width: `${score}%` }}
        />
      </div>
    </div>
  );
}

function ResumeAnalyzerPage() {
  const navigate = useNavigate();
  const fileInputRef = useRef<HTMLInputElement>(null);

  // States
  const [isUploadingNew, setIsUploadingNew] = useState(false);
  const [targetRole, setTargetRole] = useState("Java Backend Developer");
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [isDragging, setIsDragging] = useState(false);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysisStep, setAnalysisStep] = useState<string>("");
  const [currentAnalysis, setCurrentAnalysis] = useState<ResumeAnalysisResult | null>(null);
  const [history, setHistory] = useState<ResumeHistoryItem[]>([]);
  const [isLoadingHistory, setIsLoadingHistory] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<"overview" | "skills" | "suggestions" | "grammar">("overview");

  // Load existing profile & previous scan on mount
  useEffect(() => {
    if (!authStorage.isAuthenticated()) {
      return;
    }

    // Attempt to load student profile to pre-fill target role
    api
      .getProfile()
      .then((profile) => {
        if (profile?.targetRole) {
          setTargetRole(profile.targetRole);
        }
      })
      .catch(() => {
        // Not fatal if profile fetch fails
      });

    // Load latest scan
    api
      .getLatestResumeAnalysis()
      .then((latest) => {
        if (latest) {
          setCurrentAnalysis(latest);
        }
      })
      .catch(() => {});

    // Load history
    loadHistory();
  }, []);

  const loadHistory = () => {
    setIsLoadingHistory(true);
    api
      .getResumeHistory()
      .then((items) => setHistory(items))
      .catch(() => {})
      .finally(() => setIsLoadingHistory(false));
  };

  const handleFileSelect = (e: ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      validateAndSetFile(e.target.files[0]);
    }
  };

  const handleDragOver = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(false);
  };

  const handleDrop = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      validateAndSetFile(e.dataTransfer.files[0]);
    }
  };

  const validateAndSetFile = (file: File) => {
    setErrorMsg(null);
    const validExtensions = [".pdf", ".docx", ".txt"];
    const ext = file.name.substring(file.name.lastIndexOf(".")).toLowerCase();

    if (!validExtensions.includes(ext)) {
      setErrorMsg("Please upload a PDF (.pdf) or Word document (.docx)");
      return;
    }

    if (file.size > 10 * 1024 * 1024) {
      setErrorMsg("File size exceeds 10MB limit. Please upload a smaller document.");
      return;
    }

    setSelectedFile(file);
  };

  const handleStartAnalysis = async () => {
    if (!authStorage.isAuthenticated()) {
      navigate({ to: "/login" });
      return;
    }

    if (!selectedFile) {
      setErrorMsg("Please select a resume file first.");
      return;
    }

    setIsAnalyzing(true);
    setErrorMsg(null);

    // Multi-stage animation steps
    setAnalysisStep("Extracting text from resume...");
    const timer1 = setTimeout(() => setAnalysisStep("Evaluating ATS compatibility & section layout..."), 600);
    const timer2 = setTimeout(() => setAnalysisStep(`Benchmarking competencies for ${targetRole}...`), 1200);
    const timer3 = setTimeout(() => setAnalysisStep("Synthesizing AI improvement suggestions..."), 1800);

    try {
      const result = await api.analyzeResume(selectedFile, targetRole);
      setCurrentAnalysis(result);
      loadHistory();
      setSelectedFile(null);
      setIsUploadingNew(false);
    } catch (err: any) {
      setErrorMsg(err.message || "Failed to analyze resume. Please try again.");
    } finally {
      clearTimeout(timer1);
      clearTimeout(timer2);
      clearTimeout(timer3);
      setIsAnalyzing(false);
      setAnalysisStep("");
    }
  };

  const handleSelectHistory = async (id: string) => {
    try {
      const item = await api.getResumeById(id);
      setCurrentAnalysis(item);
      window.scrollTo({ top: 0, behavior: "smooth" });
    } catch (err: any) {
      setErrorMsg(err.message || "Could not load selected scan.");
    }
  };

  const handleDeleteHistory = async (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    if (!confirm("Are you sure you want to remove this scan from history?")) return;

    try {
      await api.deleteResumeAnalysis(id);
      const remaining = history.filter((h) => h.id !== id);
      setHistory(remaining);
      if (currentAnalysis?.id === id) {
        if (remaining.length > 0) {
          const nextScan = await api.getResumeById(remaining[0].id);
          setCurrentAnalysis(nextScan);
        } else {
          setCurrentAnalysis(null);
        }
      }
    } catch (err: any) {
      setErrorMsg(err.message || "Failed to delete scan.");
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />

      <main className="mx-auto max-w-7xl px-5 py-8">
        {/* Permanent file input so it is always accessible from any button */}
        <input
          ref={fileInputRef}
          type="file"
          accept=".pdf,.docx,.txt"
          className="hidden"
          onChange={handleFileSelect}
        />

        {/* Header Breadcrumbs */}
        <div className="flex items-center gap-2 text-xs text-muted-foreground">
          <Link to="/dashboard" className="transition-colors hover:text-coral">
            Dashboard
          </Link>
          <ChevronRight className="size-3" />
          <span className="font-semibold text-ink">AI Resume Analyzer</span>
        </div>

        {/* Hero Section */}
        <div className="mt-4 flex flex-wrap items-end justify-between gap-4 border-b border-border/70 pb-6">
          <div>
            <div className="inline-flex items-center gap-1.5 rounded-full bg-mint px-3 py-0.5 text-xs font-bold tracking-wide text-ink">
              <Sparkles className="size-3.5 text-coral" /> MODULE 2
            </div>
            <h1 className="mt-2 font-display text-3xl font-extrabold text-ink sm:text-4xl">
              AI Resume Analyzer & ATS Benchmark
            </h1>
            <p className="mt-1 max-w-2xl text-sm text-ink/70">
              Upload your resume to calculate your true ATS compatibility score, detect skill gaps
              for your target placement role, and receive instant actionable improvements.
            </p>
          </div>

          {currentAnalysis && (
            <div className="flex items-center gap-2">
              <button
                type="button"
                onClick={() => window.print()}
                className="inline-flex items-center gap-1.5 rounded-full border border-border bg-card px-4 py-2 text-xs font-semibold text-ink shadow-sm transition-colors hover:bg-muted cursor-pointer"
              >
                <Printer className="size-3.5" /> Print Report
              </button>
              <button
                type="button"
                onClick={() => {
                  setIsUploadingNew(true);
                  setSelectedFile(null);
                  if (fileInputRef.current) {
                    fileInputRef.current.value = "";
                    fileInputRef.current.click();
                  }
                }}
                className="inline-flex items-center gap-1.5 rounded-full bg-coral px-4 py-2 text-xs font-semibold text-primary-foreground shadow-sm transition-colors hover:bg-coral/90 cursor-pointer"
              >
                <Upload className="size-3.5" /> Upload New Version
              </button>
            </div>
          )}
        </div>

        {errorMsg && (
          <div className="mt-6 flex items-center justify-between rounded-2xl border border-destructive/30 bg-destructive/10 p-4 text-sm text-destructive">
            <div className="flex items-center gap-2">
              <AlertTriangle className="size-4 shrink-0" />
              <span>{errorMsg}</span>
            </div>
            <button onClick={() => setErrorMsg(null)} className="text-xs font-bold hover:underline">
              Dismiss
            </button>
          </div>
        )}

        {/* Upload Box (Visible if no current analysis OR when uploading a new one) */}
        {(!currentAnalysis || selectedFile || isUploadingNew) && (
          <div className="mt-8 rounded-3xl border border-border bg-card p-6 shadow-sm sm:p-8">
            <div className="flex flex-wrap items-center justify-between gap-3">
              <div>
                <h2 className="font-display text-xl font-bold text-ink">
                  {currentAnalysis ? "Upload a New Resume Version" : "Upload Your Resume for AI Scan"}
                </h2>
                <p className="text-xs text-ink/65">Supported formats: PDF (.pdf), Microsoft Word (.docx), or Text (.txt)</p>
              </div>
              {currentAnalysis && (
                <button
                  type="button"
                  onClick={() => {
                    setIsUploadingNew(false);
                    setSelectedFile(null);
                  }}
                  className="rounded-full border border-border bg-background px-4 py-1.5 text-xs font-semibold text-ink/80 transition-colors hover:bg-muted hover:text-coral cursor-pointer"
                >
                  ✕ Keep Current Report
                </button>
              )}
            </div>

            <div className="mt-6 grid gap-6 md:grid-cols-3">
              {/* Target Role Selector */}
              <div className="md:col-span-1 space-y-2">
                <label className="text-xs font-semibold uppercase tracking-wider text-ink/80">
                  Target Job Profile
                </label>
                <select
                  value={targetRole}
                  onChange={(e) => setTargetRole(e.target.value)}
                  className="w-full rounded-2xl border border-border bg-background px-3.5 py-2.5 text-sm font-medium text-ink focus:border-coral focus:outline-none focus:ring-1 focus:ring-coral"
                >
                  {ROLE_OPTIONS.map((role) => (
                    <option key={role} value={role}>
                      {role}
                    </option>
                  ))}
                </select>
                <p className="text-[0.7rem] text-ink/60">
                  We benchmark your resume keywords and competencies against industry standards for this position.
                </p>
              </div>

              {/* Drag and Drop Zone */}
              <div className="md:col-span-2">
                <div
                  onDragOver={handleDragOver}
                  onDragLeave={handleDragLeave}
                  onDrop={handleDrop}
                  onClick={() => fileInputRef.current?.click()}
                  className={`flex cursor-pointer flex-col items-center justify-center rounded-2xl border-2 border-dashed p-8 text-center transition-all ${
                    isDragging
                      ? "border-coral bg-coral/5 scale-[1.01]"
                      : "border-border hover:border-coral/60 hover:bg-muted/30"
                  }`}
                >
                  <div className="grid size-12 place-items-center rounded-2xl bg-peach/80 text-ink">
                    <Upload className="size-6 text-coral" />
                  </div>

                  {selectedFile ? (
                    <div className="mt-4">
                      <p className="text-sm font-bold text-ink">{selectedFile.name}</p>
                      <p className="text-xs text-ink/60">
                        {(selectedFile.size / 1024).toFixed(1)} KB — Ready to analyze
                      </p>
                    </div>
                  ) : (
                    <div className="mt-4">
                      <p className="text-sm font-semibold text-ink">
                        Drag and drop your resume file here, or{" "}
                        <span className="text-coral underline">browse files</span>
                      </p>
                      <p className="mt-1 text-xs text-ink/60">PDF or Word documents up to 10MB</p>
                    </div>
                  )}
                </div>

                {/* Submit Action */}
                {selectedFile && (
                  <div className="mt-4 flex items-center justify-end gap-3">
                    <button
                      type="button"
                      onClick={() => {
                        setSelectedFile(null);
                        if (fileInputRef.current) fileInputRef.current.value = "";
                        if (currentAnalysis) setIsUploadingNew(false);
                      }}
                      className="text-xs font-semibold text-ink/70 hover:text-ink cursor-pointer"
                    >
                      Clear
                    </button>
                    <button
                      type="button"
                      onClick={handleStartAnalysis}
                      disabled={isAnalyzing}
                      className="inline-flex items-center gap-2 rounded-full bg-coral px-6 py-2.5 text-sm font-bold text-primary-foreground shadow-sm transition-transform hover:bg-coral/90 active:scale-95 disabled:opacity-50 cursor-pointer"
                    >
                      {isAnalyzing ? (
                        <>
                          <RefreshCw className="size-4 animate-spin" /> Analyzing...
                        </>
                      ) : (
                        <>
                          <Sparkles className="size-4" /> Run Placement AI Scan
                        </>
                      )}
                    </button>
                  </div>
                )}
              </div>
            </div>

            {/* Live Progress Bar when scanning */}
            {isAnalyzing && (
              <div className="mt-6 rounded-2xl border border-coral/30 bg-coral/5 p-4 text-center">
                <div className="flex items-center justify-center gap-2 text-sm font-bold text-coral">
                  <RefreshCw className="size-4 animate-spin" />
                  <span>{analysisStep}</span>
                </div>
                <div className="mt-3 h-1.5 w-full overflow-hidden rounded-full bg-coral/20">
                  <div className="h-full w-2/3 animate-pulse rounded-full bg-coral" />
                </div>
              </div>
            )}
          </div>
        )}

        {/* Active Analysis Results Display */}
        {currentAnalysis && (
          <div className="mt-8 space-y-8">
            {/* Top Score Summary Cards */}
            <div className="grid gap-5 sm:grid-cols-3">
              {/* 1. Placement Readiness */}
              <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold tracking-widest text-coral uppercase">
                    OVERALL READINESS
                  </span>
                  <Target className="size-4 text-coral" />
                </div>
                <div className="mt-4 flex items-center justify-between">
                  <div>
                    <span className="font-display text-4xl font-extrabold text-ink">
                      {currentAnalysis.readinessScore}%
                    </span>
                    <p className="mt-1 text-xs text-ink/65">
                      {currentAnalysis.readinessScore >= 75
                        ? "Interview ready"
                        : currentAnalysis.readinessScore >= 55
                        ? "Good foundation — needs tuning"
                        : "Requires significant updates"}
                    </p>
                  </div>
                  <ScoreRing
                    value={currentAnalysis.readinessScore}
                    label="READY"
                    gradientColor="var(--coral)"
                  />
                </div>
              </div>

              {/* 2. ATS Compatibility */}
              <div className="rounded-3xl border border-border bg-mint/50 p-6 shadow-sm">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold tracking-widest text-emerald-800 uppercase">
                    ATS COMPATIBILITY
                  </span>
                  <ShieldCheck className="size-4 text-emerald-700" />
                </div>
                <div className="mt-4 flex items-center justify-between">
                  <div>
                    <span className="font-display text-4xl font-extrabold text-ink">
                      {currentAnalysis.atsScore}
                      <span className="text-lg text-ink/60">/100</span>
                    </span>
                    <p className="mt-1 text-xs text-ink/65">
                      {currentAnalysis.atsScore >= 80
                        ? "Passes standard ATS filters"
                        : "Formatting issues may block scan"}
                    </p>
                  </div>
                  <ScoreRing
                    value={currentAnalysis.atsScore}
                    label="ATS"
                    gradientColor="#059669"
                  />
                </div>
              </div>

              {/* 3. Resume Strength & Impact */}
              <div className="rounded-3xl border border-border bg-sky/50 p-6 shadow-sm">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold tracking-widest text-sky-900 uppercase">
                    RESUME STRENGTH
                  </span>
                  <BarChart3 className="size-4 text-sky-800" />
                </div>
                <div className="mt-4 flex items-center justify-between">
                  <div>
                    <span className="font-display text-4xl font-extrabold text-ink">
                      {currentAnalysis.strengthScore}
                      <span className="text-lg text-ink/60">/100</span>
                    </span>
                    <p className="mt-1 text-xs text-ink/65">
                      Action verbs & quantifiable metrics
                    </p>
                  </div>
                  <ScoreRing
                    value={currentAnalysis.strengthScore}
                    label="IMPACT"
                    gradientColor="#0284c7"
                  />
                </div>
              </div>
            </div>

            {/* Executive AI Evaluation Banner */}
            <div className="relative overflow-hidden rounded-3xl border border-border bg-gradient-to-r from-peach/40 via-mint/40 to-card p-6 sm:p-7 shadow-sm">
              <div className="flex items-start gap-4">
                <div className="grid size-10 shrink-0 place-items-center rounded-2xl bg-card shadow-sm">
                  <Sparkles className="size-5 text-coral" />
                </div>
                <div className="flex-1">
                  <div className="flex flex-wrap items-center justify-between gap-2">
                    <h2 className="font-display text-lg font-bold text-ink">
                      Placement AI Executive Feedback
                    </h2>
                    <span className="rounded-full bg-card px-3 py-1 text-xs font-medium text-ink shadow-xs">
                      Target Role: <strong>{currentAnalysis.targetRole}</strong>
                    </span>
                  </div>
                  <p className="mt-2 text-sm leading-relaxed text-ink/80">
                    {currentAnalysis.executiveSummary}
                  </p>
                  <p className="mt-3 text-[0.7rem] text-ink/50">
                    Analyzed file: {currentAnalysis.fileName} ·{" "}
                    {new Date(currentAnalysis.createdAt).toLocaleDateString(undefined, {
                      month: "short",
                      day: "numeric",
                      year: "numeric",
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </p>
                </div>
              </div>
            </div>

            {/* Detailed Tabs Navigation */}
            <div className="flex border-b border-border text-sm font-semibold">
              <button
                onClick={() => setActiveTab("overview")}
                className={`border-b-2 px-5 py-3 transition-colors ${
                  activeTab === "overview"
                    ? "border-coral text-coral"
                    : "border-transparent text-ink/65 hover:text-ink"
                }`}
              >
                Section Breakdown
              </button>
              <button
                onClick={() => setActiveTab("skills")}
                className={`flex items-center gap-2 border-b-2 px-5 py-3 transition-colors ${
                  activeTab === "skills"
                    ? "border-coral text-coral"
                    : "border-transparent text-ink/65 hover:text-ink"
                }`}
              >
                Skills & Keywords
                {currentAnalysis.missingSkills.length > 0 && (
                  <span className="rounded-full bg-coral/20 px-2 py-0.5 text-[0.65rem] font-bold text-coral">
                    {currentAnalysis.missingSkills.length} missing
                  </span>
                )}
              </button>
              <button
                onClick={() => setActiveTab("suggestions")}
                className={`flex items-center gap-2 border-b-2 px-5 py-3 transition-colors ${
                  activeTab === "suggestions"
                    ? "border-coral text-coral"
                    : "border-transparent text-ink/65 hover:text-ink"
                }`}
              >
                Actionable Recommendations
                <span className="rounded-full bg-muted px-2 py-0.5 text-[0.65rem] font-bold text-ink">
                  {currentAnalysis.actionableSuggestions.length}
                </span>
              </button>
              <button
                onClick={() => setActiveTab("grammar")}
                className={`border-b-2 px-5 py-3 transition-colors ${
                  activeTab === "grammar"
                    ? "border-coral text-coral"
                    : "border-transparent text-ink/65 hover:text-ink"
                }`}
              >
                Grammar & Formatting
              </button>
            </div>

            {/* Tab 1: Section Breakdown & Strengths */}
            {activeTab === "overview" && (
              <div className="grid gap-6 md:grid-cols-2">
                {/* Section Scores Card */}
                <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
                  <h3 className="font-display text-base font-bold text-ink">
                    ATS Section Evaluation
                  </h3>
                  <p className="mt-1 text-xs text-ink/60">
                    How well each core resume component satisfies automated recruiter filters:
                  </p>

                  <div className="mt-6 space-y-4">
                    <SectionProgressBar
                      label="Contact Details & Social Profiles (LinkedIn, GitHub)"
                      score={currentAnalysis.sections?.contactScore ?? 75}
                    />
                    <SectionProgressBar
                      label="Standard Section Hierarchy (Education, Projects, Skills)"
                      score={currentAnalysis.sections?.structureScore ?? 80}
                    />
                    <SectionProgressBar
                      label="Target Role Technical Skills Match"
                      score={currentAnalysis.sections?.skillsScore ?? 70}
                    />
                    <SectionProgressBar
                      label="Measurable Impact & Quantifiable Results"
                      score={currentAnalysis.sections?.impactScore ?? 65}
                    />
                  </div>
                </div>

                {/* Strengths Card */}
                <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
                  <div className="flex items-center gap-2">
                    <CheckCircle2 className="size-4 text-emerald-600" />
                    <h3 className="font-display text-base font-bold text-ink">What You Did Well</h3>
                  </div>
                  <p className="mt-1 text-xs text-ink/60">
                    Positive elements detected by the analyzer:
                  </p>

                  <ul className="mt-5 space-y-3">
                    {currentAnalysis.strengths.map((str, idx) => (
                      <li key={idx} className="flex items-start gap-3 text-xs leading-relaxed text-ink/80">
                        <span className="mt-0.5 size-1.5 shrink-0 rounded-full bg-emerald-500" />
                        <span>{str}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              </div>
            )}

            {/* Tab 2: Skills & Keywords */}
            {activeTab === "skills" && (
              <div className="space-y-6">
                {/* Missing Skills Warning */}
                {currentAnalysis.missingSkills.length > 0 && (
                  <div className="rounded-3xl border border-coral/30 bg-coral/5 p-6">
                    <div className="flex items-center gap-2 text-coral">
                      <AlertTriangle className="size-4 shrink-0" />
                      <h3 className="font-display text-base font-bold">
                        Missing Required Skills for {currentAnalysis.targetRole}
                      </h3>
                    </div>
                    <p className="mt-1 text-xs text-ink/70">
                      Standard job postings for this role expect to see these technical proficiencies:
                    </p>
                    <div className="mt-4 flex flex-wrap gap-2">
                      {currentAnalysis.missingSkills.map((skill) => (
                        <span
                          key={skill}
                          className="inline-flex items-center gap-1.5 rounded-full border border-coral/40 bg-card px-3.5 py-1 text-xs font-semibold text-coral shadow-xs"
                        >
                          <span className="size-1.5 rounded-full bg-coral" />
                          {skill}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                {/* Found Skills */}
                <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
                  <div className="flex items-center gap-2 text-emerald-700">
                    <CheckCircle2 className="size-4" />
                    <h3 className="font-display text-base font-bold text-ink">
                      Matched Skills ({currentAnalysis.skillsFound.length})
                    </h3>
                  </div>
                  <p className="mt-1 text-xs text-ink/60">
                    Skills successfully identified and parsed from your resume:
                  </p>
                  <div className="mt-4 flex flex-wrap gap-2">
                    {currentAnalysis.skillsFound.length > 0 ? (
                      currentAnalysis.skillsFound.map((skill) => (
                        <span
                          key={skill}
                          className="inline-flex items-center gap-1.5 rounded-full bg-mint px-3.5 py-1 text-xs font-medium text-ink"
                        >
                          <CheckCircle2 className="size-3 text-emerald-700" />
                          {skill}
                        </span>
                      ))
                    ) : (
                      <p className="text-xs text-muted-foreground">No specific skill keywords recognized.</p>
                    )}
                  </div>
                </div>

                {/* Missing Industry Keywords */}
                {currentAnalysis.criticalKeywords.length > 0 && (
                  <div className="rounded-3xl border border-border bg-sky/30 p-6 shadow-sm">
                    <div className="flex items-center gap-2 text-sky-900">
                      <Layers className="size-4" />
                      <h3 className="font-display text-base font-bold text-ink">
                        Recommended ATS Keywords to Weave In
                      </h3>
                    </div>
                    <p className="mt-1 text-xs text-ink/60">
                      ATS algorithms scan for these industry concepts in your project descriptions:
                    </p>
                    <div className="mt-4 flex flex-wrap gap-2">
                      {currentAnalysis.criticalKeywords.map((kw) => (
                        <span
                          key={kw}
                          className="rounded-full bg-card px-3.5 py-1 text-xs font-semibold text-sky-900 shadow-xs border border-sky-200"
                        >
                          + {kw}
                        </span>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* Tab 3: Actionable Suggestions */}
            {activeTab === "suggestions" && (
              <div className="rounded-3xl border border-border bg-card p-6 sm:p-8 shadow-sm">
                <div className="flex items-center gap-2">
                  <Sparkles className="size-5 text-coral" />
                  <h3 className="font-display text-lg font-bold text-ink">
                    Priority Recommendations to Boost Your Score
                  </h3>
                </div>
                <p className="mt-1 text-xs text-ink/60">
                  Implement these changes before submitting your resume to campus placement drives:
                </p>

                <div className="mt-6 space-y-4">
                  {currentAnalysis.actionableSuggestions.map((sug, idx) => (
                    <div
                      key={idx}
                      className="flex items-start gap-4 rounded-2xl border border-border bg-background/50 p-4 transition-all hover:bg-background"
                    >
                      <div className="grid size-7 shrink-0 place-items-center rounded-xl bg-peach text-xs font-bold text-ink">
                        {idx + 1}
                      </div>
                      <div className="text-xs sm:text-sm leading-relaxed text-ink/80">
                        {sug}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Tab 4: Grammar & Formatting */}
            {activeTab === "grammar" && (
              <div className="rounded-3xl border border-border bg-card p-6 sm:p-8 shadow-sm">
                <div className="flex items-center gap-2">
                  <Info className="size-5 text-ink" />
                  <h3 className="font-display text-lg font-bold text-ink">
                    Grammar, Voice & Professional Formatting
                  </h3>
                </div>
                <p className="mt-1 text-xs text-ink/60">
                  Checks for passive phrasing, pronoun usage, and recruiter readability standards:
                </p>

                <div className="mt-6 space-y-3">
                  {currentAnalysis.grammarSuggestions.map((item, idx) => (
                    <div
                      key={idx}
                      className="flex items-start gap-3 rounded-xl border border-border/80 bg-background/40 p-3.5 text-xs text-ink/80"
                    >
                      <AlertTriangle className="mt-0.5 size-4 shrink-0 text-amber-500" />
                      <span>{item}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {/* Previous Scan History Section */}
        <div className="mt-12 rounded-3xl border border-border bg-card p-6 sm:p-8 shadow-sm">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Clock className="size-4 text-coral" />
              <h3 className="font-display text-lg font-bold text-ink">Resume Scan History</h3>
            </div>
            <span className="text-xs text-ink/60">{history.length} scans recorded</span>
          </div>

          {isLoadingHistory ? (
            <p className="mt-4 text-xs text-muted-foreground">Loading history...</p>
          ) : history.length > 0 ? (
            <div className="mt-6 divide-y divide-border">
              {history.map((h) => (
                <div
                  key={h.id}
                  onClick={() => handleSelectHistory(h.id)}
                  className={`flex cursor-pointer items-center justify-between py-3.5 transition-colors hover:bg-muted/40 px-2 rounded-xl ${
                    currentAnalysis?.id === h.id ? "bg-peach/30 font-semibold" : ""
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <div className="grid size-8 place-items-center rounded-xl bg-mint">
                      <FileText className="size-4 text-ink" />
                    </div>
                    <div>
                      <p className="text-xs sm:text-sm font-bold text-ink">{h.fileName}</p>
                      <p className="text-[0.7rem] text-ink/60">
                        {h.targetRole} · {new Date(h.createdAt).toLocaleDateString()}
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center gap-4">
                    <div className="text-right">
                      <span className="text-xs font-extrabold text-ink">ATS: {h.atsScore}%</span>
                      <span className="ml-2 text-xs font-semibold text-coral">Ready: {h.readinessScore}%</span>
                    </div>
                    <button
                      onClick={(e) => handleDeleteHistory(h.id, e)}
                      title="Delete scan"
                      className="rounded-lg p-1.5 text-muted-foreground hover:bg-destructive/10 hover:text-destructive transition-colors"
                    >
                      <Trash2 className="size-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p className="mt-4 text-xs text-muted-foreground">
              No previous resume scans found. Upload your first resume above to begin tracking your readiness.
            </p>
          )}
        </div>
      </main>
    </div>
  );
}

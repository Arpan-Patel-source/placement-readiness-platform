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
  Copy,
  Check,
  Search,
  FileCheck,
  Briefcase,
  Cpu,
  Wand2,
  Sliders,
  Bot,
  Crosshair,
  Code2,
  GitBranch,
} from "lucide-react";

import { SiteHeader } from "@/components/SiteHeader";
import {
  api,
  authStorage,
  ResumeAnalysisResult,
  ResumeHistoryItem,
  BulletRewriteResponse,
  JdMatchResponse,
  CrossRoleComparisonResponse,
  AtsParsedTreeDto,
} from "@/lib/api";

export const Route = createFileRoute("/resume-analyzer")({
  head: () => ({
    meta: [
      { title: "AI Resume Analyzer & ATS Benchmark Studio — PlacementAI" },
      {
        name: "description",
        content:
          "Upload your resume to check real ATS compatibility, rewrite bullet points with the Google XYZ formula, match job descriptions, and inspect recruiter parser trees.",
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

  // High-level workspace view
  const [mainView, setMainView] = useState<
    "analysis" | "bullet-rewriter" | "jd-matcher" | "recruiter-bot" | "cross-role"
  >("analysis");

  // Core Analyzer States
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

  // ENHANCEMENT 1: Bullet Rewriter Studio States
  const [bulletInput, setBulletInput] = useState("");
  const [isRewritingBullet, setIsRewritingBullet] = useState(false);
  const [bulletResult, setBulletResult] = useState<BulletRewriteResponse | null>(null);
  const [copiedKey, setCopiedKey] = useState<string | null>(null);

  // ENHANCEMENT 2: Custom JD Matcher States
  const [jdText, setJdText] = useState("");
  const [jdCompany, setJdCompany] = useState("Bajaj Finserv");
  const [isMatchingJd, setIsMatchingJd] = useState(false);
  const [jdMatchResult, setJdMatchResult] = useState<JdMatchResponse | null>(null);

  // ENHANCEMENT 3: Recruiter Bot View States
  const [parsedTree, setParsedTree] = useState<AtsParsedTreeDto | null>(null);
  const [isLoadingTree, setIsLoadingTree] = useState(false);

  // ENHANCEMENT 4: Cross-Role Fit Matrix States
  const [crossRoleData, setCrossRoleData] = useState<CrossRoleComparisonResponse | null>(null);
  const [isLoadingCrossRole, setIsLoadingCrossRole] = useState(false);

  // Load profile & latest analysis
  useEffect(() => {
    if (!authStorage.isAuthenticated()) {
      return;
    }

    api
      .getProfile()
      .then((profile) => {
        if (profile?.targetRole) {
          setTargetRole(profile.targetRole);
        }
      })
      .catch(() => {});

    api
      .getLatestResumeAnalysis()
      .then((latest) => {
        if (latest) {
          setCurrentAnalysis(latest);
          // Pre-populate sample bullet if MindBridge is present
          setBulletInput(
            "Developed MindBridge, an AI-powered mental health platform, AI chatbot (Groq + Llama 3) with sentiment analysis, mood analytics, risk detection with SOS alerts, and GPS-based service locator using OpenStreetMap APIs; built using Spring Boot, Java, MySQL, REST APIs."
          );
        }
      })
      .catch(() => {});

    loadHistory();
  }, []);

  // When switching to Recruiter Bot View, auto-load parser tree if not loaded
  useEffect(() => {
    if (mainView === "recruiter-bot" && !parsedTree && currentAnalysis) {
      handleLoadParserTree(currentAnalysis.id);
    }
  }, [mainView, currentAnalysis]);

  // When switching to Cross-Role Matrix, auto-load comparison if not loaded
  useEffect(() => {
    if (mainView === "cross-role" && !crossRoleData && currentAnalysis) {
      handleLoadCrossRole(currentAnalysis.id);
    }
  }, [mainView, currentAnalysis]);

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

    setAnalysisStep("Extracting text from resume...");
    const timer1 = setTimeout(() => setAnalysisStep("Auditing real ATS keyword compatibility & section parsing..."), 600);
    const timer2 = setTimeout(() => setAnalysisStep(`Benchmarking competencies for ${targetRole}...`), 1200);

    try {
      const result = await api.analyzeResume(selectedFile, targetRole);
      setCurrentAnalysis(result);
      setIsUploadingNew(false);
      setSelectedFile(null);
      setParsedTree(null);
      setCrossRoleData(null);
      loadHistory();
    } catch (err: any) {
      setErrorMsg(err.message || "Failed to analyze resume. Please verify the document format.");
    } finally {
      clearTimeout(timer1);
      clearTimeout(timer2);
      setIsAnalyzing(false);
      setAnalysisStep("");
    }
  };

  const handleSelectHistory = async (id: string) => {
    try {
      const data = await api.getResumeById(id);
      setCurrentAnalysis(data);
      setIsUploadingNew(false);
      setParsedTree(null);
      setCrossRoleData(null);
    } catch (err: any) {
      setErrorMsg("Failed to load historical scan.");
    }
  };

  const handleDeleteHistory = async (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    try {
      await api.deleteResumeAnalysis(id);
      setHistory((prev) => prev.filter((item) => item.id !== id));
      if (currentAnalysis?.id === id) {
        const remaining = history.filter((item) => item.id !== id);
        if (remaining.length > 0) {
          handleSelectHistory(remaining[0].id);
        } else {
          setCurrentAnalysis(null);
        }
      }
    } catch (err: any) {
      setErrorMsg("Failed to delete resume scan.");
    }
  };

  const handlePrint = () => {
    window.print();
  };

  // ENHANCEMENT 1: Bullet Point Rewriter
  const handleRewriteBullet = async () => {
    if (!bulletInput.trim()) return;
    setIsRewritingBullet(true);
    try {
      const res = await api.rewriteBulletPoint({
        bulletText: bulletInput,
        roleTitle: targetRole,
      });
      setBulletResult(res);
    } catch (err: any) {
      setErrorMsg(err.message || "Failed to rewrite bullet point");
    } finally {
      setIsRewritingBullet(false);
    }
  };

  const handleCopyText = (key: string, text: string) => {
    navigator.clipboard.writeText(text);
    setCopiedKey(key);
    setTimeout(() => setCopiedKey(null), 2000);
  };

  // ENHANCEMENT 2: Custom JD Matcher
  const handleMatchJd = async () => {
    if (!jdText.trim()) return;
    setIsMatchingJd(true);
    try {
      const res = await api.matchJobDescription({
        jobDescriptionText: jdText,
        companyName: jdCompany,
        targetRole: targetRole,
        resumeId: currentAnalysis?.id,
      });
      setJdMatchResult(res);
    } catch (err: any) {
      setErrorMsg(err.message || "Failed to match job description");
    } finally {
      setIsMatchingJd(false);
    }
  };

  // ENHANCEMENT 3: Recruiter Bot Parser Inspector
  const handleLoadParserTree = async (resumeId?: string) => {
    setIsLoadingTree(true);
    try {
      const tree = await api.getAtsParsedTree(resumeId);
      setParsedTree(tree);
    } catch (err: any) {
      setErrorMsg("Failed to extract parser tree");
    } finally {
      setIsLoadingTree(false);
    }
  };

  // ENHANCEMENT 4: Cross-Role Fit Matrix
  const handleLoadCrossRole = async (resumeId?: string) => {
    setIsLoadingCrossRole(true);
    try {
      const matrix = await api.getCrossRoleComparison(resumeId);
      setCrossRoleData(matrix);
    } catch (err: any) {
      setErrorMsg("Failed to generate cross-role matrix");
    } finally {
      setIsLoadingCrossRole(false);
    }
  };

  return (
    <div className="min-h-screen bg-background text-foreground antialiased selection:bg-coral selection:text-white">
      <SiteHeader />

      {/* Permanently mounted hidden file input */}
      <input
        ref={fileInputRef}
        type="file"
        accept=".pdf,.docx,.txt"
        className="hidden"
        onChange={(e) => {
          handleFileSelect(e);
          if (currentAnalysis) {
            setIsUploadingNew(true);
          }
        }}
      />

      <main className="container mx-auto px-4 py-8 sm:px-6 lg:px-8 max-w-6xl">
        {/* Module Header */}
        <div className="mb-6 flex flex-wrap items-center justify-between gap-4 border-b border-border pb-6">
          <div>
            <div className="flex items-center gap-2">
              <span className="rounded-full bg-peach px-2.5 py-0.5 text-[0.65rem] font-bold tracking-wide text-ink uppercase">
                MODULE 2
              </span>
              <span className="text-xs font-semibold text-coral">Enterprise Placement Suite</span>
            </div>
            <h1 className="mt-1 font-display text-2xl sm:text-3xl font-black tracking-tight text-ink">
              AI Resume Analyzer & ATS Benchmark Studio
            </h1>
            <p className="mt-1 text-xs sm:text-sm text-ink/70">
              Calculate authentic ATS compatibility, rewrite project bullets with the Google XYZ formula, and match real company JDs.
            </p>
          </div>

          <div className="flex items-center gap-2">
            {currentAnalysis && (
              <>
                <button
                  onClick={handlePrint}
                  className="flex items-center gap-2 rounded-xl border border-border bg-card px-3.5 py-2 text-xs font-semibold text-ink shadow-xs transition-colors hover:bg-muted"
                >
                  <Printer className="size-3.5" />
                  Print Report
                </button>
                <button
                  onClick={() => {
                    setIsUploadingNew(true);
                    setSelectedFile(null);
                    setTimeout(() => fileInputRef.current?.click(), 50);
                  }}
                  className="flex items-center gap-2 rounded-xl bg-coral px-4 py-2 text-xs font-bold text-white shadow-sm transition-transform hover:scale-102"
                >
                  <Upload className="size-3.5" />
                  Upload New Version
                </button>
              </>
            )}
          </div>
        </div>

        {/* Global Error Banner */}
        {errorMsg && (
          <div className="mb-6 flex items-center justify-between rounded-2xl border border-destructive/20 bg-destructive/10 p-4 text-xs font-medium text-destructive">
            <div className="flex items-center gap-2">
              <AlertTriangle className="size-4 shrink-0" />
              <span>{errorMsg}</span>
            </div>
            <button
              onClick={() => setErrorMsg(null)}
              className="font-bold underline hover:opacity-80"
            >
              Dismiss
            </button>
          </div>
        )}

        {/* Studio Primary Navigation Switcher */}
        {currentAnalysis && !isUploadingNew && (
          <div className="mb-8 flex overflow-x-auto rounded-2xl border border-border bg-card p-1.5 shadow-xs">
            <button
              onClick={() => setMainView("analysis")}
              className={`flex shrink-0 items-center gap-2 rounded-xl px-4 py-2.5 text-xs font-bold transition-all ${
                mainView === "analysis"
                  ? "bg-coral text-white shadow-xs"
                  : "text-ink/70 hover:bg-muted hover:text-ink"
              }`}
            >
              <ShieldCheck className="size-4" />
              ATS Audit Report
            </button>
            <button
              onClick={() => setMainView("bullet-rewriter")}
              className={`flex shrink-0 items-center gap-2 rounded-xl px-4 py-2.5 text-xs font-bold transition-all ${
                mainView === "bullet-rewriter"
                  ? "bg-coral text-white shadow-xs"
                  : "text-ink/70 hover:bg-muted hover:text-ink"
              }`}
            >
              <Wand2 className="size-4" />
              AI Bullet Rewriter Studio
              <span className="rounded-full bg-peach px-2 py-0.5 text-[0.6rem] font-black text-ink">
                XYZ
              </span>
            </button>
            <button
              onClick={() => setMainView("jd-matcher")}
              className={`flex shrink-0 items-center gap-2 rounded-xl px-4 py-2.5 text-xs font-bold transition-all ${
                mainView === "jd-matcher"
                  ? "bg-coral text-white shadow-xs"
                  : "text-ink/70 hover:bg-muted hover:text-ink"
              }`}
            >
              <Target className="size-4" />
              Custom JD Matcher
              <span className="rounded-full bg-mint px-2 py-0.5 text-[0.6rem] font-black text-ink">
                Recruiter Fit
              </span>
            </button>
            <button
              onClick={() => setMainView("recruiter-bot")}
              className={`flex shrink-0 items-center gap-2 rounded-xl px-4 py-2.5 text-xs font-bold transition-all ${
                mainView === "recruiter-bot"
                  ? "bg-coral text-white shadow-xs"
                  : "text-ink/70 hover:bg-muted hover:text-ink"
              }`}
            >
              <Bot className="size-4" />
              Recruiter Bot View
              <span className="rounded-full bg-sky/50 px-2 py-0.5 text-[0.6rem] font-black text-sky-900">
                Parser Tree
              </span>
            </button>
            <button
              onClick={() => setMainView("cross-role")}
              className={`flex shrink-0 items-center gap-2 rounded-xl px-4 py-2.5 text-xs font-bold transition-all ${
                mainView === "cross-role"
                  ? "bg-coral text-white shadow-xs"
                  : "text-ink/70 hover:bg-muted hover:text-ink"
              }`}
            >
              <GitBranch className="size-4" />
              Cross-Role Fit Matrix
              <span className="rounded-full bg-peach px-2 py-0.5 text-[0.6rem] font-black text-ink">
                6 Tracks
              </span>
            </button>
          </div>
        )}

        {/* Upload State / Card */}
        {(!currentAnalysis || isUploadingNew) && (
          <div className="rounded-3xl border border-border bg-card p-6 sm:p-10 shadow-sm">
            <div className="mx-auto max-w-2xl text-center">
              <div className="mx-auto grid size-16 place-items-center rounded-3xl bg-peach/50 text-coral">
                <Upload className="size-8" />
              </div>
              <h2 className="mt-4 font-display text-xl sm:text-2xl font-black text-ink">
                Upload Your Resume for Real ATS Audit
              </h2>
              <p className="mt-1 text-xs sm:text-sm text-ink/70">
                Supported formats: PDF (.pdf), Microsoft Word (.docx), or Text (.txt)
              </p>
            </div>

            {/* Target Role Selector */}
            <div className="mx-auto mt-6 max-w-xl">
              <label className="block text-xs font-bold text-ink uppercase tracking-wider">
                Target Job Profile
              </label>
              <select
                value={targetRole}
                onChange={(e) => setTargetRole(e.target.value)}
                disabled={isAnalyzing}
                className="mt-1.5 w-full rounded-2xl border border-border bg-background px-4 py-3 text-sm font-semibold text-ink shadow-xs transition-colors focus:border-coral focus:outline-none"
              >
                {ROLE_OPTIONS.map((role) => (
                  <option key={role} value={role}>
                    {role}
                  </option>
                ))}
              </select>
              <p className="mt-1 text-[0.7rem] text-ink/60">
                We benchmark your resume keywords and competencies against industry standards for this position.
              </p>
            </div>

            {/* Drag & Drop Upload Zone */}
            <div className="mx-auto mt-6 max-w-xl">
              <div
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
                onDrop={handleDrop}
                onClick={() => fileInputRef.current?.click()}
                className={`relative flex cursor-pointer flex-col items-center justify-center rounded-3xl border-2 border-dashed p-8 text-center transition-all ${
                  isDragging
                    ? "border-coral bg-peach/20 scale-102"
                    : "border-border/80 bg-background/50 hover:border-coral/60 hover:bg-background"
                }`}
              >
                <div className="grid size-12 place-items-center rounded-2xl bg-muted text-ink/70">
                  <FileText className="size-6" />
                </div>
                <p className="mt-3 text-sm font-bold text-ink">
                  {selectedFile ? selectedFile.name : "Drag & drop your resume here"}
                </p>
                <p className="mt-1 text-xs text-ink/60">
                  {selectedFile
                    ? `${(selectedFile.size / 1024).toFixed(1)} KB — Ready to analyze`
                    : "or click to browse files from your computer"}
                </p>

                {selectedFile && (
                  <button
                    type="button"
                    onClick={(e) => {
                      e.stopPropagation();
                      setSelectedFile(null);
                    }}
                    className="mt-3 text-xs font-semibold text-coral underline hover:text-coral/80"
                  >
                    Clear
                  </button>
                )}
              </div>
            </div>

            {/* Actions */}
            <div className="mx-auto mt-6 flex max-w-xl items-center justify-center gap-3">
              {currentAnalysis && (
                <button
                  type="button"
                  onClick={() => {
                    setIsUploadingNew(false);
                    setSelectedFile(null);
                  }}
                  className="rounded-2xl border border-border px-6 py-3 text-xs font-bold text-ink hover:bg-muted"
                >
                  Cancel
                </button>
              )}
              <button
                type="button"
                onClick={handleStartAnalysis}
                disabled={!selectedFile || isAnalyzing}
                className="flex items-center gap-2 rounded-2xl bg-coral px-8 py-3 text-sm font-bold text-white shadow-md transition-all hover:opacity-95 disabled:pointer-events-none disabled:opacity-50"
              >
                {isAnalyzing ? (
                  <>
                    <RefreshCw className="size-4 animate-spin" />
                    Analyzing...
                  </>
                ) : (
                  <>
                    <Sparkles className="size-4" />
                    Run Placement AI Scan
                  </>
                )}
              </button>
            </div>

            {/* Loading Stepper Animation */}
            {isAnalyzing && (
              <div className="mx-auto mt-6 max-w-md rounded-2xl border border-border bg-card p-4 text-center">
                <div className="flex items-center justify-center gap-2 text-xs font-bold text-coral">
                  <RefreshCw className="size-3.5 animate-spin" />
                  <span>{analysisStep}</span>
                </div>
                <div className="mt-3 h-1.5 w-full overflow-hidden rounded-full bg-muted">
                  <div className="h-full w-2/3 animate-pulse rounded-full bg-coral" />
                </div>
              </div>
            )}
          </div>
        )}

        {/* ========================================================================= */}
        {/* VIEW 1: ATS AUDIT REPORT & BREAKDOWN */}
        {/* ========================================================================= */}
        {currentAnalysis && !isUploadingNew && mainView === "analysis" && (
          <div className="space-y-8">
            {/* Top Tri-Score Summary */}
            <div className="grid gap-6 md:grid-cols-3">
              {/* 1. Overall Readiness */}
              <div className="rounded-3xl border border-border bg-peach/40 p-6 shadow-sm">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold tracking-widest text-ink/70 uppercase">
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
                      {currentAnalysis.readinessScore >= 80
                        ? "Interview ready"
                        : currentAnalysis.readinessScore >= 60
                        ? "Competitive with recommended tuning"
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
                      {currentAnalysis.atsScore >= 85
                        ? "Exceptional — Passes top recruiter bots"
                        : currentAnalysis.atsScore >= 70
                        ? "Passes standard enterprise filters"
                        : "Deductions applied: Needs ATS adjustments"}
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

            {/* Tab 1: Section Breakdown */}
            {activeTab === "overview" && (
              <div className="grid gap-6 md:grid-cols-2">
                <div className="rounded-3xl border border-border bg-card p-6 sm:p-8 shadow-sm">
                  <h3 className="font-display text-base font-bold text-ink">
                    ATS Section Evaluation
                  </h3>
                  <p className="mt-1 text-xs text-ink/60">
                    How well each core resume component satisfies automated recruiter filters:
                  </p>
                  <div className="mt-6 space-y-4">
                    <SectionProgressBar
                      label="Contact Details & Social Profiles (LinkedIn, GitHub)"
                      score={currentAnalysis.sections.contactScore}
                    />
                    <SectionProgressBar
                      label="Standard Section Hierarchy (Education, Projects, Skills)"
                      score={currentAnalysis.sections.structureScore}
                    />
                    <SectionProgressBar
                      label="Target Role Technical Skills Match"
                      score={currentAnalysis.sections.skillsScore}
                    />
                    <SectionProgressBar
                      label="Measurable Impact & Quantifiable Results"
                      score={currentAnalysis.sections.impactScore}
                    />
                  </div>
                </div>

                <div className="rounded-3xl border border-border bg-card p-6 sm:p-8 shadow-sm">
                  <h3 className="font-display text-base font-bold text-ink">What You Did Well</h3>
                  <p className="mt-1 text-xs text-ink/60">
                    Positive elements detected by the analyzer:
                  </p>
                  <div className="mt-4 space-y-2.5">
                    {currentAnalysis.strengths.map((str, idx) => (
                      <div key={idx} className="flex items-start gap-2.5 text-xs text-ink/80">
                        <CheckCircle2 className="mt-0.5 size-4 shrink-0 text-emerald-600" />
                        <span>{str}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )}

            {/* Tab 2: Skills & Keywords */}
            {activeTab === "skills" && (
              <div className="space-y-6">
                {currentAnalysis.missingSkills.length > 0 && (
                  <div className="rounded-3xl border border-destructive/20 bg-destructive/5 p-6 shadow-sm">
                    <div className="flex items-center gap-2 text-destructive">
                      <AlertTriangle className="size-4" />
                      <h3 className="font-display text-base font-bold text-ink">
                        Missing Required Skills for {currentAnalysis.targetRole}
                      </h3>
                    </div>
                    <p className="mt-1 text-xs text-ink/60">
                      Standard industry job descriptions require these technologies:
                    </p>
                    <div className="mt-4 flex flex-wrap gap-2">
                      {currentAnalysis.missingSkills.map((skill) => (
                        <span
                          key={skill}
                          className="rounded-full bg-destructive/10 px-3.5 py-1 text-xs font-semibold text-destructive"
                        >
                          ✕ {skill}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
                  <h3 className="font-display text-base font-bold text-ink">
                    Detected Skills in Your Resume ({currentAnalysis.skillsFound.length})
                  </h3>
                  <p className="mt-1 text-xs text-ink/60">
                    Technologies and keywords matched against our catalog:
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

        {/* ========================================================================= */}
        {/* VIEW 2: AI BULLET REWRITER STUDIO (STAR / GOOGLE XYZ METHOD) */}
        {/* ========================================================================= */}
        {currentAnalysis && !isUploadingNew && mainView === "bullet-rewriter" && (
          <div className="space-y-8">
            <div className="rounded-3xl border border-border bg-card p-6 sm:p-8 shadow-sm">
              <div className="flex items-center gap-3">
                <div className="grid size-10 place-items-center rounded-2xl bg-peach text-coral">
                  <Wand2 className="size-5" />
                </div>
                <div>
                  <h2 className="font-display text-xl font-bold text-ink">
                    AI Bullet Point Rewriter Studio
                  </h2>
                  <p className="text-xs text-ink/70">
                    Transform weak or unquantified project descriptions into high-scoring Google XYZ bullets with 1-click copy.
                  </p>
                </div>
              </div>

              {/* Sample Loader Buttons */}
              <div className="mt-6">
                <span className="text-xs font-bold text-ink/70 uppercase tracking-wider">
                  Quick Load From Your Resume:
                </span>
                <div className="mt-2 flex flex-wrap gap-2">
                  <button
                    onClick={() =>
                      setBulletInput(
                        "Developed MindBridge, an AI-powered mental health platform, AI chatbot (Groq + Llama 3) with sentiment analysis, mood analytics, risk detection with SOS alerts, and GPS-based service locator using OpenStreetMap APIs; built using Spring Boot, Java, MySQL, REST APIs."
                      )
                    }
                    className="rounded-xl border border-border bg-background px-3 py-1.5 text-xs font-semibold text-ink hover:bg-peach/30"
                  >
                    MindBridge AI Platform
                  </button>
                  <button
                    onClick={() =>
                      setBulletInput(
                        "A web-based platform design to manage courses, attendance and marks with separate dashboard for learners, instructors and admin using Java, JSP and MySQL."
                      )
                    }
                    className="rounded-xl border border-border bg-background px-3 py-1.5 text-xs font-semibold text-ink hover:bg-peach/30"
                  >
                    Learning Path Dashboard
                  </button>
                  <button
                    onClick={() =>
                      setBulletInput(
                        "Helped build an e-commerce backend with Spring Boot and created APIs for user cart and orders."
                      )
                    }
                    className="rounded-xl border border-border bg-background px-3 py-1.5 text-xs font-semibold text-ink hover:bg-peach/30"
                  >
                    E-Commerce Backend
                  </button>
                </div>
              </div>

              {/* Textarea */}
              <div className="mt-4">
                <textarea
                  rows={4}
                  value={bulletInput}
                  onChange={(e) => setBulletInput(e.target.value)}
                  placeholder="Paste or type any existing project bullet point here..."
                  className="w-full rounded-2xl border border-border bg-background p-4 text-sm font-medium text-ink shadow-inner focus:border-coral focus:outline-none"
                />
              </div>

              <div className="mt-4 flex items-center justify-between">
                <span className="text-xs text-ink/60">
                  Target Role: <strong>{targetRole}</strong>
                </span>
                <button
                  onClick={handleRewriteBullet}
                  disabled={!bulletInput.trim() || isRewritingBullet}
                  className="flex items-center gap-2 rounded-2xl bg-coral px-6 py-2.5 text-xs font-bold text-white shadow-sm transition-transform hover:scale-102 disabled:opacity-50"
                >
                  {isRewritingBullet ? (
                    <>
                      <RefreshCw className="size-4 animate-spin" />
                      Generating High-Impact Bullets...
                    </>
                  ) : (
                    <>
                      <Sparkles className="size-4" />
                      Generate ATS-Optimized Bullets
                    </>
                  )}
                </button>
              </div>
            </div>

            {/* Generated Results */}
            {bulletResult && (
              <div className="space-y-6">
                <h3 className="font-display text-lg font-bold text-ink">
                  3 High-Impact Rewrite Formulations
                </h3>

                <div className="grid gap-6 md:grid-cols-3">
                  {/* Option 1: Quantified Google XYZ */}
                  <div className="flex flex-col justify-between rounded-3xl border border-coral/30 bg-peach/20 p-6 shadow-sm">
                    <div>
                      <div className="flex items-center justify-between">
                        <span className="rounded-full bg-coral px-2.5 py-0.5 text-[0.65rem] font-extrabold text-white uppercase tracking-wider">
                          RECOMMENDED
                        </span>
                        <span className="text-[0.7rem] font-bold text-coral">Google XYZ Method</span>
                      </div>
                      <h4 className="mt-3 font-display text-base font-bold text-ink">
                        {bulletResult.quantifiedXyz.title}
                      </h4>
                      <p className="mt-2 text-xs leading-relaxed text-ink/90 font-medium">
                        "{bulletResult.quantifiedXyz.text}"
                      </p>

                      <div className="mt-4 rounded-xl bg-card p-2.5 border border-border text-[0.7rem]">
                        <span className="font-bold text-coral">Metric Impact: </span>
                        <span className="text-ink/80">{bulletResult.quantifiedXyz.highlightMetric}</span>
                      </div>
                    </div>

                    <button
                      onClick={() => handleCopyText("xyz", bulletResult.quantifiedXyz.text)}
                      className="mt-6 flex w-full items-center justify-center gap-2 rounded-xl bg-card py-2 text-xs font-bold text-ink border border-border shadow-xs hover:bg-muted transition-colors"
                    >
                      {copiedKey === "xyz" ? (
                        <>
                          <Check className="size-3.5 text-emerald-600" />
                          <span className="text-emerald-600">Copied to Clipboard!</span>
                        </>
                      ) : (
                        <>
                          <Copy className="size-3.5" />
                          Copy Bullet Point
                        </>
                      )}
                    </button>
                  </div>

                  {/* Option 2: Enterprise Architecture */}
                  <div className="flex flex-col justify-between rounded-3xl border border-border bg-mint/30 p-6 shadow-sm">
                    <div>
                      <div className="flex items-center justify-between">
                        <span className="rounded-full bg-emerald-700 px-2.5 py-0.5 text-[0.65rem] font-extrabold text-white uppercase tracking-wider">
                          ENTERPRISE
                        </span>
                        <span className="text-[0.7rem] font-bold text-emerald-800">Architecture First</span>
                      </div>
                      <h4 className="mt-3 font-display text-base font-bold text-ink">
                        {bulletResult.enterpriseStack.title}
                      </h4>
                      <p className="mt-2 text-xs leading-relaxed text-ink/90 font-medium">
                        "{bulletResult.enterpriseStack.text}"
                      </p>

                      <div className="mt-4 rounded-xl bg-card p-2.5 border border-border text-[0.7rem]">
                        <span className="font-bold text-emerald-700">Tech Depth: </span>
                        <span className="text-ink/80">{bulletResult.enterpriseStack.highlightMetric}</span>
                      </div>
                    </div>

                    <button
                      onClick={() => handleCopyText("arch", bulletResult.enterpriseStack.text)}
                      className="mt-6 flex w-full items-center justify-center gap-2 rounded-xl bg-card py-2 text-xs font-bold text-ink border border-border shadow-xs hover:bg-muted transition-colors"
                    >
                      {copiedKey === "arch" ? (
                        <>
                          <Check className="size-3.5 text-emerald-600" />
                          <span className="text-emerald-600">Copied to Clipboard!</span>
                        </>
                      ) : (
                        <>
                          <Copy className="size-3.5" />
                          Copy Bullet Point
                        </>
                      )}
                    </button>
                  </div>

                  {/* Option 3: STAR Leadership */}
                  <div className="flex flex-col justify-between rounded-3xl border border-border bg-sky/30 p-6 shadow-sm">
                    <div>
                      <div className="flex items-center justify-between">
                        <span className="rounded-full bg-sky-800 px-2.5 py-0.5 text-[0.65rem] font-extrabold text-white uppercase tracking-wider">
                          LEADERSHIP
                        </span>
                        <span className="text-[0.7rem] font-bold text-sky-900">STAR Method</span>
                      </div>
                      <h4 className="mt-3 font-display text-base font-bold text-ink">
                        {bulletResult.leadershipImpact.title}
                      </h4>
                      <p className="mt-2 text-xs leading-relaxed text-ink/90 font-medium">
                        "{bulletResult.leadershipImpact.text}"
                      </p>

                      <div className="mt-4 rounded-xl bg-card p-2.5 border border-border text-[0.7rem]">
                        <span className="font-bold text-sky-800">Ownership: </span>
                        <span className="text-ink/80">{bulletResult.leadershipImpact.highlightMetric}</span>
                      </div>
                    </div>

                    <button
                      onClick={() => handleCopyText("lead", bulletResult.leadershipImpact.text)}
                      className="mt-6 flex w-full items-center justify-center gap-2 rounded-xl bg-card py-2 text-xs font-bold text-ink border border-border shadow-xs hover:bg-muted transition-colors"
                    >
                      {copiedKey === "lead" ? (
                        <>
                          <Check className="size-3.5 text-emerald-600" />
                          <span className="text-emerald-600">Copied to Clipboard!</span>
                        </>
                      ) : (
                        <>
                          <Copy className="size-3.5" />
                          Copy Bullet Point
                        </>
                      )}
                    </button>
                  </div>
                </div>

                {/* Improvements Breakdown */}
                <div className="rounded-2xl border border-border bg-card p-5 shadow-xs">
                  <span className="text-xs font-bold text-ink uppercase tracking-wider">
                    Why These Bullets Rank Higher in ATS & Recruiter Screenings:
                  </span>
                  <div className="mt-3 grid gap-2 sm:grid-cols-2 text-xs text-ink/80">
                    {bulletResult.improvementsApplied.map((imp, idx) => (
                      <div key={idx} className="flex items-center gap-2">
                        <CheckCircle2 className="size-3.5 text-emerald-600 shrink-0" />
                        <span>{imp}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* ========================================================================= */}
        {/* VIEW 3: CUSTOM JOB DESCRIPTION (JD) MATCHER */}
        {/* ========================================================================= */}
        {currentAnalysis && !isUploadingNew && mainView === "jd-matcher" && (
          <div className="space-y-8">
            <div className="rounded-3xl border border-border bg-card p-6 sm:p-8 shadow-sm">
              <div className="flex items-center gap-3">
                <div className="grid size-10 place-items-center rounded-2xl bg-mint text-emerald-800">
                  <Target className="size-5" />
                </div>
                <div>
                  <h2 className="font-display text-xl font-bold text-ink">
                    Custom Job Description (JD) Matcher
                  </h2>
                  <p className="text-xs text-ink/70">
                    Paste any recruiter job posting to compare keyword overlap, calculate JD match %, and detect missing criteria before applying.
                  </p>
                </div>
              </div>

              {/* Company and Role Input */}
              <div className="mt-6 grid gap-4 sm:grid-cols-2">
                <div>
                  <label className="block text-xs font-bold text-ink uppercase tracking-wider">
                    Target Company
                  </label>
                  <input
                    type="text"
                    value={jdCompany}
                    onChange={(e) => setJdCompany(e.target.value)}
                    placeholder="e.g. Bajaj Finserv, Google, Amazon"
                    className="mt-1.5 w-full rounded-2xl border border-border bg-background px-4 py-2.5 text-sm font-semibold text-ink shadow-xs focus:border-coral focus:outline-none"
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold text-ink uppercase tracking-wider">
                    Role Category
                  </label>
                  <select
                    value={targetRole}
                    onChange={(e) => setTargetRole(e.target.value)}
                    className="mt-1.5 w-full rounded-2xl border border-border bg-background px-4 py-2.5 text-sm font-semibold text-ink shadow-xs focus:border-coral focus:outline-none"
                  >
                    {ROLE_OPTIONS.map((r) => (
                      <option key={r} value={r}>
                        {r}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Sample JD Buttons */}
              <div className="mt-4">
                <span className="text-xs font-bold text-ink/70 uppercase tracking-wider">
                  Quick Load Sample Job Descriptions:
                </span>
                <div className="mt-2 flex flex-wrap gap-2">
                  <button
                    onClick={() => {
                      setJdCompany("Bajaj Finserv");
                      setJdText(
                        "We are seeking an experienced Java Backend Developer to build robust microservices for our financial platforms. Required: Java 17+, Spring Boot, Microservices, REST APIs, MySQL, PostgreSQL, Docker, Redis caching, Unit Testing with JUnit. Good to have: Kafka, AWS, CI/CD pipeline, Clean Architecture."
                      );
                    }}
                    className="rounded-xl border border-border bg-background px-3 py-1.5 text-xs font-semibold text-ink hover:bg-mint/30"
                  >
                    Bajaj Finserv — Java Backend
                  </button>
                  <button
                    onClick={() => {
                      setJdCompany("Amazon");
                      setJdText(
                        "Amazon is looking for a Software Development Engineer (SDE-1). You will design scalable distributed systems using Java or Python. Experience with Data Structures & Algorithms, Object-Oriented Programming, RESTful services, Docker, AWS, System Design, and automated unit testing."
                      );
                    }}
                    className="rounded-xl border border-border bg-background px-3 py-1.5 text-xs font-semibold text-ink hover:bg-mint/30"
                  >
                    Amazon — SDE-1
                  </button>
                  <button
                    onClick={() => {
                      setJdCompany("Fintech Unicorn");
                      setJdText(
                        "Full Stack / Backend Engineer wanted. Stack: Java, Spring Boot, React, TypeScript, Docker, Kubernetes, PostgreSQL, Redis caching, Event-driven architecture with Kafka, and CI/CD pipelines."
                      );
                    }}
                    className="rounded-xl border border-border bg-background px-3 py-1.5 text-xs font-semibold text-ink hover:bg-mint/30"
                  >
                    Fintech — Full Stack / Backend
                  </button>
                </div>
              </div>

              {/* JD Textarea */}
              <div className="mt-4">
                <textarea
                  rows={5}
                  value={jdText}
                  onChange={(e) => setJdText(e.target.value)}
                  placeholder="Paste the recruiter's complete job description here..."
                  className="w-full rounded-2xl border border-border bg-background p-4 text-sm font-medium text-ink shadow-inner focus:border-coral focus:outline-none"
                />
              </div>

              <div className="mt-4 flex items-center justify-between">
                <span className="text-xs text-ink/60">
                  Matching against: <strong>{currentAnalysis.fileName}</strong>
                </span>
                <button
                  onClick={handleMatchJd}
                  disabled={!jdText.trim() || isMatchingJd}
                  className="flex items-center gap-2 rounded-2xl bg-coral px-6 py-2.5 text-xs font-bold text-white shadow-sm transition-transform hover:scale-102 disabled:opacity-50"
                >
                  {isMatchingJd ? (
                    <>
                      <RefreshCw className="size-4 animate-spin" />
                      Auditing JD Overlap...
                    </>
                  ) : (
                    <>
                      <Target className="size-4" />
                      Benchmark Against This JD
                    </>
                  )}
                </button>
              </div>
            </div>

            {/* JD Match Results */}
            {jdMatchResult && (
              <div className="space-y-6">
                <div className="grid gap-6 md:grid-cols-3">
                  {/* Gauge Card */}
                  <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
                    <span className="text-xs font-bold text-ink uppercase tracking-wider">
                      JD COMPATIBILITY SCORE
                    </span>
                    <div className="mt-4 flex items-center justify-between">
                      <div>
                        <span className="font-display text-4xl font-black text-ink">
                          {jdMatchResult.matchScore}%
                        </span>
                        <p className="mt-1 text-xs font-semibold text-coral">
                          {jdMatchResult.matchVerdict}
                        </p>
                      </div>
                      <ScoreRing
                        value={jdMatchResult.matchScore}
                        label="MATCH"
                        gradientColor={jdMatchResult.matchScore >= 75 ? "#059669" : "var(--coral)"}
                      />
                    </div>
                  </div>

                  {/* Overlap Stats */}
                  <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
                    <span className="text-xs font-bold text-ink uppercase tracking-wider">
                      KEYWORD DENSITY RATIO
                    </span>
                    <div className="mt-4">
                      <span className="font-display text-3xl font-extrabold text-ink">
                        {jdMatchResult.totalJdKeywordsFound} / {jdMatchResult.totalJdKeywordsExtracted}
                      </span>
                      <p className="mt-1 text-xs text-ink/70">
                        Keywords extracted from {jdMatchResult.companyName}'s posting found in your resume.
                      </p>
                    </div>
                  </div>

                  {/* Action Checklist */}
                  <div className="rounded-3xl border border-border bg-peach/30 p-6 shadow-sm">
                    <span className="text-xs font-bold text-coral uppercase tracking-wider">
                      QUICK TAILORING ADVICE
                    </span>
                    <div className="mt-3 space-y-2 text-xs text-ink/80">
                      {jdMatchResult.tailoringTips.map((tip, idx) => (
                        <div key={idx} className="flex items-start gap-2">
                          <CheckCircle2 className="size-3.5 text-coral shrink-0 mt-0.5" />
                          <span>{tip}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>

                {/* Skills Comparison Lists */}
                <div className="grid gap-6 md:grid-cols-2">
                  {/* Matched Skills */}
                  <div className="rounded-3xl border border-border bg-card p-6 shadow-sm">
                    <h4 className="font-display text-sm font-bold text-ink">
                      Matched Skills in JD ({jdMatchResult.matchedSkills.length})
                    </h4>
                    <p className="mt-1 text-xs text-ink/60">
                      These requirements in the posting are satisfied by your resume:
                    </p>
                    <div className="mt-4 flex flex-wrap gap-2">
                      {jdMatchResult.matchedSkills.map((s) => (
                        <span
                          key={s}
                          className="inline-flex items-center gap-1.5 rounded-full bg-mint px-3 py-1 text-xs font-medium text-ink"
                        >
                          <CheckCircle2 className="size-3 text-emerald-700" />
                          {s}
                        </span>
                      ))}
                    </div>
                  </div>

                  {/* Missing Must-Have Skills */}
                  <div className="rounded-3xl border border-destructive/20 bg-destructive/5 p-6 shadow-sm">
                    <h4 className="font-display text-sm font-bold text-destructive">
                      Missing Must-Have Skills ({jdMatchResult.missingMustHaveSkills.length})
                    </h4>
                    <p className="mt-1 text-xs text-ink/60">
                      Recruiters at {jdMatchResult.companyName} filter candidates without these:
                    </p>
                    <div className="mt-4 flex flex-wrap gap-2">
                      {jdMatchResult.missingMustHaveSkills.length > 0 ? (
                        jdMatchResult.missingMustHaveSkills.map((s) => (
                          <span
                            key={s}
                            className="rounded-full bg-destructive/10 px-3 py-1 text-xs font-bold text-destructive"
                          >
                            ✕ {s}
                          </span>
                        ))
                      ) : (
                        <p className="text-xs text-emerald-700 font-semibold">
                          All core technical requirements matched!
                        </p>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* ========================================================================= */}
        {/* VIEW 4: RECRUITER BOT VIEW (ATS PARSER INSPECTOR) */}
        {/* ========================================================================= */}
        {currentAnalysis && !isUploadingNew && mainView === "recruiter-bot" && (
          <div className="space-y-8">
            <div className="rounded-3xl border border-border bg-card p-6 sm:p-8 shadow-sm">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="grid size-10 place-items-center rounded-2xl bg-sky/40 text-sky-900">
                    <Bot className="size-5" />
                  </div>
                  <div>
                    <h2 className="font-display text-xl font-bold text-ink">
                      ATS Recruiter Bot Inspector
                    </h2>
                    <p className="text-xs text-ink/70">
                      Transparency view: see exactly how automated enterprise parsers (Workday, Taleo, Ashby) extract and index your resume.
                    </p>
                  </div>
                </div>

                <button
                  onClick={() => handleLoadParserTree(currentAnalysis.id)}
                  disabled={isLoadingTree}
                  className="flex items-center gap-2 rounded-xl border border-border bg-background px-3.5 py-2 text-xs font-bold text-ink hover:bg-muted"
                >
                  <RefreshCw className={`size-3.5 ${isLoadingTree ? "animate-spin" : ""}`} />
                  Re-Parse
                </button>
              </div>

              {isLoadingTree ? (
                <div className="mt-8 text-center py-12">
                  <RefreshCw className="mx-auto size-6 animate-spin text-coral" />
                  <p className="mt-2 text-xs text-ink/60">Parsing document tree structure...</p>
                </div>
              ) : parsedTree ? (
                <div className="mt-8 space-y-6">
                  {/* Health Banner */}
                  <div
                    className={`rounded-2xl border p-4 flex items-center justify-between ${
                      parsedTree.health.status === "EXCELLENT" || parsedTree.health.status === "GOOD"
                        ? "border-emerald-500/30 bg-mint/30"
                        : "border-amber-500/30 bg-amber-500/10"
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <ShieldCheck
                        className={`size-5 ${
                          parsedTree.health.status === "EXCELLENT" || parsedTree.health.status === "GOOD"
                            ? "text-emerald-700"
                            : "text-amber-600"
                        }`}
                      />
                      <div>
                        <span className="text-xs font-bold text-ink">
                          Parser Health Status: <strong>{parsedTree.health.status}</strong>
                        </span>
                        <p className="text-[0.7rem] text-ink/70">
                          {parsedTree.health.totalWordCount} words parsed ·{" "}
                          {parsedTree.health.singlePageFit ? "Optimal single-page length" : "Length warning"} ·{" "}
                          {parsedTree.health.hasLegacyBiodataClutter
                            ? "Legacy bio-data detected"
                            : "Clean header"}
                        </p>
                      </div>
                    </div>
                  </div>

                  {/* Warning pills if any */}
                  {parsedTree.health.warnings.length > 0 && (
                    <div className="rounded-2xl border border-amber-500/20 bg-amber-500/5 p-4 space-y-2">
                      <span className="text-[0.65rem] font-bold tracking-wider text-amber-700 uppercase">
                        Parser Diagnostics & Warnings:
                      </span>
                      {parsedTree.health.warnings.map((w, idx) => (
                        <div key={idx} className="flex items-start gap-2 text-xs text-ink/80">
                          <AlertTriangle className="size-3.5 text-amber-600 shrink-0 mt-0.5" />
                          <span>{w}</span>
                        </div>
                      ))}
                    </div>
                  )}

                  {/* Extracted Identity Card */}
                  <div className="rounded-2xl border border-border bg-background/60 p-5">
                    <span className="text-xs font-bold text-ink/70 uppercase tracking-wider">
                      Parsed Identity & Contact Channel
                    </span>
                    <div className="mt-3 grid gap-3 sm:grid-cols-2 lg:grid-cols-3 text-xs">
                      <div>
                        <span className="text-ink/60">Candidate Name:</span>
                        <p className="font-bold text-ink">{parsedTree.candidateName}</p>
                      </div>
                      <div>
                        <span className="text-ink/60">Email:</span>
                        <p className="font-bold text-ink">{parsedTree.detectedEmail || "Not detected"}</p>
                      </div>
                      <div>
                        <span className="text-ink/60">Phone:</span>
                        <p className="font-bold text-ink">{parsedTree.detectedPhone || "Not detected"}</p>
                      </div>
                      <div>
                        <span className="text-ink/60">LinkedIn Profile:</span>
                        <p className="font-bold text-coral truncate">{parsedTree.detectedLinkedIn || "Not detected"}</p>
                      </div>
                      <div>
                        <span className="text-ink/60">GitHub Portfolio:</span>
                        <p className="font-bold text-coral truncate">{parsedTree.detectedGitHub || "Not detected"}</p>
                      </div>
                      <div>
                        <span className="text-ink/60">Location:</span>
                        <p className="font-bold text-ink">{parsedTree.detectedLocation}</p>
                      </div>
                    </div>
                  </div>

                  {/* Extracted Education */}
                  <div className="rounded-2xl border border-border bg-background/60 p-5">
                    <span className="text-xs font-bold text-ink/70 uppercase tracking-wider">
                      Parsed Education Tree
                    </span>
                    <div className="mt-3 grid gap-3 sm:grid-cols-2 text-xs">
                      <div>
                        <span className="text-ink/60">Recognized Degree:</span>
                        <p className="font-bold text-ink">{parsedTree.educationSummary.degree}</p>
                      </div>
                      <div>
                        <span className="text-ink/60">Institution:</span>
                        <p className="font-bold text-ink">{parsedTree.educationSummary.institution}</p>
                      </div>
                      <div>
                        <span className="text-ink/60">Graduation Timeline:</span>
                        <p className="font-bold text-ink">{parsedTree.educationSummary.gradYear}</p>
                      </div>
                      <div>
                        <span className="text-ink/60">Grade / CGPA:</span>
                        <p className="font-bold text-ink">{parsedTree.educationSummary.grade}</p>
                      </div>
                    </div>
                  </div>

                  {/* Parsed Projects */}
                  <div className="rounded-2xl border border-border bg-background/60 p-5">
                    <span className="text-xs font-bold text-ink/70 uppercase tracking-wider">
                      Parsed Projects & Deliverables ({parsedTree.parsedProjects.length})
                    </span>
                    <div className="mt-3 space-y-4">
                      {parsedTree.parsedProjects.map((p, idx) => (
                        <div key={idx} className="rounded-xl border border-border bg-card p-4">
                          <div className="flex flex-wrap items-center justify-between gap-2">
                            <h4 className="font-bold text-xs sm:text-sm text-ink">{p.title}</h4>
                            <span className="text-[0.7rem] text-ink/60">
                              {p.role} · {p.duration}
                            </span>
                          </div>
                          <ul className="mt-2 list-disc list-inside space-y-1 text-xs text-ink/80">
                            {p.bulletPoints.map((b, bIdx) => (
                              <li key={bIdx}>{b}</li>
                            ))}
                          </ul>
                          <div className="mt-3 flex gap-2">
                            <span
                              className={`rounded-full px-2 py-0.5 text-[0.65rem] font-bold ${
                                p.hasMetrics ? "bg-mint text-emerald-800" : "bg-muted text-ink/60"
                              }`}
                            >
                              {p.hasMetrics ? "✓ Quantified Metrics Found" : "✕ Zero Metrics Found"}
                            </span>
                            <span
                              className={`rounded-full px-2 py-0.5 text-[0.65rem] font-bold ${
                                p.hasLiveUrl ? "bg-mint text-emerald-800" : "bg-muted text-ink/60"
                              }`}
                            >
                              {p.hasLiveUrl ? "✓ Live Repository Linked" : "✕ No Direct Link"}
                            </span>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              ) : null}
            </div>
          </div>
        )}

        {/* ========================================================================= */}
        {/* VIEW 5: CROSS-ROLE READINESS BENCHMARK MATRIX */}
        {/* ========================================================================= */}
        {currentAnalysis && !isUploadingNew && mainView === "cross-role" && (
          <div className="space-y-8">
            <div className="rounded-3xl border border-border bg-card p-6 sm:p-8 shadow-sm">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="grid size-10 place-items-center rounded-2xl bg-peach text-coral">
                    <GitBranch className="size-5" />
                  </div>
                  <div>
                    <h2 className="font-display text-xl font-bold text-ink">
                      Cross-Role Readiness Benchmark
                    </h2>
                    <p className="text-xs text-ink/70">
                      See how your resume stacks up across all 6 core tech tracks to discover your strongest placement opportunities.
                    </p>
                  </div>
                </div>

                <button
                  onClick={() => handleLoadCrossRole(currentAnalysis.id)}
                  disabled={isLoadingCrossRole}
                  className="flex items-center gap-2 rounded-xl border border-border bg-background px-3.5 py-2 text-xs font-bold text-ink hover:bg-muted"
                >
                  <RefreshCw className={`size-3.5 ${isLoadingCrossRole ? "animate-spin" : ""}`} />
                  Re-Score
                </button>
              </div>

              {isLoadingCrossRole ? (
                <div className="mt-8 text-center py-12">
                  <RefreshCw className="mx-auto size-6 animate-spin text-coral" />
                  <p className="mt-2 text-xs text-ink/60">Evaluating across 6 engineering tracks...</p>
                </div>
              ) : crossRoleData ? (
                <div className="mt-8 grid gap-6 md:grid-cols-2 lg:grid-cols-3">
                  {crossRoleData.roleScores.map((item, idx) => (
                    <div
                      key={idx}
                      className={`flex flex-col justify-between rounded-3xl border p-6 shadow-sm transition-transform hover:scale-101 ${
                        item.suitabilityBadge === "Strong Fit"
                          ? "border-emerald-500/40 bg-mint/20"
                          : item.suitabilityBadge === "Good Fit"
                          ? "border-coral/30 bg-peach/20"
                          : "border-border bg-card"
                      }`}
                    >
                      <div>
                        <div className="flex items-center justify-between">
                          <span
                            className={`rounded-full px-2.5 py-0.5 text-[0.65rem] font-bold uppercase tracking-wider ${
                              item.suitabilityBadge === "Strong Fit"
                                ? "bg-emerald-700 text-white"
                                : item.suitabilityBadge === "Good Fit"
                                ? "bg-coral text-white"
                                : "bg-muted text-ink/70"
                            }`}
                          >
                            {item.suitabilityBadge}
                          </span>
                          <span className="text-xs font-black text-ink">
                            ATS: {item.atsScore}%
                          </span>
                        </div>

                        <h4 className="mt-3 font-display text-base font-extrabold text-ink">
                          {item.roleTitle}
                        </h4>

                        <div className="mt-3 flex items-center gap-3">
                          <div>
                            <span className="text-[0.65rem] text-ink/60 uppercase font-bold">
                              Core Skills Matched
                            </span>
                            <p className="font-extrabold text-sm text-ink">
                              {item.matchedSkillCount} / {item.totalPrimarySkillCount}
                            </p>
                          </div>
                          <div>
                            <span className="text-[0.65rem] text-ink/60 uppercase font-bold">
                              Readiness Score
                            </span>
                            <p className="font-extrabold text-sm text-coral">
                              {item.readinessScore}%
                            </p>
                          </div>
                        </div>

                        {item.topMissingSkills.length > 0 && (
                          <div className="mt-4">
                            <span className="text-[0.65rem] text-ink/60 font-bold uppercase">
                              Key Skills to Add:
                            </span>
                            <div className="mt-1 flex flex-wrap gap-1">
                              {item.topMissingSkills.map((m) => (
                                <span
                                  key={m}
                                  className="rounded-md bg-background px-2 py-0.5 text-[0.65rem] font-semibold text-destructive border border-destructive/20"
                                >
                                  {m}
                                </span>
                              ))}
                            </div>
                          </div>
                        )}

                        <p className="mt-4 text-xs text-ink/70 italic leading-relaxed">
                          "{item.transitionAdvice}"
                        </p>
                      </div>

                      <button
                        onClick={() => {
                          setTargetRole(item.roleTitle);
                          setMainView("analysis");
                          handleStartAnalysis();
                        }}
                        className="mt-6 flex w-full items-center justify-center gap-1.5 rounded-xl bg-card py-2 text-xs font-bold text-ink border border-border shadow-xs hover:bg-muted"
                      >
                        <span>Audit for this Role</span>
                        <ArrowRight className="size-3.5" />
                      </button>
                    </div>
                  ))}
                </div>
              ) : null}
            </div>
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

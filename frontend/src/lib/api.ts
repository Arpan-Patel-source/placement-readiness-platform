// API Service for PlacementAI Spring Boot Backend
export function getApiBaseUrl(): string {
  if (typeof window !== "undefined") {
    const custom = localStorage.getItem("placement_custom_api_url");
    if (custom && custom.trim()) {
      return custom.trim().replace(/\/+$/, "");
    }
  }
  const envObj = import.meta.env as Record<string, string | undefined>;
  const envUrl = (envObj["VITE_API_URL"] || "").trim();
  return envUrl.replace(/\/+$/, "");
}

export function setCustomApiUrl(url: string): void {
  if (typeof window === "undefined") return;
  if (!url || !url.trim()) {
    localStorage.removeItem("placement_custom_api_url");
  } else {
    localStorage.setItem("placement_custom_api_url", url.trim().replace(/\/+$/, ""));
  }
}

// Global fallback for any `${API_BASE_URL}/...` legacy usages
export const API_BASE_URL = "";

// Timeout helper: 45s to accommodate Render free-tier cold boot
const DEFAULT_API_TIMEOUT_MS = 45000;

export async function apiFetch(
  endpointOrUrl: string,
  init?: RequestInit,
  timeoutMs: number = DEFAULT_API_TIMEOUT_MS
): Promise<Response> {
  const base = getApiBaseUrl();
  let url = endpointOrUrl;
  if (!endpointOrUrl.startsWith("http://") && !endpointOrUrl.startsWith("https://")) {
    const cleanPath = endpointOrUrl.startsWith("/") ? endpointOrUrl : `/${endpointOrUrl}`;
    url = `${base}${cleanPath}`;
  }

  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), timeoutMs);

  try {
    const response = await fetch(url, {
      ...init,
      signal: controller.signal,
    });
    return response;
  } catch (err: any) {
    if (err.name === "AbortError") {
      throw new Error(
        "Request timed out. The backend server may still be waking up (Render free tier can take 40–60s on cold start). Please try again or switch to Demo Mode."
      );
    }
    throw err;
  } finally {
    clearTimeout(timer);
  }
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  email: string;
  token: string;
  newPassword: string;
}

export interface ForgotPasswordResponse {
  message: string;
  resetToken?: string;
  expiresInMinutes?: number;
}


export interface AuthResponse {
  token: string;
  email: string;
  role: string;
}

export interface StudentProfile {
  id?: number;
  fullName?: string;
  phone?: string;
  targetRole?: string;
  collegeName?: string;
  degree?: string;
  branch?: string;
  graduationYear?: number;
  cgpa?: number;
  skills?: string;
  resumeUrl?: string;
  linkedinUrl?: string;
  githubUrl?: string;
}

export interface SectionScores {
  contactScore: number;
  structureScore: number;
  skillsScore: number;
  impactScore: number;
}

export interface ResumeAnalysisResult {
  id: string;
  fileName: string;
  fileType: string;
  fileSizeBytes: number;
  targetRole: string;
  atsScore: number;
  strengthScore: number;
  readinessScore: number;
  sections: SectionScores;
  skillsFound: string[];
  missingSkills: string[];
  criticalKeywords: string[];
  actionableSuggestions: string[];
  grammarSuggestions: string[];
  strengths: string[];
  executiveSummary: string;
  createdAt: string;
}

export interface ResumeHistoryItem {
  id: string;
  fileName: string;
  targetRole: string;
  atsScore: number;
  strengthScore: number;
  readinessScore: number;
  createdAt: string;
}

export interface BulletRewriteRequest {
  bulletText: string;
  roleTitle?: string;
  targetMetric?: string;
  projectContext?: string;
}

export interface RewriteOption {
  title: string;
  text: string;
  formula: string;
  highlightMetric: string;
  keywordsEmbedded: string[];
}

export interface BulletRewriteResponse {
  originalBullet: string;
  quantifiedXyz: RewriteOption;
  enterpriseStack: RewriteOption;
  leadershipImpact: RewriteOption;
  improvementsApplied: string[];
}

export interface JdMatchRequest {
  resumeId?: string;
  resumeText?: string;
  jobDescriptionText: string;
  targetRole?: string;
  companyName?: string;
}

export interface JdMatchResponse {
  matchScore: number;
  matchVerdict: string;
  companyName: string;
  targetRole: string;
  totalJdKeywordsFound: number;
  totalJdKeywordsExtracted: number;
  matchedSkills: string[];
  missingMustHaveSkills: string[];
  missingGoodToHaveKeywords: string[];
  tailoringTips: string[];
}

export interface RoleScoreItem {
  roleTitle: string;
  atsScore: number;
  readinessScore: number;
  matchedSkillCount: number;
  totalPrimarySkillCount: number;
  matchedSkills: string[];
  topMissingSkills: string[];
  suitabilityBadge: string;
  transitionAdvice: string;
}

export interface CrossRoleComparisonResponse {
  candidateName: string;
  primaryRoleAnalyzed: string;
  roleScores: RoleScoreItem[];
}

export interface ParsedEducation {
  degree: string;
  institution: string;
  gradYear: string;
  grade: string;
}

export interface ParsedProject {
  title: string;
  role: string;
  duration: string;
  bulletPoints: string[];
  hasLiveUrl: boolean;
  hasMetrics: boolean;
}

export interface SkillCategory {
  categoryName: string;
  skills: string[];
}

export interface ParserHealth {
  status: "EXCELLENT" | "GOOD" | "WARNING" | "POOR";
  totalWordCount: number;
  singlePageFit: boolean;
  hasUnparseableCharacters: boolean;
  hasLegacyBiodataClutter: boolean;
  warnings: string[];
}

export interface AtsParsedTreeDto {
  candidateName: string;
  detectedEmail: string;
  detectedPhone: string;
  detectedLinkedIn: string;
  detectedGitHub: string;
  detectedLocation: string;
  educationSummary: ParsedEducation;
  parsedProjects: ParsedProject[];
  skillCategories: SkillCategory[];
  health: ParserHealth;
  rawTextSample: string;
}

export interface UserSession {
  email: string;
  name: string;
  role: string;
  token: string;
}

const TOKEN_KEY = "placement_token";
const USER_KEY = "placement_user";

export const authStorage = {
  getToken(): string | null {
    if (typeof window === "undefined") return null;
    return localStorage.getItem(TOKEN_KEY);
  },

  getUser(): UserSession | null {
    if (typeof window === "undefined") return null;
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw);
    } catch {
      return null;
    }
  },

  setSession(data: AuthResponse, name?: string): void {
    if (typeof window === "undefined") return;
    localStorage.setItem(TOKEN_KEY, data.token);
    const fallbackName = data.email ? data.email.split("@")[0] : "Student";
    const session: UserSession = {
      token: data.token,
      email: data.email,
      role: data.role,
      name: name || fallbackName || "Student",
    };
    localStorage.setItem(USER_KEY, JSON.stringify(session));

  },


  clearSession(): void {
    if (typeof window === "undefined") return;
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },

  isAuthenticated(): boolean {
    return !!this.getToken();
  },
};

async function handleResponse<T>(response: Response): Promise<T> {
  if (response.status === 204) {
    return null as T;
  }
  const contentType = response.headers.get("content-type") || "";
  const isJson = contentType.includes("application/json");
  const data = isJson ? await response.json().catch(() => null) : null;

  if (!isJson && response.ok) {
    throw new Error(
      "Received HTML from server instead of JSON. If deployed on Vercel, please check that VITE_API_URL is configured in your Vercel Project Settings to point to your live backend."
    );
  }

  if (!response.ok) {
    const errorMessage =
      (data && (data.error || data.message)) ||
      (typeof data === "string" ? data : `Request failed with status ${response.status}`);
    throw new Error(errorMessage);
  }

  return data as T;
}

export const api = {
  /**
   * Register a new student account
   * POST /api/auth/register
   */
  async register(request: RegisterRequest): Promise<AuthResponse> {
    const response = await apiFetch(`${API_BASE_URL}/api/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    });

    const data = await handleResponse<AuthResponse>(response);
    authStorage.setSession(data, request.name);
    return data;
  },

  /**
   * Login with email and password
   * POST /api/auth/login
   */
  async login(request: LoginRequest): Promise<AuthResponse> {
    const response = await apiFetch(`${API_BASE_URL}/api/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    });

    const data = await handleResponse<AuthResponse>(response);
    authStorage.setSession(data);
    return data;
  },

  /**
   * Get authenticated student profile
   * GET /api/profile
   */
  async getProfile(): Promise<StudentProfile> {
    const token = authStorage.getToken();
    if (!token) throw new Error("No authentication token available");

    const response = await apiFetch(`${API_BASE_URL}/api/profile`, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    return handleResponse<StudentProfile>(response);
  },

  /**
   * Request password reset token/link
   * POST /api/auth/forgot-password
   */
  async forgotPassword(email: string): Promise<ForgotPasswordResponse> {
    const response = await apiFetch(`${API_BASE_URL}/api/auth/forgot-password`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email }),
    });

    return handleResponse<ForgotPasswordResponse>(response);
  },

  /**
   * Reset password using token
   * POST /api/auth/reset-password
   */
  async resetPassword(request: ResetPasswordRequest): Promise<AuthResponse> {
    const response = await apiFetch(`${API_BASE_URL}/api/auth/reset-password`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    });

    const data = await handleResponse<AuthResponse>(response);
    authStorage.setSession(data);
    return data;
  },

  /**
   * Update student profile
   * PUT /api/profile
   */
  async updateProfile(profile: Partial<StudentProfile>): Promise<StudentProfile> {
    const token = authStorage.getToken();
    if (!token) throw new Error("No authentication token available");

    const response = await apiFetch(`${API_BASE_URL}/api/profile`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(profile),
    });

    return handleResponse<StudentProfile>(response);
  },

  /**
   * Upload and analyze a resume file (PDF or DOCX)
   * POST /api/resume/analyze
   */
  async analyzeResume(file: File, targetRole?: string): Promise<ResumeAnalysisResult> {
    const token = authStorage.getToken();
    if (!token) throw new Error("Please log in to analyze your resume");

    const formData = new FormData();
    formData.append("file", file);
    if (targetRole) {
      formData.append("targetRole", targetRole);
    }

    const response = await apiFetch(`${API_BASE_URL}/api/resume/analyze`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      body: formData,
    });

    return handleResponse<ResumeAnalysisResult>(response);
  },

  /**
   * Get the most recent resume analysis for the logged-in student
   * GET /api/resume/latest
   */
  async getLatestResumeAnalysis(): Promise<ResumeAnalysisResult | null> {
    const token = authStorage.getToken();
    if (!token) return null;

    const response = await apiFetch(`${API_BASE_URL}/api/resume/latest`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return handleResponse<ResumeAnalysisResult | null>(response);
  },

  /**
   * Get previous resume scans history
   * GET /api/resume/history
   */
  async getResumeHistory(): Promise<ResumeHistoryItem[]> {
    const token = authStorage.getToken();
    if (!token) return [];

    const response = await apiFetch(`${API_BASE_URL}/api/resume/history`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return handleResponse<ResumeHistoryItem[]>(response);
  },

  /**
   * Get detailed analysis by scan ID
   * GET /api/resume/{id}
   */
  async getResumeById(id: string): Promise<ResumeAnalysisResult> {
    const token = authStorage.getToken();
    if (!token) throw new Error("No authentication token available");

    const response = await apiFetch(`${API_BASE_URL}/api/resume/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return handleResponse<ResumeAnalysisResult>(response);
  },

  /**
   * Delete a resume scan from history
   * DELETE /api/resume/{id}
   */
  async deleteResumeAnalysis(id: string): Promise<void> {
    const token = authStorage.getToken();
    if (!token) throw new Error("No authentication token available");

    const response = await apiFetch(`${API_BASE_URL}/api/resume/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return handleResponse<void>(response);
  },

  /**
   * AI Bullet Point Rewriter Studio (STAR / Google XYZ method)
   * POST /api/resume/rewrite-bullet
   */
  async rewriteBulletPoint(request: BulletRewriteRequest): Promise<BulletRewriteResponse> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/resume/rewrite-bullet`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    });
    return handleResponse<BulletRewriteResponse>(response);
  },

  /**
   * Match resume against a recruiter job description
   * POST /api/resume/match-jd
   */
  async matchJobDescription(request: JdMatchRequest): Promise<JdMatchResponse> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/resume/match-jd`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    });
    return handleResponse<JdMatchResponse>(response);
  },

  /**
   * Get cross-role fit scores across all 6 engineering tracks
   * GET /api/resume/cross-role
   */
  async getCrossRoleComparison(resumeId?: string): Promise<CrossRoleComparisonResponse> {
    const token = authStorage.getToken();
    const url = resumeId
      ? `${API_BASE_URL}/api/resume/cross-role?resumeId=${resumeId}`
      : `${API_BASE_URL}/api/resume/cross-role`;
    const response = await apiFetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<CrossRoleComparisonResponse>(response);
  },

  /**
   * Get parsed ATS recruiter bot tree view & parser health
   * GET /api/resume/parser-tree
   */
  async getAtsParsedTree(resumeId?: string): Promise<AtsParsedTreeDto> {
    const token = authStorage.getToken();
    const url = resumeId
      ? `${API_BASE_URL}/api/resume/parser-tree?resumeId=${resumeId}`
      : `${API_BASE_URL}/api/resume/parser-tree`;
    const response = await apiFetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<AtsParsedTreeDto>(response);
  },

  // ═════════════════════════════════════════════════════════════════════════════
  // MODULE 5: APTITUDE TRAINING API
  // ═════════════════════════════════════════════════════════════════════════════

  /**
   * Get categories and topics summary
   * GET /api/aptitude/categories
   */
  async getAptitudeCategories(): Promise<AptitudeCategorySummary[]> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/aptitude/categories`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<AptitudeCategorySummary[]>(response);
  },

  /**
   * Get filtered questions for practice
   * GET /api/aptitude/questions
   */
  async getAptitudeQuestions(params?: {
    category?: string;
    topic?: string;
    difficulty?: string;
    limit?: number;
  }): Promise<AptitudeQuestion[]> {
    const token = authStorage.getToken();
    const q = new URLSearchParams();
    if (params?.category) q.set("category", params.category);
    if (params?.topic) q.set("topic", params.topic);
    if (params?.difficulty) q.set("difficulty", params.difficulty);
    if (params?.limit) q.set("limit", params.limit.toString());

    const url = `${API_BASE_URL}/api/aptitude/questions${q.toString() ? `?${q.toString()}` : ""}`;
    const response = await apiFetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<AptitudeQuestion[]>(response);
  },

  /**
   * Generate a timed mock test set
   * GET /api/aptitude/mock-test
   */
  async getAptitudeMockTest(category?: string, count: number = 10): Promise<AptitudeQuestion[]> {
    const token = authStorage.getToken();
    const q = new URLSearchParams();
    if (category) q.set("category", category);
    q.set("count", count.toString());

    const response = await apiFetch(`${API_BASE_URL}/api/aptitude/mock-test?${q.toString()}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<AptitudeQuestion[]>(response);
  },

  /**
   * Submit and grade aptitude test answers
   * POST /api/aptitude/submit
   */
  async submitAptitudeTest(request: AptitudeSubmitRequest): Promise<AptitudeResultResponse> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/aptitude/submit`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    });
    return handleResponse<AptitudeResultResponse>(response);
  },

  /**
   * Get placement formula cheat sheets
   * GET /api/aptitude/cheatsheet
   */
  async getAptitudeCheatsheet(): Promise<FormulaCard[]> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/aptitude/cheatsheet`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<FormulaCard[]>(response);
  },

  // ═════════════════════════════════════════════════════════════════════════════
  // MODULE 6: HR TRAINING & STAR EVALUATOR API
  // ═════════════════════════════════════════════════════════════════════════════

  /**
   * Get HR interview prompts
   * GET /api/hr/prompts
   */
  async getHrPrompts(category?: string): Promise<HrPrompt[]> {
    const token = authStorage.getToken();
    const url = category
      ? `${API_BASE_URL}/api/hr/prompts?category=${category}`
      : `${API_BASE_URL}/api/hr/prompts`;
    const response = await apiFetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<HrPrompt[]>(response);
  },

  /**
   * Get a specific prompt by ID
   * GET /api/hr/prompts/{id}
   */
  async getHrPromptById(id: string): Promise<HrPrompt> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/hr/prompts/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<HrPrompt>(response);
  },

  /**
   * Submit response for AI STAR evaluation
   * POST /api/hr/evaluate
   */
  async evaluateHrResponse(request: HrEvaluationRequest): Promise<HrEvaluationResponse> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/hr/evaluate`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    });
    return handleResponse<HrEvaluationResponse>(response);
  },

  /**
   * Get user's past evaluated HR answers
   * GET /api/hr/history
   */
  async getHrHistory(): Promise<HrHistoryItem[]> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/hr/history`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<HrHistoryItem[]>(response);
  },

  getApiBaseUrl,
  setCustomApiUrl,
};

// ── Types for Module 5: Aptitude Training ──────────────────────────────────
export interface AptitudeTopicSummary {
  topicId: string;
  topicName: string;
  questionCount: number;
  keyConcept: string;
}

export interface AptitudeCategorySummary {
  category: "QUANTITATIVE" | "LOGICAL_REASONING" | "VERBAL_ABILITY";
  title: string;
  description: string;
  totalQuestions: number;
  topics: AptitudeTopicSummary[];
}

export interface AptitudeQuestion {
  id: string;
  category: "QUANTITATIVE" | "LOGICAL_REASONING" | "VERBAL_ABILITY";
  topic: string;
  difficulty: "EASY" | "MEDIUM" | "HARD";
  question: string;
  options: string[];
  correctOptionIndex?: number;
  explanation?: string;
  formulaTip?: string;
  companiesAsked?: string[];
}

export interface AptitudeAnswerSubmission {
  questionId: string;
  selectedOptionIndex: number | null;
  timeSpentSeconds: number;
}

export interface AptitudeSubmitRequest {
  testId?: string;
  category?: string;
  topic?: string;
  totalTimeSpentSeconds: number;
  answers: AptitudeAnswerSubmission[];
}

export interface AptitudeTopicBreakdown {
  topic: string;
  total: number;
  correct: number;
  accuracy: number;
}

export interface AptitudeQuestionReview {
  questionId: string;
  topic: string;
  question: string;
  options: string[];
  selectedOptionIndex: number | null;
  correctOptionIndex: number;
  isCorrect: boolean;
  isAttempted: boolean;
  explanation: string;
  formulaTip?: string;
}

export interface AptitudeResultResponse {
  testId: string;
  totalQuestions: number;
  correctCount: number;
  incorrectCount: number;
  unattemptedCount: number;
  scorePercentage: number;
  totalTimeSpentSeconds: number;
  performanceVerdict: string;
  performanceFeedback: string;
  topicBreakdowns: AptitudeTopicBreakdown[];
  questionReviews: AptitudeQuestionReview[];
}

export interface FormulaCard {
  category: string;
  topic: string;
  title: string;
  formula: string;
  tip: string;
  example: string;
}

// ── Types for Module 6: HR Training ────────────────────────────────────────
export type HrCategoryType =
  | "SELF_INTRODUCTION"
  | "LEADERSHIP"
  | "CONFLICT_RESOLUTION"
  | "TEAMWORK"
  | "FAILURE_RESILIENCE"
  | "CAREER_VISION";

export interface HrPrompt {
  id: string;
  category: HrCategoryType;
  categoryTitle: string;
  question: string;
  recruiterIntent: string;
  keyPointsToInclude: string[];
  commonPitfalls: string[];
  sampleModelAnswer: string;
  companyTags: string[];
}

export interface HrEvaluationRequest {
  promptId?: string;
  category?: HrCategoryType;
  questionText?: string;
  userResponse: string;
}

export interface HrComponentScore {
  score: number;
  detected: boolean;
  status: string;
  feedback: string;
}

export interface HrStarBreakdown {
  situation: HrComponentScore;
  task: HrComponentScore;
  action: HrComponentScore;
  result: HrComponentScore;
}

export interface HrEvaluationResponse {
  id: string;
  overallScore: number;
  situationScore: number;
  taskScore: number;
  actionScore: number;
  resultScore: number;
  clarityScore: number;
  verdict: string;
  executiveSummary: string;
  starBreakdown: HrStarBreakdown;
  strengths: string[];
  areasForImprovement: string[];
  barRaiserModelAnswer: string;
  wordCount: number;
  metricsDetected: string[];
  actionVerbsDetected: string[];
}

export interface HrHistoryItem {
  id: string;
  promptId: string;
  category: string;
  categoryTitle: string;
  questionText: string;
  userResponseSnippet: string;
  overallScore: number;
  verdict: string;
  createdAt: string;
}


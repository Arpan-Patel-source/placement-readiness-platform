// API Service for PlacementAI Spring Boot Backend
export function getApiBaseUrl(): string {
  // 1. Check local storage override if user configured one
  if (typeof window !== "undefined") {
    const custom = localStorage.getItem("placement_custom_api_url");
    if (custom && custom.trim()) {
      return custom.trim().replace(/\/+$/, "");
    }
  }

  // 2. Vite build-time static replacement (check multiple common env naming conventions)
  const envUrl =
    import.meta.env.VITE_API_URL ||
    (import.meta.env as any).VITE_BACKEND_URL ||
    (import.meta.env as any).VITE_API_BASE_URL ||
    (import.meta.env as any).API_URL;
  if (typeof envUrl === "string" && envUrl.trim()) {
    return envUrl.trim().replace(/\/+$/, "");
  }

  // 3. Node/SSR fallback if running in Nitro serverless function
  if (typeof process !== "undefined" && process.env) {
    const procEnv = process.env as Record<string, string | undefined>;
    const procUrl =
      procEnv["VITE_API_URL"] ||
      procEnv["VITE_BACKEND_URL"] ||
      procEnv["API_URL"] ||
      procEnv["BACKEND_URL"];
    if (typeof procUrl === "string" && procUrl.trim()) {
      return procUrl.trim().replace(/\/+$/, "");
    }
  }

  // 4. Automatic production fallback on Vercel or any remote cloud domain
  if (typeof window !== "undefined") {
    const host = window.location.hostname;
    if (host && host !== "localhost" && host !== "127.0.0.1" && !host.startsWith("192.168.")) {
      return "https://placement-ai-assistant.onrender.com";
    }
  }

  // 5. In local development on localhost, empty string uses Vite dev proxy
  return "";
}

export function setCustomApiUrl(url: string): void {
  if (typeof window === "undefined") return;
  if (!url || !url.trim()) {
    localStorage.removeItem("placement_custom_api_url");
  } else {
    localStorage.setItem("placement_custom_api_url", url.trim().replace(/\/+$/, ""));
  }
}

/**
 * Pre-warms the backend container if it is asleep on Render's free tier.
 * Safe fire-and-forget ping to /health.
 */
export function prewarmBackend(): void {
  if (typeof window === "undefined") return;
  const base = getApiBaseUrl();
  if (!base) return;
  try {
    fetch(`${base}/health`, { method: "GET", mode: "no-cors" }).catch(() => {});
  } catch {
    // Ignore prewarm error
  }
}

// Global fallback for any `${API_BASE_URL}/...` legacy usages
export const API_BASE_URL = "";

// Timeout helper: 90s to accommodate Render free-tier cold boot (takes ~50–75s on spin-up)
const DEFAULT_API_TIMEOUT_MS = 90000;

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
        "Request timed out. The backend server may still be waking up (Render free tier can take up to 60–80s on cold start). Please try again or switch to Demo Mode."
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

  // ═════════════════════════════════════════════════════════════════════════════
  // MODULE 7: TECHNICAL TRAINING API
  // ═════════════════════════════════════════════════════════════════════════════

  /**
   * Get technical training categories and topic summaries
   * GET /api/technical/categories
   */
  async getTechCategories(): Promise<TechCategorySummary[]> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/technical/categories`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<TechCategorySummary[]>(response);
  },

  /**
   * Get filtered technical questions for practice
   * GET /api/technical/questions
   */
  async getTechQuestions(params?: {
    category?: string;
    topic?: string;
    difficulty?: string;
    limit?: number;
  }): Promise<TechQuestion[]> {
    const token = authStorage.getToken();
    const q = new URLSearchParams();
    if (params?.category) q.set("category", params.category);
    if (params?.topic) q.set("topic", params.topic);
    if (params?.difficulty) q.set("difficulty", params.difficulty);
    if (params?.limit) q.set("limit", params.limit.toString());

    const url = `${API_BASE_URL}/api/technical/questions${q.toString() ? `?${q.toString()}` : ""}`;
    const response = await apiFetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<TechQuestion[]>(response);
  },

  /**
   * Generate a timed technical mock test
   * GET /api/technical/mock-test
   */
  async getTechMockTest(category?: string, count: number = 10): Promise<TechQuestion[]> {
    const token = authStorage.getToken();
    const q = new URLSearchParams();
    if (category) q.set("category", category);
    q.set("count", count.toString());

    const response = await apiFetch(`${API_BASE_URL}/api/technical/mock-test?${q.toString()}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<TechQuestion[]>(response);
  },

  /**
   * Submit and grade a technical test
   * POST /api/technical/submit
   */
  async submitTechTest(request: TechSubmitRequest): Promise<TechResultResponse> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/technical/submit`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    });
    return handleResponse<TechResultResponse>(response);
  },

  /**
   * Get technical concept cheat sheets
   * GET /api/technical/cheatsheet
   */
  async getTechCheatsheet(): Promise<TechFormulaCard[]> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/technical/cheatsheet`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<TechFormulaCard[]>(response);
  },

  /**
   * Get user's technical test history
   * GET /api/technical/history
   */
  async getTechHistory(): Promise<TechHistoryItem[]> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/technical/history`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<TechHistoryItem[]>(response);
  },

  // ═════════════════════════════════════════════════════════════════════════════
  // MODULE 3 & 4: COMPETITIVE CODING ARENA & AI CODING MENTOR API
  // ═════════════════════════════════════════════════════════════════════════════

  /**
   * Get coding categories and problem counts
   * GET /api/coding/categories
   */
  async getCodingCategories(): Promise<CodingCategorySummary[]> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/coding/categories`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<CodingCategorySummary[]>(response);
  },

  /**
   * Get problems list with optional category, difficulty, or search filters
   * GET /api/coding/problems
   */
  async getCodingProblems(params?: {
    category?: string;
    difficulty?: string;
    search?: string;
  }): Promise<CodingProblem[]> {
    const token = authStorage.getToken();
    const q = new URLSearchParams();
    if (params?.category) q.set("category", params.category);
    if (params?.difficulty) q.set("difficulty", params.difficulty);
    if (params?.search) q.set("search", params.search);

    const url = `${API_BASE_URL}/api/coding/problems${q.toString() ? `?${q.toString()}` : ""}`;
    const response = await apiFetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<CodingProblem[]>(response);
  },

  /**
   * Get single problem by ID
   * GET /api/coding/problems/{id}
   */
  async getCodingProblem(id: string): Promise<CodingProblem> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/coding/problems/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<CodingProblem>(response);
  },

  /**
   * Submit solution for automated evaluation
   * POST /api/coding/submit
   */
  async submitCodingSolution(request: CodingSubmitRequest): Promise<CodingResultResponse> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/coding/submit`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    });
    return handleResponse<CodingResultResponse>(response);
  },

  /**
   * Get user's coding submission history
   * GET /api/coding/history
   */
  async getCodingHistory(): Promise<CodingHistoryItem[]> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/coding/history`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<CodingHistoryItem[]>(response);
  },

  /**
   * Get coding arena stats
   * GET /api/coding/stats
   */
  async getCodingStats(): Promise<Record<string, any>> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/coding/stats`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<Record<string, any>>(response);
  },

  /**
   * Analyze code with AI Mentor
   * POST /api/coding-mentor/analyze
   */
  async analyzeCodingSolution(request: MentorAnalysisRequest): Promise<MentorAnalysisResponse> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/coding-mentor/analyze`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    });
    return handleResponse<MentorAnalysisResponse>(response);
  },

  // ═════════════════════════════════════════════════════════════════════════════
  // MODULE 7: AI MOCK INTERVIEW API
  // ═════════════════════════════════════════════════════════════════════════════

  /**
   * Start a new mock interview session
   * POST /api/interview/start
   */
  async startMockInterview(request: StartInterviewRequest): Promise<StartInterviewResponse> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/interview/start`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    });
    return handleResponse<StartInterviewResponse>(response);
  },

  /**
   * Submit interview answers for evaluation
   * POST /api/interview/submit
   */
  async submitMockInterview(request: SubmitInterviewRequest): Promise<InterviewResultResponse> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/interview/submit`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    });
    return handleResponse<InterviewResultResponse>(response);
  },

  /**
   * Get interview history
   * GET /api/interview/history
   */
  async getMockInterviewHistory(): Promise<InterviewHistoryItem[]> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/interview/history`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<InterviewHistoryItem[]>(response);
  },

  // ═════════════════════════════════════════════════════════════════════════════
  // MODULE 10, 11, 12: DASHBOARD, ROADMAP, PREDICTION API
  // ═════════════════════════════════════════════════════════════════════════════

  /**
   * Get placement readiness scores
   * GET /api/dashboard/readiness
   */
  async getReadinessScore(): Promise<ReadinessScoreResponse> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/dashboard/readiness`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<ReadinessScoreResponse>(response);
  },

  /**
   * Generate personalized roadmap based on weak areas
   * GET /api/roadmap/generate
   */
  async getRoadmap(): Promise<RoadmapResponse> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/roadmap/generate`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<RoadmapResponse>(response);
  },

  /**
   * Get company-specific placement predictions
   * GET /api/prediction/companies
   */
  async getCompanyPredictions(): Promise<CompanyPredictionDto[]> {
    const token = authStorage.getToken();
    const response = await apiFetch(`${API_BASE_URL}/api/prediction/companies`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return handleResponse<CompanyPredictionDto[]>(response);
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

// ── Types for Module 7: Technical Training ─────────────────────────────────
export type TechCategoryType =
  | "OOP"
  | "DBMS"
  | "OPERATING_SYSTEMS"
  | "COMPUTER_NETWORKS"
  | "DSA"
  | "WEB_TECHNOLOGIES";

export interface TechTopicSummary {
  topicId: string;
  topicName: string;
  questionCount: number;
  keyConcept: string;
}

export interface TechCategorySummary {
  category: TechCategoryType;
  title: string;
  description: string;
  totalQuestions: number;
  topics: TechTopicSummary[];
}

export interface TechQuestion {
  id: string;
  category: TechCategoryType;
  topic: string;
  difficulty: "EASY" | "MEDIUM" | "HARD";
  question: string;
  options: string[];
  correctOptionIndex?: number;
  explanation?: string;
  conceptTip?: string;
  companiesAsked?: string[];
}

export interface TechAnswerSubmission {
  questionId: string;
  selectedOptionIndex: number | null;
  timeSpentSeconds: number;
}

export interface TechSubmitRequest {
  testId?: string;
  category?: string;
  topic?: string;
  totalTimeSpentSeconds: number;
  answers: TechAnswerSubmission[];
}

export interface TechTopicBreakdown {
  topic: string;
  total: number;
  correct: number;
  accuracy: number;
}

export interface TechQuestionReview {
  questionId: string;
  topic: string;
  question: string;
  options: string[];
  selectedOptionIndex: number | null;
  correctOptionIndex: number;
  isCorrect: boolean;
  isAttempted: boolean;
  explanation: string;
  conceptTip?: string;
}

export interface TechResultResponse {
  testId: string;
  totalQuestions: number;
  correctCount: number;
  incorrectCount: number;
  unattemptedCount: number;
  scorePercentage: number;
  totalTimeSpentSeconds: number;
  performanceVerdict: string;
  performanceFeedback: string;
  topicBreakdowns: TechTopicBreakdown[];
  questionReviews: TechQuestionReview[];
}

export interface TechFormulaCard {
  category: string;
  topic: string;
  title: string;
  formula: string;
  tip: string;
  example: string;
}

export interface TechHistoryItem {
  id: string;
  testId: string;
  category: TechCategoryType;
  categoryTitle: string;
  totalQuestions: number;
  correctCount: number;
  scorePercentage: number;
  verdict: string;
  createdAt: string;
}

// ── Types for Module 3 & 4: Coding Arena & Mentor ─────────────────────────────
export type CodingCategoryType =
  | "ARRAYS"
  | "STRINGS"
  | "LINKED_LIST"
  | "TREES"
  | "GRAPHS"
  | "DP"
  | "GREEDY";

export type CodingDifficultyType = "EASY" | "MEDIUM" | "HARD";
export type CodingLanguageType = "JAVA" | "PYTHON" | "CPP" | "C";

export interface CodingCategorySummary {
  category: CodingCategoryType;
  displayName: string;
  problemCount: number;
  description: string;
}

export interface CodingTestCaseDto {
  input: string;
  expectedOutput: string;
}

export interface CodingProblem {
  id: string;
  title: string;
  description: string;
  category: CodingCategoryType;
  difficulty: CodingDifficultyType;
  constraints: string;
  inputFormat: string;
  outputFormat: string;
  sampleTestCases: CodingTestCaseDto[];
  totalTestCases: number;
  starterCodeJava: string;
  starterCodePython: string;
  starterCodeCpp: string;
  starterCodeC: string;
  hints: string[];
  timeComplexity: string;
  spaceComplexity: string;
  companiesAsked: string[];
}

export interface CodingSubmitRequest {
  problemId: string;
  language: CodingLanguageType;
  code: string;
  runSampleOnly?: boolean;
}

export interface CodingTestCaseResult {
  testCaseIndex: number;
  passed: boolean;
  input: string;
  expectedOutput: string;
  actualOutput: string;
  isHidden: boolean;
}

export interface CodingResultResponse {
  submissionId: string;
  problemId: string;
  status:
    | "ACCEPTED"
    | "WRONG_ANSWER"
    | "TIME_LIMIT_EXCEEDED"
    | "RUNTIME_ERROR"
    | "COMPILATION_ERROR";
  passedTestCases: number;
  totalTestCases: number;
  runtimeMs: number;
  memoryKb: number;
  testCaseResults: CodingTestCaseResult[];
  verdict: string;
  feedback: string;
}

export interface CodingHistoryItem {
  id: string;
  problemId: string;
  problemTitle: string;
  language: string;
  status: string;
  runtimeMs: number;
  memoryKb: number;
  createdAt: string;
}

export interface MentorAnalysisRequest {
  problemId: string;
  language: string;
  code: string;
}

export interface MentorAnalysisResponse {
  problemId: string;
  problemTitle: string;
  detectedApproach: string;
  timeComplexity: string;
  spaceComplexity: string;
  optimalTimeComplexity: string;
  optimalSpaceComplexity: string;
  isOptimal: boolean;
  betterApproach: string;
  betterApproachExplanation: string;
  codeQualitySuggestions: string[];
  optimizationTips: string[];
  overallVerdict: string;
  codeQualityScore: number;
}

// ── Types for Module 7: AI Mock Interview ─────────────────────────────────────
export type InterviewRoundType = "HR" | "TECHNICAL" | "CODING";

export interface StartInterviewRequest {
  roundType: InterviewRoundType;
  questionCount?: number;
  skills?: string[];
}

export interface StartInterviewResponse {
  sessionId: string;
  roundType: string;
  questions: Array<Record<string, string>>;
  totalQuestions: number;
}

export interface InterviewAnswerEntry {
  questionId: string;
  question: string;
  answer: string;
}

export interface SubmitInterviewRequest {
  sessionId: string;
  answers: InterviewAnswerEntry[];
}

export interface InterviewQuestionResult {
  questionId: string;
  question: string;
  answer: string;
  score: number;
  verdict: string;
  feedback: string[];
}

export interface InterviewResultResponse {
  sessionId: string;
  roundType: string;
  overallScore: number;
  verdict: string;
  executiveSummary: string;
  questionResults: InterviewQuestionResult[];
  strengths: string[];
  areasForImprovement: string[];
}

export interface InterviewHistoryItem {
  sessionId: string;
  roundType: string;
  overallScore: number;
  verdict: string;
  totalQuestions: number;
  createdAt: string;
}

// ── Types for Module 10, 11, 12: Dashboard, Roadmap, Prediction ───────────────
export interface ReadinessScoreResponse {
  resumeScore: number;
  codingScore: number;
  aptitudeScore: number;
  hrScore: number;
  technicalScore: number;
  interviewScore: number;
  overallScore: number;
  weakAreas: string[];
  strongAreas: string[];
  totalCodingSubmissions: number;
  solvedProblems: number;
  readinessVerdict: string;
}

export interface WeekPlan {
  week: string;
  focus: string;
  rationale: string;
  tasks: string[];
}

export interface RoadmapResponse {
  studentName: string;
  currentReadiness: number;
  weeks: WeekPlan[];
}

export interface CompanyPredictionDto {
  companyName: string;
  placementProbability: number;
  interviewSuccessRate: number;
  prepAdvice: string;
  focusAreas: string[];
  previousQuestionTopics: string[];
  aptitudePattern: string;
  codingDifficulty: string;
  interviewStyle: string;
}


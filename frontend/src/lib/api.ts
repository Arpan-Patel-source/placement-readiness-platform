// API Service for PlacementAI Spring Boot Backend

const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

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
    const session: UserSession = {
      token: data.token,
      email: data.email,
      role: data.role,
      name: name || data.email.split("@")[0],
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
  const isJson = response.headers.get("content-type")?.includes("application/json");
  const data = isJson ? await response.json() : null;

  if (!response.ok) {
    const errorMessage =
      (data && (data.message || data.error)) ||
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
    const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
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
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
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

    const response = await fetch(`${API_BASE_URL}/api/profile`, {
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
    const response = await fetch(`${API_BASE_URL}/api/auth/forgot-password`, {
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
    const response = await fetch(`${API_BASE_URL}/api/auth/reset-password`, {
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

    const response = await fetch(`${API_BASE_URL}/api/profile`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(profile),
    });

    return handleResponse<StudentProfile>(response);
  },
};


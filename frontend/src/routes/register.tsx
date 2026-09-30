import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState, useEffect, useRef } from "react";

import { api, authStorage, prewarmBackend } from "@/lib/api";
import { AuthLayout } from "@/components/AuthLayout";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Loader2, ArrowRight } from "lucide-react";

export const Route = createFileRoute("/register")({
  head: () => ({
    meta: [
      { title: "Create your account — PlacementAI" },
      {
        name: "description",
        content:
          "Create a free PlacementAI account for AI resume analysis, mock interviews, coding and aptitude practice.",
      },
      { property: "og:title", content: "Create your account — PlacementAI" },
      {
        property: "og:description",
        content: "Start preparing for placements with an AI companion built for students.",
      },
    ],
  }),
  component: RegisterPage,
});

function RegisterPage() {
  const navigate = useNavigate();
  // Read Google pre-fill params from the URL (set by login page when isNewUser=true)
  const urlParams = typeof window !== "undefined" ? new URLSearchParams(window.location.search) : null;
  let search: any = {};
  try {
    search = (Route.useSearch as any)?.() ?? {};
  } catch {
    search = {};
  }

  const isGoogleFlow = search?.google === "1" || urlParams?.get("google") === "1";
  const googleIdToken = (search?.idToken as string | undefined) || urlParams?.get("idToken") || undefined;

  const [name, setName] = useState((search?.name as string) || urlParams?.get("name") || "");
  const [college, setCollege] = useState("");
  const [email, setEmail] = useState((search?.email as string) || urlParams?.get("email") || "");
  const [password, setPassword] = useState("");
  const [termsAccepted, setTermsAccepted] = useState(true);
  const [loading, setLoading] = useState(false);
  const [loadingSeconds, setLoadingSeconds] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [offlineNotice, setOfflineNotice] = useState(false);
  const [showServerConfig, setShowServerConfig] = useState(false);
  const [customBackendUrl, setCustomBackendUrl] = useState(() => api.getApiBaseUrl());
  const [googleLoading, setGoogleLoading] = useState(false);
  const googleBtnRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    prewarmBackend();
  }, []);

  useEffect(() => {
    let interval: any;
    if (loading) {
      setLoadingSeconds(0);
      interval = setInterval(() => {
        setLoadingSeconds((prev) => prev + 1);
      }, 1000);
    }
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [loading]);

  // Load Google GSI script for the register page (so users can also start Google flow from here)
  useEffect(() => {
    if (isGoogleFlow) return; // Already in Google flow, no need to show button again
    const clientId = import.meta.env["VITE_GOOGLE_CLIENT_ID"];
    if (!clientId || clientId === "YOUR_GOOGLE_CLIENT_ID_HERE") return;

    const scriptId = "google-gsi-script";

    function initGoogleSignIn() {
      const google = (window as any).google;
      if (!google || !googleBtnRef.current) return;
      google.accounts.id.initialize({
        client_id: clientId,
        callback: handleGoogleCredential,
      });
      google.accounts.id.renderButton(googleBtnRef.current, {
        type: "standard",
        shape: "pill",
        theme: "outline",
        size: "large",
        text: "signup_with",
        width: googleBtnRef.current.offsetWidth || 340,
      });
    }

    if (!document.getElementById(scriptId)) {
      const script = document.createElement("script");
      script.id = scriptId;
      script.src = "https://accounts.google.com/gsi/client";
      script.async = true;
      script.defer = true;
      script.onload = () => initGoogleSignIn();
      document.head.appendChild(script);
    } else {
      initGoogleSignIn();
    }
  }, [isGoogleFlow]);

  const handleGoogleCredential = async (response: { credential: string }) => {
    setError(null);
    setGoogleLoading(true);
    try {
      const result = await api.googleAuth(response.credential);
      if (result.token && !result.isNewUser) {
        // Account already exists — show a friendly notice instead of silently logging in
        setError("ACCOUNT_EXISTS");
      } else if (result.isNewUser) {
        // Same page but now with pre-filled data
        navigate({
          to: "/register",
          search: {
            google: "1",
            name: result.name,
            email: result.email,
            idToken: response.credential,
          } as any,
        });
      } else {
        navigate({ to: "/dashboard" });
      }
    } catch (err: any) {
      const errMsg = err?.message || "";
      if (
        errMsg.includes("Failed to fetch") ||
        errMsg.includes("NetworkError") ||
        errMsg.includes("fetch") ||
        errMsg.includes("timed out") ||
        errMsg.includes("HTML from server")
      ) {
        setOfflineNotice(true);
      } else if (errMsg.toLowerCase().includes("already registered")) {
        setError("ACCOUNT_EXISTS");
      } else {
        const friendlyMsg = errMsg.includes("violates not-null constraint") || errMsg.includes("could not execute statement")
          ? "Unable to complete Google sign-up due to a server account setup error. Please try again."
          : errMsg;
        setError(friendlyMsg || "Google sign-up failed. Please try again.");
      }
    } finally {
      setGoogleLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setOfflineNotice(false);

    if (isGoogleFlow && googleIdToken) {
      // Complete Google registration
      if (!name.trim()) {
        setError("Please enter your full name.");
        return;
      }
      setLoading(true);
      try {
        // Re-verify the token to get the googleId (subject) from backend
        // We pass the idToken again and the backend extracts the googleId
        await api.googleRegister({ idToken: googleIdToken, name, email, college: college || undefined });
        navigate({ to: "/dashboard" });
      } catch (err: any) {
        const errMsg = err?.message || "";
        if (
          errMsg.includes("Failed to fetch") ||
          errMsg.includes("NetworkError") ||
          errMsg.includes("fetch") ||
          errMsg.includes("timed out") ||
          errMsg.includes("HTML from server")
        ) {
          setOfflineNotice(true);
        } else if (errMsg.toLowerCase().includes("already registered")) {
          setError("ACCOUNT_EXISTS");
        } else {
          setError(errMsg || "Failed to create account. Please check your details.");
        }
      } finally {
        setLoading(false);
      }
      return;
    }

    if (password.length < 6) {
      setError("Password must be at least 6 characters long.");
      return;
    }

    setLoading(true);

    try {
      await api.register({ name, email, password });
      if (college) {
        try {
          await api.updateProfile({ collegeName: college, fullName: name });
        } catch {
          // Non-critical profile update failure
        }
      }
      navigate({ to: "/dashboard" });
    } catch (err: any) {
      console.warn("Backend register error:", err);
      const errMsg = err?.message || "";
      if (
        errMsg.includes("Failed to fetch") ||
        errMsg.includes("NetworkError") ||
        errMsg.includes("fetch") ||
        errMsg.includes("timed out") ||
        errMsg.includes("HTML from server")
      ) {
        setOfflineNotice(true);
      } else {
        setError(errMsg || "Failed to create account. Please check your details.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleDemoRegister = () => {
    authStorage.setSession(
      {
        token: "demo-jwt-token",
        email: email || "student@placementai.edu",
        role: "STUDENT",
      },
      name || "Demo Student",
    );
    navigate({ to: "/dashboard" });
  };

  const googleClientId = import.meta.env["VITE_GOOGLE_CLIENT_ID"];
  const googleEnabled = googleClientId && googleClientId !== "YOUR_GOOGLE_CLIENT_ID_HERE";

  return (
    <AuthLayout
      eyebrow={isGoogleFlow ? "ALMOST THERE" : "GET STARTED"}
      title={isGoogleFlow ? "Confirm your" : "Start your"}
      highlight="journey"
      subtitle={
        isGoogleFlow
          ? "Your Google account is ready. Just confirm a few details to get started."
          : "Create a free account and get your first AI resume review in minutes."
      }
      footer={
        <>
          Already have an account?{" "}
          <Link to="/login" className="font-semibold text-coral">
            Sign in
          </Link>
        </>
      }
    >
      <form className="space-y-5" onSubmit={handleSubmit}>
        {error && error === "ACCOUNT_EXISTS" && (
          <div className="rounded-xl border border-amber-300/40 bg-amber-50 p-4 text-sm text-amber-900 space-y-2">
            <p className="font-semibold flex items-center gap-2">
              <svg className="size-4 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              An account already exists for this email
            </p>
            <p className="text-xs text-amber-800 leading-relaxed">
              You've already signed up with this Google account. Please log in instead of creating a new account.
            </p>
            <Link
              to="/login"
              className="inline-flex items-center gap-1.5 rounded-full bg-coral px-4 py-1.5 text-xs font-semibold text-primary-foreground shadow-sm transition hover:bg-coral/90"
            >
              Go to Login <ArrowRight className="size-3" />
            </Link>
          </div>
        )}

        {error && error !== "ACCOUNT_EXISTS" && (
          <div className="rounded-xl border border-destructive/20 bg-destructive/10 p-3 text-sm text-destructive">
            {error}
          </div>
        )}

        {/* Google Sign-Up button — only shown on fresh register page (not in Google flow) */}
        {googleEnabled && !isGoogleFlow && (
          <div className="space-y-3">
            <div
              ref={googleBtnRef}
              className="flex w-full justify-center"
              style={{ minHeight: 44 }}
            />
            {googleLoading && (
              <div className="flex items-center justify-center gap-2 text-xs text-ink/60">
                <Loader2 className="size-3.5 animate-spin" />
                Signing up with Google...
              </div>
            )}
            <div className="relative flex items-center gap-3">
              <div className="h-px flex-1 bg-border" />
              <span className="text-xs text-ink/40 font-medium">or sign up with email</span>
              <div className="h-px flex-1 bg-border" />
            </div>
          </div>
        )}

        {/* Google flow banner */}
        {isGoogleFlow && (
          <div className="rounded-xl border border-green-200 bg-green-50 p-3 text-sm text-green-800 flex items-center gap-2">
            <svg className="size-4 shrink-0" viewBox="0 0 24 24">
              <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
              <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
              <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
              <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
            </svg>
            <span>Signed in with Google as <strong>{email}</strong></span>
          </div>
        )}

        {offlineNotice && (
          <div className="space-y-3 rounded-2xl border border-border bg-peach/40 p-4 text-sm text-ink">
            <p className="font-semibold text-coral">Unable to reach backend server</p>
            <p className="text-xs text-ink/75 leading-relaxed">
              {typeof window !== "undefined" && window.location.hostname.includes("localhost") ? (
                <>
                  To connect live, start the local Spring Boot app:
                  <br />
                  <code className="mt-1 inline-block rounded bg-background/80 px-2 py-0.5 font-mono text-[11px]">
                    cd backend &amp;&amp; ./mvnw spring-boot:run
                  </code>
                </>
              ) : (
                <>
                  Render free-tier instances sleep when idle and take 40–60s on cold start. You can
                  continue instantly in Demo Mode or specify your Render backend URL below.
                </>
              )}
            </p>
            <div className="flex flex-wrap items-center gap-2 pt-1">
              <button
                type="button"
                onClick={handleDemoRegister}
                className="inline-flex items-center gap-1.5 rounded-full bg-coral px-4 py-1.5 text-xs font-semibold text-primary-foreground shadow-sm transition hover:bg-coral/90"
              >
                Enter Demo Mode <ArrowRight className="size-3" />
              </button>
              <button
                type="button"
                onClick={() => setShowServerConfig(!showServerConfig)}
                className="text-xs text-coral hover:underline"
              >
                {showServerConfig ? "Hide server settings" : "Configure backend URL"}
              </button>
            </div>
            {showServerConfig && (
              <div className="mt-2 pt-2 border-t border-border/40 space-y-2">
                <Label htmlFor="custom-backend" className="text-xs">
                  Render Backend URL:
                </Label>
                <div className="flex gap-2">
                  <Input
                    id="custom-backend"
                    value={customBackendUrl}
                    onChange={(e) => setCustomBackendUrl(e.target.value)}
                    placeholder="https://placement-ai-backend.onrender.com"
                    className="text-xs h-8"
                  />
                  <button
                    type="button"
                    onClick={() => {
                      api.setCustomApiUrl(customBackendUrl);
                      setOfflineNotice(false);
                      setError("Backend URL saved! You can try registering now.");
                    }}
                    className="rounded-lg bg-ink px-3 text-xs text-white hover:bg-ink/80"
                  >
                    Save
                  </button>
                </div>
              </div>
            )}
          </div>
        )}

        <div className="space-y-2">
          <Label htmlFor="name">Full name</Label>
          <Input
            id="name"
            required
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Azhar Khan"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="college">College</Label>
          <Input
            id="college"
            value={college}
            onChange={(e) => setCollege(e.target.value)}
            placeholder="Your college or university"
          />
        </div>

        {/* Email — readonly in Google flow */}
        <div className="space-y-2">
          <Label htmlFor="email">Email</Label>
          <Input
            id="email"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="you@college.edu"
            readOnly={isGoogleFlow}
            className={isGoogleFlow ? "opacity-60 cursor-not-allowed" : ""}
          />
          {isGoogleFlow && (
            <p className="text-xs text-ink/50">Email is set by your Google account.</p>
          )}
        </div>

        {/* Password field — only for non-Google flow */}
        {!isGoogleFlow && (
          <div className="space-y-2">
            <Label htmlFor="password">Password</Label>
            <Input
              id="password"
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="At least 6 characters"
            />
          </div>
        )}

        <label className="flex items-start gap-2 text-sm text-ink/70">
          <Checkbox
            id="terms"
            className="mt-0.5"
            checked={termsAccepted}
            onCheckedChange={(checked) => setTermsAccepted(!!checked)}
          />
          <span>I agree to the Terms of Service and Privacy Policy</span>
        </label>

        {loading && loadingSeconds >= 2 && (
          <div className="rounded-2xl border border-coral/30 bg-coral/5 p-3.5 text-sm text-ink animate-in fade-in duration-300 space-y-2">
            <div className="flex items-center justify-between font-medium text-coral text-xs">
              <span className="flex items-center gap-1.5">
                <Loader2 className="size-3.5 animate-spin" />
                {loadingSeconds > 25
                  ? "Spring Boot backend is booting up..."
                  : "Connecting to backend server..."}
              </span>
              <span className="font-mono bg-coral/10 px-2 py-0.5 rounded-full">
                {loadingSeconds}s
              </span>
            </div>
            <p className="text-[11px] text-ink/75 leading-relaxed">
              Render free tier spins down after 15m of inactivity. First cold start takes ~50–70s to
              boot. Please wait, or click below for instant Demo Mode!
            </p>
            {loadingSeconds >= 5 && (
              <div className="pt-2 border-t border-coral/20 flex items-center justify-between">
                <span className="text-[11px] text-ink/60">Don't want to wait?</span>
                <button
                  type="button"
                  onClick={handleDemoRegister}
                  className="inline-flex items-center gap-1 rounded-full bg-coral px-3 py-1 text-[11px] font-semibold text-primary-foreground shadow-sm hover:bg-coral/90 transition"
                >
                  Instant Demo Mode <ArrowRight className="size-3" />
                </button>
              </div>
            )}
          </div>
        )}

        <button
          type="submit"
          disabled={loading || !termsAccepted}
          className="inline-flex w-full items-center justify-center gap-2 rounded-full bg-coral py-3 text-sm font-semibold text-primary-foreground shadow-md transition-transform hover:-translate-y-0.5 disabled:opacity-70"
        >
          {loading ? (
            <>
              <Loader2 className="size-4 animate-spin" />
              <span>Creating account... {loadingSeconds > 0 ? `(${loadingSeconds}s)` : ""}</span>
            </>
          ) : isGoogleFlow ? (
            "Complete sign-up"
          ) : (
            "Create account"
          )}
        </button>

        <div className="text-center pt-1">
          <button
            type="button"
            onClick={handleDemoRegister}
            className="text-xs text-ink/60 hover:text-coral transition inline-flex items-center gap-1"
          >
            <span>Testing the platform?</span>
            <span className="font-semibold text-coral underline">
              Instant Demo Access (No wait) →
            </span>
          </button>
        </div>
      </form>
    </AuthLayout>
  );
}

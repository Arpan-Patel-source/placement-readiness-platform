import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState, useEffect, useRef } from "react";

import { api, authStorage, prewarmBackend } from "@/lib/api";
import { AuthLayout } from "@/components/AuthLayout";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Loader2, ArrowRight } from "lucide-react";

export const Route = createFileRoute("/login")({
  head: () => ({
    meta: [
      { title: "Sign in — PlacementAI" },
      {
        name: "description",
        content: "Sign in to PlacementAI to continue your placement preparation journey.",
      },
      { property: "og:title", content: "Sign in — PlacementAI" },
      {
        property: "og:description",
        content: "Access your resume reviews, mock interviews and practice progress.",
      },
    ],
  }),
  component: LoginPage,
});

function LoginPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
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

  // Load Google GSI script and render the official Google button
  useEffect(() => {
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
        text: "signin_with",
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
  }, []);

  const handleGoogleCredential = async (response: { credential: string }) => {
    setError(null);
    setOfflineNotice(false);
    setGoogleLoading(true);
    try {
      const result = await api.googleAuth(response.credential);
      if (result.token) {
        navigate({ to: "/dashboard" });
      } else if (result.isNewUser) {
        // Fallback for pre-filled registration if token is not issued yet
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
      } else {
        const friendlyMsg = errMsg.includes("violates not-null constraint") || errMsg.includes("could not execute statement")
          ? "Unable to complete Google sign-in due to a server account setup error. Please try again."
          : errMsg;
        setError(friendlyMsg || "Google sign-in failed. Please try again.");
      }
    } finally {
      setGoogleLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setOfflineNotice(false);
    setLoading(true);

    try {
      await api.login({ email, password });
      navigate({ to: "/dashboard" });
    } catch (err: any) {
      console.warn("Backend login error:", err);
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
        setError(errMsg || "Invalid email or password");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleDemoLogin = () => {
    authStorage.setSession(
      {
        token: "demo-jwt-token",
        email: email || "student@placementai.edu",
        role: "STUDENT",
      },
      "Demo Student",
    );
    navigate({ to: "/dashboard" });
  };

  const googleClientId = import.meta.env["VITE_GOOGLE_CLIENT_ID"];
  const googleEnabled = googleClientId && googleClientId !== "YOUR_GOOGLE_CLIENT_ID_HERE";

  return (
    <AuthLayout
      eyebrow="WELCOME BACK"
      title="Continue your"
      highlight="journey"
      subtitle="Sign in to pick up your preparation right where you left off."
      footer={
        <>
          New to PlacementAI?{" "}
          <Link to="/register" className="font-semibold text-coral">
            Create an account
          </Link>
        </>
      }
    >
      <form className="space-y-5" onSubmit={handleSubmit}>
        {error && (
          <div className="rounded-xl border border-destructive/20 bg-destructive/10 p-3 text-sm text-destructive">
            {error}
          </div>
        )}

        {/* Google Sign-In button */}
        {googleEnabled && (
          <div className="space-y-3">
            <div
              ref={googleBtnRef}
              className="flex w-full justify-center"
              style={{ minHeight: 44 }}
            />
            {googleLoading && (
              <div className="flex items-center justify-center gap-2 text-xs text-ink/60">
                <Loader2 className="size-3.5 animate-spin" />
                Signing in with Google...
              </div>
            )}
            <div className="relative flex items-center gap-3">
              <div className="h-px flex-1 bg-border" />
              <span className="text-xs text-ink/40 font-medium">or sign in with email</span>
              <div className="h-px flex-1 bg-border" />
            </div>
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
                  continue instantly in Demo Mode or configure your Render backend URL below.
                </>
              )}
            </p>
            <div className="flex flex-wrap items-center gap-2 pt-1">
              <button
                type="button"
                onClick={handleDemoLogin}
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
                      setError("Backend URL saved! You can try signing in now.");
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
          <Label htmlFor="email">Email</Label>
          <Input
            id="email"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="you@college.edu"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="password">Password</Label>
          <Input
            id="password"
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
          />
        </div>

        <div className="flex items-center justify-between">
          <label className="flex items-center gap-2 text-sm text-ink/70">
            <Checkbox id="remember" /> Remember me
          </label>
          <Link to="/forgot-password" className="text-sm font-medium text-coral hover:underline">
            Forgot password?
          </Link>
        </div>

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
                  onClick={handleDemoLogin}
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
          disabled={loading}
          className="inline-flex w-full items-center justify-center gap-2 rounded-full bg-coral py-3 text-sm font-semibold text-primary-foreground shadow-md transition-transform hover:-translate-y-0.5 disabled:opacity-70"
        >
          {loading ? (
            <>
              <Loader2 className="size-4 animate-spin" />
              <span>Signing in... {loadingSeconds > 0 ? `(${loadingSeconds}s)` : ""}</span>
            </>
          ) : (
            "Sign in"
          )}
        </button>

        <div className="text-center pt-1">
          <button
            type="button"
            onClick={handleDemoLogin}
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

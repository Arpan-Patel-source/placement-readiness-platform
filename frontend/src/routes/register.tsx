import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState, useEffect } from "react";

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
  const [name, setName] = useState("");
  const [college, setCollege] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [termsAccepted, setTermsAccepted] = useState(true);
  const [loading, setLoading] = useState(false);
  const [loadingSeconds, setLoadingSeconds] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [offlineNotice, setOfflineNotice] = useState(false);
  const [showServerConfig, setShowServerConfig] = useState(false);
  const [customBackendUrl, setCustomBackendUrl] = useState(() => api.getApiBaseUrl());

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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setOfflineNotice(false);

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

  return (
    <AuthLayout
      eyebrow="GET STARTED"
      title="Start your"
      highlight="journey"
      subtitle="Create a free account and get your first AI resume review in minutes."
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
        {error && (
          <div className="rounded-xl border border-destructive/20 bg-destructive/10 p-3 text-sm text-destructive">
            {error}
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
                    cd backend && ./mvnw spring-boot:run
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
            placeholder="At least 6 characters"
          />
        </div>

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

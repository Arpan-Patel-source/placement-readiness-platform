import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState } from "react";

import { api, authStorage } from "@/lib/api";
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
  const [error, setError] = useState<string | null>(null);
  const [offlineNotice, setOfflineNotice] = useState(false);

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
      if (err.message?.includes("Failed to fetch") || err.message?.includes("NetworkError") || err.message?.includes("fetch")) {
        setOfflineNotice(true);
      } else {
        setError(err.message || "Invalid email or password");
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
      "Azhar Khan"
    );
    navigate({ to: "/dashboard" });
  };

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

        {offlineNotice && (
          <div className="space-y-3 rounded-2xl border border-border bg-peach/40 p-4 text-sm text-ink">
            <p className="font-semibold text-coral">Backend server is not running on localhost:8080</p>
            <p className="text-xs text-ink/75">
              To connect live, start the Spring Boot app:
              <br />
              <code className="mt-1 inline-block rounded bg-background/80 px-2 py-0.5 font-mono text-[11px]">
                cd backend && ./mvnw spring-boot:run
              </code>
            </p>
            <button
              type="button"
              onClick={handleDemoLogin}
              className="inline-flex items-center gap-1.5 rounded-full bg-coral px-4 py-1.5 text-xs font-semibold text-primary-foreground shadow-sm transition hover:bg-coral/90"
            >
              Enter Demo Mode <ArrowRight className="size-3" />
            </button>
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

        <button
          type="submit"
          disabled={loading}
          className="inline-flex w-full items-center justify-center gap-2 rounded-full bg-coral py-3 text-sm font-semibold text-primary-foreground shadow-md transition-transform hover:-translate-y-0.5 disabled:opacity-70"
        >
          {loading ? (
            <>
              <Loader2 className="size-4 animate-spin" /> Signing in...
            </>
          ) : (
            "Sign in"
          )}
        </button>
      </form>
    </AuthLayout>
  );
}


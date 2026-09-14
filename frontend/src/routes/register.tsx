import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState } from "react";

import { api, authStorage } from "@/lib/api";
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
  const [error, setError] = useState<string | null>(null);
  const [offlineNotice, setOfflineNotice] = useState(false);

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
      if (err.message?.includes("Failed to fetch") || err.message?.includes("NetworkError") || err.message?.includes("fetch")) {
        setOfflineNotice(true);
      } else {
        setError(err.message || "Failed to create account. Please check your details.");
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
      name || "Azhar Khan"
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
              onClick={handleDemoRegister}
              className="inline-flex items-center gap-1.5 rounded-full bg-coral px-4 py-1.5 text-xs font-semibold text-primary-foreground shadow-sm transition hover:bg-coral/90"
            >
              Enter Demo Mode <ArrowRight className="size-3" />
            </button>
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

        <button
          type="submit"
          disabled={loading || !termsAccepted}
          className="inline-flex w-full items-center justify-center gap-2 rounded-full bg-coral py-3 text-sm font-semibold text-primary-foreground shadow-md transition-transform hover:-translate-y-0.5 disabled:opacity-70"
        >
          {loading ? (
            <>
              <Loader2 className="size-4 animate-spin" /> Creating account...
            </>
          ) : (
            "Create account"
          )}
        </button>
      </form>
    </AuthLayout>
  );
}


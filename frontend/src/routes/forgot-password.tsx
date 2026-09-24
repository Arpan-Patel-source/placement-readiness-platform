import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState } from "react";

import { api, authStorage } from "@/lib/api";
import { AuthLayout } from "@/components/AuthLayout";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Loader2, ArrowRight, CheckCircle2, KeyRound, Mail, Lock } from "lucide-react";

export const Route = createFileRoute("/forgot-password")({
  head: () => ({
    meta: [
      { title: "Reset Password — PlacementAI" },
      {
        name: "description",
        content: "Reset your PlacementAI account password securely.",
      },
      { property: "og:title", content: "Reset Password — PlacementAI" },
      {
        property: "og:description",
        content: "Reset your PlacementAI account password securely.",
      },
    ],
  }),
  component: ForgotPasswordPage,
});

function ForgotPasswordPage() {
  const navigate = useNavigate();

  // Step 1: Request code | Step 2: Enter token & new password | Step 3: Success
  const [step, setStep] = useState<"request" | "reset" | "success">("request");

  const [email, setEmail] = useState("");
  const [token, setToken] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [generatedCode, setGeneratedCode] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [offlineNotice, setOfflineNotice] = useState(false);

  // Step 1: Request Reset Code
  const handleRequestCode = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setOfflineNotice(false);
    setLoading(true);

    try {
      const response = await api.forgotPassword(email);
      if (response.resetToken) {
        setGeneratedCode(response.resetToken);
        setToken(response.resetToken);
      }
      setStep("reset");
    } catch (err: any) {
      console.warn("Forgot password request error:", err);
      if (
        err.message?.includes("Failed to fetch") ||
        err.message?.includes("NetworkError") ||
        err.message?.includes("fetch")
      ) {
        setOfflineNotice(true);
      } else {
        setError(err.message || "Failed to process request. Please check your email address.");
      }
    } finally {
      setLoading(false);
    }
  };

  // Demo Fallback for Step 1
  const handleDemoRequest = () => {
    const demoCode = "DEMO99";
    setGeneratedCode(demoCode);
    setToken(demoCode);
    setStep("reset");
  };

  // Step 2: Reset Password with Code
  const handleResetPassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setOfflineNotice(false);

    if (newPassword.length < 6) {
      setError("New password must be at least 6 characters long.");
      return;
    }

    if (newPassword !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);

    try {
      await api.resetPassword({
        email,
        token: token.trim(),
        newPassword,
      });
      setStep("success");
    } catch (err: any) {
      console.warn("Reset password submission error:", err);
      if (
        err.message?.includes("Failed to fetch") ||
        err.message?.includes("NetworkError") ||
        err.message?.includes("fetch")
      ) {
        // In offline mode with demo code, simulate successful password reset
        if (token === generatedCode) {
          authStorage.setSession(
            {
              token: "demo-jwt-token",
              email: email || "student@placementai.edu",
              role: "STUDENT",
            },
            "Azhar Khan",
          );
          setStep("success");
        } else {
          setOfflineNotice(true);
        }
      } else {
        setError(err.message || "Failed to reset password. Please verify your reset code.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout
      eyebrow="ACCOUNT RECOVERY"
      title="Reset your"
      highlight="password"
      subtitle={
        step === "request"
          ? "Enter your registered email and we'll send you a password reset code."
          : step === "reset"
            ? "Enter the 6-character code and choose a new password."
            : "Your password has been reset successfully!"
      }
      footer={
        <>
          Remember your password?{" "}
          <Link to="/login" className="font-semibold text-coral">
            Sign in
          </Link>
        </>
      }
    >
      {step === "request" && (
        <form className="space-y-5" onSubmit={handleRequestCode}>
          {error && (
            <div className="rounded-xl border border-destructive/20 bg-destructive/10 p-3 text-sm text-destructive">
              {error}
            </div>
          )}

          {offlineNotice && (
            <div className="space-y-3 rounded-2xl border border-border bg-peach/40 p-4 text-sm text-ink">
              <p className="font-semibold text-coral">
                Backend server is not running on localhost:8080
              </p>
              <p className="text-xs text-ink/75">
                To test live with Spring Boot:
                <br />
                <code className="mt-1 inline-block rounded bg-background/80 px-2 py-0.5 font-mono text-[11px]">
                  cd backend && ./mvnw spring-boot:run
                </code>
              </p>
              <button
                type="button"
                onClick={handleDemoRequest}
                className="inline-flex items-center gap-1.5 rounded-full bg-coral px-4 py-1.5 text-xs font-semibold text-primary-foreground shadow-sm transition hover:bg-coral/90"
              >
                Test in Demo Mode <ArrowRight className="size-3" />
              </button>
            </div>
          )}

          <div className="space-y-2">
            <Label htmlFor="email" className="flex items-center gap-1.5">
              <Mail className="size-3.5 text-coral" /> Email address
            </Label>
            <Input
              id="email"
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@college.edu"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="inline-flex w-full items-center justify-center gap-2 rounded-full bg-coral py-3 text-sm font-semibold text-primary-foreground shadow-md transition-transform hover:-translate-y-0.5 disabled:opacity-70"
          >
            {loading ? (
              <>
                <Loader2 className="size-4 animate-spin" /> Sending code...
              </>
            ) : (
              <>
                Send Reset Code <ArrowRight className="size-4" />
              </>
            )}
          </button>
        </form>
      )}

      {step === "reset" && (
        <form className="space-y-5" onSubmit={handleResetPassword}>
          {generatedCode && (
            <div className="rounded-2xl border border-mint bg-mint/40 p-4 text-sm text-ink">
              <p className="font-semibold text-ink">Password reset code ready:</p>
              <div className="mt-2 flex items-center justify-between rounded-xl bg-card px-3 py-2">
                <span className="font-mono text-base font-bold tracking-widest text-coral">
                  {generatedCode}
                </span>
                <span className="text-xs text-muted-foreground">Valid for 15 minutes</span>
              </div>
            </div>
          )}

          {error && (
            <div className="rounded-xl border border-destructive/20 bg-destructive/10 p-3 text-sm text-destructive">
              {error}
            </div>
          )}

          <div className="space-y-2">
            <Label htmlFor="token" className="flex items-center gap-1.5">
              <KeyRound className="size-3.5 text-coral" /> 6-Character Reset Code
            </Label>
            <Input
              id="token"
              type="text"
              required
              maxLength={12}
              value={token}
              onChange={(e) => setToken(e.target.value.toUpperCase())}
              placeholder="e.g. 3F8A2B"
              className="font-mono tracking-wider uppercase"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="newPassword" className="flex items-center gap-1.5">
              <Lock className="size-3.5 text-coral" /> New Password
            </Label>
            <Input
              id="newPassword"
              type="password"
              required
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="At least 6 characters"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="confirmPassword">Confirm New Password</Label>
            <Input
              id="confirmPassword"
              type="password"
              required
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Re-enter password"
            />
          </div>

          <div className="flex flex-col gap-2 pt-2">
            <button
              type="submit"
              disabled={loading}
              className="inline-flex w-full items-center justify-center gap-2 rounded-full bg-coral py-3 text-sm font-semibold text-primary-foreground shadow-md transition-transform hover:-translate-y-0.5 disabled:opacity-70"
            >
              {loading ? (
                <>
                  <Loader2 className="size-4 animate-spin" /> Updating password...
                </>
              ) : (
                "Update Password"
              )}
            </button>

            <button
              type="button"
              onClick={() => {
                setError(null);
                setStep("request");
              }}
              className="py-1 text-center text-xs text-ink/70 hover:text-coral transition-colors"
            >
              Didn't receive code? Change email or request again
            </button>
          </div>
        </form>
      )}

      {step === "success" && (
        <div className="space-y-6 text-center py-4">
          <div className="mx-auto flex size-16 items-center justify-center rounded-full bg-mint text-ink">
            <CheckCircle2 className="size-8 text-coral" />
          </div>

          <div className="space-y-2">
            <h3 className="font-display text-xl font-bold text-ink">Password Changed!</h3>
            <p className="text-sm text-ink/70">
              Your password has been successfully updated. You can now sign in with your new
              credentials.
            </p>
          </div>

          <Link
            to="/login"
            className="inline-flex w-full items-center justify-center gap-2 rounded-full bg-coral py-3 text-sm font-semibold text-primary-foreground shadow-md transition-transform hover:-translate-y-0.5"
          >
            Sign in now <ArrowRight className="size-4" />
          </Link>
        </div>
      )}
    </AuthLayout>
  );
}

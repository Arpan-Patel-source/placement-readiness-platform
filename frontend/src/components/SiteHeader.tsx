import { Link, useNavigate } from "@tanstack/react-router";
import { Search, User, LogOut } from "lucide-react";
import { useEffect, useState } from "react";
import { authStorage, UserSession } from "@/lib/api";

const nav = ["Why PlacementAI", "Features", "Resources", "Success Stories", "About Us"];

export function Logo() {
  return (
    <Link to="/" className="shrink-0">
      <div className="flex items-baseline gap-1">
        <span className="font-display text-xl font-extrabold tracking-tight text-ink">
          Placement<span className="text-coral">AI</span>
        </span>
        <span className="text-coral">↗</span>
      </div>
      <p className="text-[0.55rem] tracking-[0.28em] text-muted-foreground">
        PREPARE · PRACTICE · PROGRESS
      </p>
    </Link>
  );
}

export function SiteHeader() {
  const navigate = useNavigate();
  const [session, setSession] = useState<UserSession | null>(null);

  useEffect(() => {
    setSession(authStorage.getUser());
  }, []);

  const handleLogout = () => {
    authStorage.clearSession();
    setSession(null);
    navigate({ to: "/" });
  };

  return (
    <header className="border-b border-border/60 bg-background/90 backdrop-blur">
      <div className="mx-auto flex max-w-7xl items-center gap-6 px-5 py-3">
        <Logo />
        <nav className="hidden flex-1 items-center gap-6 lg:flex">
          {nav.map((item) => (
            <span
              key={item}
              className="cursor-pointer text-sm font-medium text-ink/80 transition-colors hover:text-coral"
            >
              {item}
            </span>
          ))}
        </nav>
        <div className="ml-auto flex items-center gap-3">
          <Link
            to="/dashboard"
            className="hidden text-sm font-medium text-ink/80 transition-colors hover:text-coral sm:block"
          >
            Dashboard
          </Link>
          <Search className="hidden size-4 text-ink/70 sm:block" />

          {session ? (
            <div className="flex items-center gap-2.5">
              <Link
                to="/dashboard"
                className="rounded-full bg-peach/70 px-3 py-1 text-xs font-semibold text-ink hover:bg-peach"
              >
                Hi, {session.name}
              </Link>
              <button
                type="button"
                onClick={handleLogout}
                title="Sign out"
                className="rounded-full p-1.5 text-ink/70 transition-colors hover:bg-muted hover:text-coral"
              >
                <LogOut className="size-4" />
              </button>
            </div>
          ) : (
            <>
              <Link to="/login" aria-label="Sign in">
                <User className="size-4 text-ink/70 transition-colors hover:text-coral" />
              </Link>
              <Link
                to="/register"
                className="rounded-full bg-coral px-4 py-2 text-sm font-semibold text-primary-foreground shadow-sm transition-colors hover:bg-coral/90"
              >
                Get Started
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}

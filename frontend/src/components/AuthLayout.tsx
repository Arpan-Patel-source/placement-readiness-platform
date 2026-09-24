import type { ReactNode } from "react";
import { Link } from "@tanstack/react-router";

import { Logo } from "@/components/SiteHeader";
import heroImage from "@/assets/hero-journey.jpg";

export function AuthLayout({
  eyebrow,
  title,
  highlight,
  subtitle,
  children,
  footer,
}: {
  eyebrow: string;
  title: string;
  highlight: string;
  subtitle: string;
  children: ReactNode;
  footer: ReactNode;
}) {
  return (
    <div className="grid min-h-screen lg:grid-cols-2">
      {/* Illustration side */}
      <aside className="relative hidden flex-col justify-between bg-peach/50 p-10 lg:flex">
        <Logo />
        <div>
          <img
            src={heroImage}
            alt="Student walking a path of signposts toward a city skyline"
            width={1280}
            height={896}
            loading="lazy"
            className="w-full rounded-3xl object-cover"
          />
          <p className="mt-8 font-hand text-2xl text-ink/70">Same students, brighter futures.</p>
        </div>
        <p className="text-[0.6rem] tracking-[0.3em] text-ink/40">
          PREPARE TODAY FOR A BRIGHTER TOMORROW
        </p>
      </aside>

      {/* Form side */}
      <main className="flex flex-col justify-center bg-background px-6 py-12 sm:px-14">
        <div className="mx-auto w-full max-w-md">
          <div className="lg:hidden">
            <Logo />
          </div>
          <p className="mt-8 text-xs font-semibold tracking-[0.22em] text-coral lg:mt-0">
            {eyebrow}
          </p>
          <h1 className="mt-3 font-display text-4xl font-extrabold leading-tight text-ink">
            {title} <span className="text-coral">{highlight}</span>
          </h1>
          <p className="mt-3 text-sm text-ink/70">{subtitle}</p>

          <div className="mt-8">{children}</div>

          <p className="mt-8 text-sm text-ink/70">{footer}</p>
          <Link to="/" className="mt-6 inline-block text-xs text-muted-foreground hover:text-coral">
            ← Back to home
          </Link>
        </div>
      </main>
    </div>
  );
}

import { createFileRoute, Link } from "@tanstack/react-router";
import {
  FileText,
  MessagesSquare,
  Code2,
  BarChart3,
  Users,
  Play,
  ArrowRight,
  Sprout,
  FlaskConical,
  Sun,
} from "lucide-react";

import { SiteHeader, Logo } from "@/components/SiteHeader";
import heroImage from "@/assets/hero-journey.jpg";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "PlacementAI — From Campus to Career" },
      {
        name: "description",
        content:
          "PlacementAI is your AI-powered placement preparation companion: resume analysis, mock interviews, coding practice, aptitude and HR prep in one platform.",
      },
      { property: "og:title", content: "PlacementAI — From Campus to Career" },
      {
        property: "og:description",
        content: "Prepare, practice and progress with an AI-powered placement platform built for students.",
      },
    ],
  }),
  component: Landing,
});

const features = [
  {
    icon: FileText,
    title: "Resume Analysis",
    text: "Get AI feedback and improve your resume",
    bg: "bg-mint",
  },
  {
    icon: MessagesSquare,
    title: "Mock Interviews",
    text: "Practice with AI interviewers and get real-time feedback",
    bg: "bg-blush",
  },
  {
    icon: Code2,
    title: "Coding Practice",
    text: "Solve problems and build your skills",
    bg: "bg-sky",
  },
  {
    icon: BarChart3,
    title: "Aptitude Preparation",
    text: "Practice, learn and track your progress",
    bg: "bg-peach",
  },
  {
    icon: Users,
    title: "HR Preparation",
    text: "Be ready for real-world conversations",
    bg: "bg-sage",
  },
];

const pillars = [
  { icon: Sprout, title: "Learn", text: "Build the right skills", bg: "bg-mint" },
  { icon: FlaskConical, title: "Practice", text: "Gain real confidence", bg: "bg-blush" },
  { icon: Sun, title: "Get Placed", text: "Turn your efforts into opportunities", bg: "bg-peach" },
];

function Landing() {
  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />

      <main>
        {/* Hero */}
        <section className="relative overflow-hidden bg-peach/50">
          <div className="mx-auto grid max-w-7xl items-center gap-8 px-5 py-14 lg:grid-cols-2 lg:py-20">
            <div>
              <h1 className="font-display text-5xl font-extrabold leading-[1.05] text-ink sm:text-6xl">
                From Campus
                <br />
                to <span className="text-coral">Career</span>
              </h1>
              <p className="mt-5 max-w-md text-base text-ink/75">
                Your AI-powered placement preparation companion for a brighter tomorrow.
              </p>
              <div className="mt-7 flex flex-wrap items-center gap-5">
                <Link
                  to="/register"
                  className="inline-flex items-center gap-2 rounded-full bg-coral px-6 py-3 text-sm font-semibold text-primary-foreground shadow-md transition-transform hover:-translate-y-0.5"
                >
                  Start Your Journey <ArrowRight className="size-4" />
                </Link>
                <button className="inline-flex items-center gap-2 text-sm font-semibold text-ink">
                  <span className="flex size-8 items-center justify-center rounded-full border-2 border-coral text-coral">
                    <Play className="size-3 fill-current" />
                  </span>
                  Watch Video
                </button>
              </div>
              <p className="mt-8 text-[0.65rem] tracking-[0.32em] text-ink/45">
                PREPARE TODAY FOR A BRIGHTER TOMORROW
              </p>
            </div>

            <div className="relative">
              <img
                src={heroImage}
                alt="Student walking a path of signposts from campus toward a city skyline"
                width={1280}
                height={896}
                className="w-full rounded-3xl object-cover"
              />
              <span className="absolute -top-2 right-2 hidden font-hand text-xl leading-tight text-ink/70 lg:block">
                Same
                <br />
                Students
                <br />
                Brighter
                <br />
                Futures
              </span>
            </div>
          </div>
        </section>

        {/* Feature strip */}
        <section className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5">
          {features.map(({ icon: Icon, title, text, bg }) => (
            <div key={title} className={`${bg} group p-7`}>
              <Icon className="size-7 text-ink/80" strokeWidth={1.6} />
              <h3 className="mt-5 font-display text-base font-bold text-ink">{title}</h3>
              <p className="mt-1 text-sm leading-snug text-ink/70">{text}</p>
              <ArrowRight className="mt-5 size-4 text-coral transition-transform group-hover:translate-x-1" />
            </div>
          ))}
        </section>

        {/* Mission */}
        <section className="bg-cream">
          <div className="mx-auto grid max-w-7xl gap-12 px-5 py-16 lg:grid-cols-2">
            <div>
              <p className="text-xs font-semibold tracking-[0.22em] text-coral">OUR MISSION</p>
              <h2 className="mt-4 font-display text-4xl font-extrabold leading-tight text-ink">
                Empowering Students
                <br />
                for a <span className="text-coral">Brighter Tomorrow</span>
              </h2>
              <p className="mt-5 max-w-lg text-sm leading-relaxed text-ink/70">
                PlacementAI brings everything you need for placement preparation into one unified,
                AI-powered platform — so you can focus on what truly matters: your growth.
              </p>
            </div>

            <div>
              <div className="grid grid-cols-3 divide-x divide-border">
                {pillars.map(({ icon: Icon, title, text, bg }) => (
                  <div key={title} className="px-3 text-center">
                    <span
                      className={`mx-auto flex size-12 items-center justify-center rounded-full ${bg}`}
                    >
                      <Icon className="size-5 text-ink/80" strokeWidth={1.6} />
                    </span>
                    <h3 className="mt-3 font-display text-sm font-bold text-ink">{title}</h3>
                    <p className="mt-1 text-xs text-ink/65">{text}</p>
                  </div>
                ))}
              </div>
              <p className="mt-10 text-center text-[0.6rem] tracking-[0.3em] text-ink/40">
                MORE THAN PLACEMENTS — A BRIGHTER YOU
              </p>
            </div>
          </div>
        </section>

        {/* CTA */}
        <section className="bg-mint/60">
          <div className="mx-auto flex max-w-7xl flex-col items-center gap-5 px-5 py-14 text-center">
            <h2 className="font-display text-3xl font-extrabold text-ink">
              Ready to start preparing?
            </h2>
            <p className="max-w-md text-sm text-ink/70">
              Create a free account and get your first AI resume review in minutes.
            </p>
            <div className="flex flex-wrap justify-center gap-3">
              <Link
                to="/register"
                className="rounded-full bg-coral px-6 py-3 text-sm font-semibold text-primary-foreground shadow-md"
              >
                Create free account
              </Link>
              <Link
                to="/login"
                className="rounded-full border border-ink/20 px-6 py-3 text-sm font-semibold text-ink"
              >
                I already have one
              </Link>
            </div>
          </div>
        </section>
      </main>

      <footer className="border-t border-border/60 bg-background">
        <div className="mx-auto flex max-w-7xl flex-col gap-4 px-5 py-8 sm:flex-row sm:items-center sm:justify-between">
          <Logo />
          <p className="text-xs text-muted-foreground">
            © {new Date().getFullYear()} PlacementAI. Prepare · Practice · Progress.
          </p>
        </div>
      </footer>
    </div>
  );
}

import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useState, useRef } from "react";
import {
  Mic,
  MicOff,
  Volume2,
  VolumeX,
  Play,
  Square,
  Sparkles,
  CheckCircle2,
  AlertTriangle,
  Clock,
  RotateCcw,
  ArrowRight,
  ArrowLeft,
  Activity,
  Award,
  BarChart2,
  Info,
  Layers,
  ThumbsUp,
  MessageSquare,
} from "lucide-react";
import { SiteHeader } from "../components/SiteHeader";

export const Route = createFileRoute("/voice-interview")({
  component: VoiceInterviewPage,
});

const HR_VOICE_QUESTIONS = [
  "Tell me about yourself and walk me through your engineering background.",
  "Describe a time when you faced a difficult bug or production outage under tight deadlines.",
  "Why should our company hire you over other qualified engineering graduates?",
  "Tell me about a time you had to work with a difficult teammate or disagreed on technical design.",
  "What is your greatest technical strength and your most significant area for improvement?",
  "How do you prioritize competing deadlines when multiple project milestones require immediate attention?",
  "Tell me about a project that did not meet expectations or failed. What was your key takeaway?",
  "Explain how you stay updated with emerging technologies and new engineering frameworks.",
  "Describe a situation where you had to explain a complex technical concept to a non-technical stakeholder.",
  "Where do you see yourself in 3 to 5 years in your software engineering career?",
  "Describe an instance where you optimized an algorithm or database query to improve response time.",
  "What motivates you to deliver high-quality code and how do you handle constructive code review critique?"
];

const FILLER_WORDS = ["um", "uh", "like", "you know", "actually", "basically", "literally", "sort of", "kind of", "so yeah"];

interface VoiceMetrics {
  totalWords: number;
  wpm: number;
  fillerCount: number;
  detectedFillers: Record<string, number>;
  confidenceScore: number;
  fluencyScore: number;
  paceVerdict: "Too Slow" | "Ideal Pace" | "Too Fast";
  durationSeconds: number;
}

function VoiceInterviewPage() {
  const [questionIdx, setQuestionIdx] = useState(0);
  const [isRecording, setIsRecording] = useState(false);
  const [transcript, setTranscript] = useState("");
  const [duration, setDuration] = useState(0);
  const [metrics, setMetrics] = useState<VoiceMetrics | null>(null);
  const [speechSupported, setSpeechSupported] = useState(true);
  const [isEvaluating, setIsEvaluating] = useState(false);
  const [simulatedVolume, setSimulatedVolume] = useState<number[]>([12, 24, 18, 35, 48, 20, 15, 30, 42, 25]);

  const recognitionRef = useRef<any>(null);
  const timerRef = useRef<any>(null);
  const waveIntervalRef = useRef<any>(null);

  useEffect(() => {
    // Check SpeechRecognition support in window
    const SpeechRecognition =
      (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;

    if (!SpeechRecognition) {
      setSpeechSupported(false);
    }
  }, []);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      stopRecordingCleanup();
    };
  }, []);

  const stopRecordingCleanup = () => {
    if (timerRef.current) clearInterval(timerRef.current);
    if (waveIntervalRef.current) clearInterval(waveIntervalRef.current);
    if (recognitionRef.current) {
      try {
        recognitionRef.current.stop();
      } catch {}
    }
  };

  const startRecording = () => {
    setTranscript("");
    setMetrics(null);
    setDuration(0);
    setIsRecording(true);

    // Timer
    timerRef.current = setInterval(() => {
      setDuration((prev) => prev + 1);
    }, 1000);

    // Waveform simulation
    waveIntervalRef.current = setInterval(() => {
      setSimulatedVolume(
        Array.from({ length: 16 }, () => Math.floor(Math.random() * 60) + 10)
      );
    }, 120);

    // Initialize Web Speech API if supported
    const SpeechRecognition =
      (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;

    if (SpeechRecognition) {
      try {
        const recognition = new SpeechRecognition();
        recognition.continuous = true;
        recognition.interimResults = true;
        recognition.lang = "en-US";

        recognition.onresult = (event: any) => {
          let currentText = "";
          for (let i = 0; i < event.results.length; i++) {
            currentText += event.results[i][0].transcript + " ";
          }
          setTranscript(currentText.trim());
        };

        recognition.onerror = (err: any) => {
          console.warn("Speech recognition error:", err);
        };

        recognition.start();
        recognitionRef.current = recognition;
      } catch (e) {
        console.error("Failed to start speech recognition:", e);
      }
    }
  };

  const stopRecording = () => {
    stopRecordingCleanup();
    setIsRecording(false);
    evaluateSpeech();
  };

  const evaluateSpeech = () => {
    setIsEvaluating(true);

    setTimeout(() => {
      const text = transcript.trim();
      const words = text ? text.split(/\s+/).filter(Boolean) : [];
      const wordCount = words.length;

      const elapsedMinutes = Math.max(0.1, duration / 60);
      const wpm = Math.round(wordCount / elapsedMinutes);

      // Filler words detection
      const detectedFillers: Record<string, number> = {};
      let totalFillers = 0;
      const lowerText = text.toLowerCase();

      FILLER_WORDS.forEach((filler) => {
        const regex = new RegExp(`\\b${filler}\\b`, "gi");
        const matches = lowerText.match(regex);
        if (matches && matches.length > 0) {
          detectedFillers[filler] = matches.length;
          totalFillers += matches.length;
        }
      });

      // Pace assessment (ideal is 120 - 150 wpm)
      let paceVerdict: "Too Slow" | "Ideal Pace" | "Too Fast" = "Ideal Pace";
      if (wpm < 110) paceVerdict = "Too Slow";
      else if (wpm > 165) paceVerdict = "Too Fast";

      // Confidence & Fluency Scoring
      let confidenceScore = 85;
      if (totalFillers > 5) confidenceScore -= 15;
      else if (totalFillers > 2) confidenceScore -= 8;
      if (paceVerdict !== "Ideal Pace") confidenceScore -= 10;
      if (wordCount < 40) confidenceScore -= 15;
      confidenceScore = Math.max(35, Math.min(98, confidenceScore));

      let fluencyScore = 88;
      const fillerDensity = wordCount > 0 ? (totalFillers / wordCount) * 100 : 0;
      if (fillerDensity > 8) fluencyScore -= 20;
      else if (fillerDensity > 4) fluencyScore -= 10;
      fluencyScore = Math.max(40, Math.min(96, fluencyScore));

      setMetrics({
        totalWords: wordCount,
        wpm,
        fillerCount: totalFillers,
        detectedFillers,
        confidenceScore,
        fluencyScore,
        paceVerdict,
        durationSeconds: duration,
      });

      setIsEvaluating(false);
    }, 600);
  };

  const formatTime = (secs: number) => {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m.toString().padStart(2, "0")}:${s.toString().padStart(2, "0")}`;
  };

  return (
    <div className="min-h-screen bg-[#0d1117] text-slate-100 flex flex-col font-sans selection:bg-coral/30 selection:text-coral-200">
      <SiteHeader />

      {/* Hero Subheader */}
      <div className="border-b border-slate-800 bg-[#161b22]/70 backdrop-blur px-6 py-4">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <Link
              to="/mock-interview"
              className="p-2 rounded-lg bg-slate-800/80 hover:bg-slate-700 text-slate-400 hover:text-white transition-colors"
              title="Back to Mock Interview"
            >
              <ArrowLeft className="size-4" />
            </Link>
            <div>
              <div className="flex items-center gap-2">
                <span className="p-1.5 rounded-md bg-coral/20 text-coral">
                  <Mic className="size-5" />
                </span>
                <h1 className="text-xl font-bold tracking-tight text-white">
                  Voice-Based Interview & Speech Analytics
                </h1>
                <span className="text-xs px-2 py-0.5 rounded-full bg-slate-800 text-coral font-medium border border-coral/30">
                  AI Voice Analyzer
                </span>
              </div>
              <p className="text-xs text-slate-400 mt-0.5">
                Practice verbal communication with live speech-to-text, speaking pace (WPM), filler word detection, and confidence scoring
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <Link
              to="/mock-interview"
              className="px-3.5 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold border border-slate-700 transition-colors"
            >
              Switch to Text Interview
            </Link>
          </div>
        </div>
      </div>

      {/* Main Container */}
      <div className="flex-1 max-w-5xl w-full mx-auto p-4 md:p-6 space-y-6">
        {!speechSupported && (
          <div className="bg-amber-950/30 border border-amber-800/40 rounded-xl p-4 flex items-start gap-3 text-xs text-amber-200">
            <Info className="size-5 text-amber-400 shrink-0 mt-0.5" />
            <div>
              <span className="font-bold text-amber-300">Browser Speech API Note: </span>
              Your current browser does not support the native Web Speech Recognition API. We recommend using Google Chrome or Microsoft Edge for live microphone transcription. You can still test voice evaluation by speaking or typing answers directly!
            </div>
          </div>
        )}

        {/* Question Banner */}
        <div className="bg-[#161b22] border border-slate-800 rounded-2xl p-6 shadow-sm space-y-3">
          <div className="flex items-center justify-between text-xs">
            <span className="font-bold uppercase tracking-wider text-coral">
              Question {questionIdx + 1} of {HR_VOICE_QUESTIONS.length}
            </span>
            <div className="flex items-center gap-1.5">
              <button
                onClick={() => {
                  stopRecordingCleanup();
                  setIsRecording(false);
                  setMetrics(null);
                  setTranscript("");
                  setQuestionIdx((p) => Math.max(0, p - 1));
                }}
                disabled={questionIdx === 0}
                className="px-2.5 py-1 rounded bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold disabled:opacity-30"
              >
                Prev
              </button>
              <button
                onClick={() => {
                  stopRecordingCleanup();
                  setIsRecording(false);
                  setMetrics(null);
                  setTranscript("");
                  setQuestionIdx((p) => Math.min(HR_VOICE_QUESTIONS.length - 1, p + 1));
                }}
                disabled={questionIdx === HR_VOICE_QUESTIONS.length - 1}
                className="px-2.5 py-1 rounded bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold disabled:opacity-30"
              >
                Next
              </button>
            </div>
          </div>

          <h2 className="text-xl font-bold text-white leading-relaxed">
            "{HR_VOICE_QUESTIONS[questionIdx]}"
          </h2>
          <p className="text-xs text-slate-400">
            Click the microphone below and deliver your response aloud as if speaking with a senior recruiter or hiring manager.
          </p>
        </div>

        {/* Voice Recorder Studio Card */}
        <div className="bg-[#161b22] border border-slate-800 rounded-2xl p-6 md:p-8 flex flex-col items-center justify-center text-center space-y-6 shadow-lg">
          {/* Audio Waveform Visualization */}
          <div className="h-16 flex items-center justify-center gap-1.5 w-full max-w-md">
            {simulatedVolume.map((vol, idx) => (
              <div
                key={idx}
                className={`w-2 rounded-full transition-all duration-150 ${
                  isRecording
                    ? "bg-gradient-to-t from-coral to-rose-400"
                    : "bg-slate-800"
                }`}
                style={{
                  height: isRecording ? `${Math.max(10, vol)}px` : "12px",
                }}
              />
            ))}
          </div>

          {/* Record Control Button */}
          <div className="space-y-3">
            <button
              onClick={isRecording ? stopRecording : startRecording}
              className={`size-20 rounded-full flex items-center justify-center shadow-2xl transition-all ${
                isRecording
                  ? "bg-rose-600 hover:bg-rose-500 animate-pulse ring-8 ring-rose-500/20"
                  : "bg-coral hover:bg-coral/90 ring-8 ring-coral/20 hover:scale-105"
              }`}
            >
              {isRecording ? (
                <Square className="size-8 text-white fill-white" />
              ) : (
                <Mic className="size-8 text-white" />
              )}
            </button>

            <div>
              <div className="text-sm font-bold text-white">
                {isRecording ? "Listening & Recording..." : "Click Mic to Begin Answering"}
              </div>
              <div className="text-xs text-slate-400 font-mono mt-0.5">
                {isRecording ? formatTime(duration) : "Target length: 1 to 2 minutes"}
              </div>
            </div>
          </div>

          {/* Live Transcript Box */}
          <div className="w-full text-left bg-[#0d1117] border border-slate-800 rounded-xl p-4 space-y-2">
            <div className="flex items-center justify-between text-[11px] font-semibold text-slate-500 uppercase tracking-wider">
              <span>Live Speech Transcription</span>
              {isRecording && (
                <span className="text-coral flex items-center gap-1">
                  <span className="size-2 rounded-full bg-coral animate-ping" /> Live
                </span>
              )}
            </div>
            <textarea
              value={transcript}
              onChange={(e) => setTranscript(e.target.value)}
              placeholder={
                isRecording
                  ? "Speak clearly into your microphone..."
                  : "Your speech transcript will appear here. You can also type or edit before evaluation..."
              }
              rows={4}
              className="w-full bg-transparent text-xs text-slate-200 placeholder:text-slate-600 focus:outline-none resize-none leading-relaxed"
            />
          </div>

          {!isRecording && transcript.trim() && !metrics && (
            <button
              onClick={evaluateSpeech}
              disabled={isEvaluating}
              className="px-6 py-2.5 rounded-xl bg-coral hover:bg-coral/90 text-white text-xs font-bold shadow transition-colors flex items-center gap-2"
            >
              <Sparkles className="size-4" />
              {isEvaluating ? "Analyzing Speech..." : "Analyze Speech Metrics"}
            </button>
          )}
        </div>

        {/* Evaluation Metrics Card */}
        {metrics && (
          <div className="bg-[#161b22] border border-slate-800 rounded-2xl p-6 space-y-6 shadow-lg">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
              <div>
                <span className="text-xs font-bold uppercase tracking-wider text-coral">
                  AI Speech Diagnostics
                </span>
                <h3 className="text-lg font-bold text-white mt-1">
                  Vocal Performance Report
                </h3>
              </div>

              <div className="flex items-center gap-3">
                <div className="text-right">
                  <div className="text-[10px] uppercase font-bold text-slate-500">Speaking Score</div>
                  <div className="text-2xl font-black text-coral">
                    {Math.round((metrics.confidenceScore + metrics.fluencyScore) / 2)} / 100
                  </div>
                </div>
              </div>
            </div>

            {/* Core Metrics Grid */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              {/* Pace */}
              <div className="bg-[#0d1117] border border-slate-800 rounded-xl p-4 space-y-1">
                <div className="text-[10px] uppercase font-bold text-slate-500 flex items-center gap-1">
                  <Clock className="size-3 text-cyan-400" /> Speaking Pace
                </div>
                <div className="text-xl font-bold text-white">{metrics.wpm} <span className="text-xs font-normal text-slate-500">WPM</span></div>
                <div className={`text-[11px] font-semibold ${
                  metrics.paceVerdict === "Ideal Pace" ? "text-emerald-400" : "text-amber-400"
                }`}>
                  {metrics.paceVerdict} (120-150 target)
                </div>
              </div>

              {/* Filler Words */}
              <div className="bg-[#0d1117] border border-slate-800 rounded-xl p-4 space-y-1">
                <div className="text-[10px] uppercase font-bold text-slate-500 flex items-center gap-1">
                  <Activity className="size-3 text-amber-400" /> Filler Words
                </div>
                <div className="text-xl font-bold text-white">{metrics.fillerCount}</div>
                <div className="text-[11px] text-slate-400">
                  {metrics.fillerCount === 0 ? "Excellent clarity! 0 fillers" : "Detected vocal crutches"}
                </div>
              </div>

              {/* Confidence Score */}
              <div className="bg-[#0d1117] border border-slate-800 rounded-xl p-4 space-y-1">
                <div className="text-[10px] uppercase font-bold text-slate-500 flex items-center gap-1">
                  <Award className="size-3 text-coral" /> Confidence
                </div>
                <div className="text-xl font-bold text-white">{metrics.confidenceScore}%</div>
                <div className="text-[11px] text-emerald-400 font-semibold">
                  {metrics.confidenceScore >= 80 ? "Strong conviction" : "Good, practice pacing"}
                </div>
              </div>

              {/* Fluency Score */}
              <div className="bg-[#0d1117] border border-slate-800 rounded-xl p-4 space-y-1">
                <div className="text-[10px] uppercase font-bold text-slate-500 flex items-center gap-1">
                  <Sparkles className="size-3 text-violet-400" /> Fluency & Flow
                </div>
                <div className="text-xl font-bold text-white">{metrics.fluencyScore}%</div>
                <div className="text-[11px] text-slate-400">
                  {metrics.totalWords} words spoken
                </div>
              </div>
            </div>

            {/* Filler Words Breakdown */}
            {metrics.fillerCount > 0 && (
              <div className="bg-slate-800/40 border border-slate-700/60 rounded-xl p-4 space-y-2">
                <div className="text-xs font-bold text-amber-400 uppercase tracking-wider flex items-center gap-1.5">
                  <AlertTriangle className="size-4" /> Detected Fillers to Reduce
                </div>
                <div className="flex flex-wrap gap-2">
                  {Object.entries(metrics.detectedFillers).map(([word, count]) => (
                    <span
                      key={word}
                      className="px-2.5 py-1 rounded-lg bg-slate-900 border border-amber-500/30 text-xs text-amber-300 font-medium"
                    >
                      "{word}": <span className="font-bold">{count}x</span>
                    </span>
                  ))}
                </div>
              </div>
            )}

            {/* Recommendations */}
            <div className="space-y-2">
              <div className="text-xs font-bold text-slate-400 uppercase tracking-wider">
                Recruiter Recommendations
              </div>
              <ul className="text-xs text-slate-300 space-y-1.5 list-disc list-inside bg-[#0d1117] p-4 rounded-xl border border-slate-800 leading-relaxed">
                <li>
                  {metrics.wpm < 110
                    ? "Increase your tempo slightly. A conversational rhythm around 130 WPM conveys greater energy and confidence."
                    : metrics.wpm > 160
                    ? "Slow down intentionally between major points. Strategic 1-second pauses allow interviewers to absorb your key achievements."
                    : "Your speaking speed is well-calibrated and comfortable for remote technical interviews."}
                </li>
                <li>
                  {metrics.fillerCount > 3
                    ? "Replace filler sounds like 'um' or 'like' with silent pauses. In an interview, silence sounds thoughtful; fillers sound uncertain."
                    : "Clean vocal delivery with minimal filler friction. Keep this up!"}
                </li>
                <li>
                  Structure your vocal responses with a clear punchline first: summarize the outcome before narrating the technical steps.
                </li>
              </ul>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

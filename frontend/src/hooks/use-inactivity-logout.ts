import { useEffect, useRef, useCallback } from "react";
import { useNavigate } from "@tanstack/react-router";
import { authStorage } from "@/lib/api";

/**
 * Auto-logout hook — clears the session and redirects to the login page
 * after `timeoutMs` milliseconds of user inactivity.
 *
 * "Activity" is defined as any of: mouse movement, mouse click,
 * key press, scroll, or touch event.
 *
 * The hook only activates when the user is authenticated;
 * unauthenticated visitors are ignored.
 */
const ACTIVITY_EVENTS: (keyof WindowEventMap)[] = [
  "mousemove",
  "mousedown",
  "keydown",
  "scroll",
  "touchstart",
];

const TEN_MINUTES_MS = 10 * 60 * 1000;

export function useInactivityLogout(timeoutMs: number = TEN_MINUTES_MS) {
  const navigate = useNavigate();
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const logout = useCallback(() => {
    authStorage.clearSession();
    // Navigate to login with a query param so the login page can show a message
    navigate({ to: "/login" });
  }, [navigate]);

  const resetTimer = useCallback(() => {
    // Only run the timer for authenticated users
    if (!authStorage.isAuthenticated()) return;

    if (timerRef.current) {
      clearTimeout(timerRef.current);
    }
    timerRef.current = setTimeout(logout, timeoutMs);
  }, [logout, timeoutMs]);

  useEffect(() => {
    // Don't set up listeners on the server
    if (typeof window === "undefined") return;

    // Don't set up if user isn't logged in
    if (!authStorage.isAuthenticated()) return;

    // Start the initial timer
    resetTimer();

    // Attach activity listeners
    for (const event of ACTIVITY_EVENTS) {
      window.addEventListener(event, resetTimer, { passive: true });
    }

    return () => {
      // Cleanup: remove listeners and clear timer
      for (const event of ACTIVITY_EVENTS) {
        window.removeEventListener(event, resetTimer);
      }
      if (timerRef.current) {
        clearTimeout(timerRef.current);
      }
    };
  }, [resetTimer]);
}

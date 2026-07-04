import { useEffect, useMemo, useState } from "react";
import type { LoginResponse } from "@/api/types";
import { AUTH_SESSION_UPDATED_EVENT } from "@/api/auth-session";
import type { AuthSession } from "@/api/auth-session";

export const STORAGE_KEY = "medminder-auth-session";

function readStoredSession(): AuthSession | null {
  const value = window.localStorage.getItem(STORAGE_KEY);
  return value ? (JSON.parse(value) as AuthSession) : null;
}

export function useDemoAuth() {
  const [session, setSession] = useState<AuthSession | null>(() => readStoredSession());

  useEffect(() => {
    function handleSessionUpdated(event: Event) {
      const nextSession = (event as CustomEvent<AuthSession>).detail;
      if (nextSession) {
        setSession(nextSession);
      }
    }

    window.addEventListener(AUTH_SESSION_UPDATED_EVENT, handleSessionUpdated as EventListener);
    return () => {
      window.removeEventListener(AUTH_SESSION_UPDATED_EVENT, handleSessionUpdated as EventListener);
    };
  }, []);

  const value = useMemo(() => ({
    user: session?.profile ?? null,
    session,
    login(nextSession: AuthSession) {
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(nextSession));
      setSession(nextSession);
    },
    logout() {
      window.localStorage.removeItem(STORAGE_KEY);
      setSession(null);
    },
  }), [session]);

  return value;
}

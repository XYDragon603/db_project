import { useEffect, useMemo, useState } from "react";
import type { LoginResponse } from "@/api/types";
import { AUTH_SESSION_UPDATED_EVENT, clearAuthSession, persistAuthSession } from "@/api/auth-session";
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
      setSession(nextSession ?? null);
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
      persistAuthSession(nextSession);
    },
    logout() {
      clearAuthSession();
    },
  }), [session]);

  return value;
}

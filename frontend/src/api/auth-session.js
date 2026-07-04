import { STORAGE_KEY } from "@/hooks/use-demo-auth";

export const AUTH_SESSION_UPDATED_EVENT = "medminder-auth-session-updated";

export function createAuthSession(profile, email, password) {
  const normalizedEmail = email.trim().toLowerCase();
  const authHeader = `Basic ${btoa(`${normalizedEmail}:${password}`)}`;

  return {
    profile,
    authHeader,
  };
}

export function updateAuthSessionProfile(profile) {
  const value = window.localStorage.getItem(STORAGE_KEY);
  if (!value) {
    return null;
  }

  const session = JSON.parse(value);
  const nextSession = {
    ...session,
    profile: {
      ...session.profile,
      ...profile,
    },
  };

  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(nextSession));
  window.dispatchEvent(new CustomEvent(AUTH_SESSION_UPDATED_EVENT, { detail: nextSession }));
  return nextSession;
}

export function persistAuthSession(nextSession) {
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(nextSession));
  window.dispatchEvent(new CustomEvent(AUTH_SESSION_UPDATED_EVENT, { detail: nextSession }));
}

export function clearAuthSession() {
  window.localStorage.removeItem(STORAGE_KEY);
  window.dispatchEvent(new CustomEvent(AUTH_SESSION_UPDATED_EVENT, { detail: null }));
}

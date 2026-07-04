import type { LoginResponse } from "@/api/types";

export interface AuthSession {
  profile: LoginResponse;
  authHeader: string;
}

export declare const AUTH_SESSION_UPDATED_EVENT: string;

export declare function createAuthSession(
  profile: LoginResponse,
  email: string,
  password: string,
): AuthSession;

export declare function updateAuthSessionProfile(
  profile: Partial<LoginResponse>,
): AuthSession | null;

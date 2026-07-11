import {
  adherenceReport,
  adminUsers,
  auditLogs,
  caregiverAccessRecords,
  caregiverOverview,
  dashboardData,
  demoUsers,
  doseHistory,
  medications,
  profileByUserId,
  refillAlerts,
  schedulesByMedication,
} from "@/api/mock-data";
import type {
  AdherenceReport,
  AdminUser,
  AuditLogEntry,
  CaregiverAccessRecord,
  CaregiverPatientOverview,
  DashboardData,
  DoseLogStatus,
  DoseHistoryEntry,
  LoginResponse,
  Medication,
  RefillAlert,
  RefillRecordResponse,
  Schedule,
  UserProfile,
} from "@/api/types";
import { STORAGE_KEY } from "@/hooks/use-demo-auth";
import type { AuthSession } from "@/api/auth-session";
import { resolveMockFallbackEnabled } from "@/lib/runtime-config";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api";
const ENABLE_MOCK_FALLBACK = resolveMockFallbackEnabled(import.meta.env.VITE_ENABLE_MOCK_FALLBACK);

function isNetworkError(error: unknown) {
  return error instanceof TypeError;
}

function readStoredSession(): AuthSession | null {
  const value = window.localStorage.getItem(STORAGE_KEY);
  return value ? (JSON.parse(value) as AuthSession) : null;
}

async function request<T>(path: string, options?: RequestInit, fallback?: T): Promise<T> {
  const session = readStoredSession();
  try {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      headers: {
        "Content-Type": "application/json",
        ...(session?.authHeader ? { Authorization: session.authHeader } : {}),
        ...(options?.headers ?? {}),
      },
      ...options,
    });
    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`);
    }
    return (await response.json()) as T;
  } catch (error) {
    // Offline fallback is only a student-demo backup and is not the main project flow.
    if (ENABLE_MOCK_FALLBACK && fallback !== undefined && isNetworkError(error)) {
      return fallback;
    }
    throw error;
  }
}

export async function login(email: string, password: string): Promise<LoginResponse> {
  const normalizedEmail = email.trim().toLowerCase();
  const fallback = demoUsers[normalizedEmail];
  try {
    return await request<LoginResponse>("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email: normalizedEmail, password }),
    });
  } catch (error) {
    if (ENABLE_MOCK_FALLBACK && fallback && password === "password" && isNetworkError(error)) {
      return fallback;
    }
    throw error;
  }
}

export async function register(payload: {
  fullName: string;
  email: string;
  password: string;
  phone?: string;
}): Promise<LoginResponse> {
  return request<LoginResponse>("/auth/register", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export function getDashboard(userId: number) {
  void userId;
  return request<DashboardData>("/dashboard/today", undefined, dashboardData);
}

export function getMedications(userId: number) {
  void userId;
  return request<Medication[]>("/medications", undefined, medications);
}

export function createMedication(userId: number, payload: Record<string, unknown>) {
  void userId;
  return request<Medication>("/medications", {
    method: "POST",
    body: JSON.stringify(payload),
  }, {
    medicationId: Date.now(),
    medicineName: String(payload.medicineName ?? "New Medication"),
    dosage: String(payload.dosage ?? ""),
    form: String(payload.form ?? ""),
    currentQuantity: Number(payload.currentQuantity ?? 0),
    refillThreshold: Number(payload.refillThreshold ?? 0),
    active: true,
    startDate: String(payload.startDate ?? "") || null,
    endDate: String(payload.endDate ?? "") || null,
    notes: String(payload.notes ?? "") || null,
  });
}

export function getMedicationById(userId: number, medicationId: number) {
  void userId;
  const fallback = medications.find((item) => item.medicationId === medicationId);
  return request<Medication>(`/medications/${medicationId}`, undefined, fallback);
}

export function updateMedication(userId: number, medicationId: number, payload: Record<string, unknown>) {
  void userId;
  return request<Medication>(`/medications/${medicationId}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  }, {
    medicationId,
    medicineName: String(payload.medicineName ?? "Updated Medication"),
    dosage: String(payload.dosage ?? ""),
    form: String(payload.form ?? ""),
    currentQuantity: Number(payload.currentQuantity ?? 0),
    refillThreshold: Number(payload.refillThreshold ?? 0),
    active: true,
    startDate: String(payload.startDate ?? "") || null,
    endDate: String(payload.endDate ?? "") || null,
    notes: String(payload.notes ?? "") || null,
  });
}

export function deactivateMedication(userId: number, medicationId: number) {
  void userId;
  const fallback = medications.find((item) => item.medicationId === medicationId);
  return request<Medication>(`/medications/${medicationId}/deactivate`, {
    method: "PATCH",
  }, fallback ? { ...fallback, active: false } : undefined);
}

export function getSchedules(userId: number, medicationId: number) {
  void userId;
  return request<Schedule[]>(
    `/medications/${medicationId}/schedules`,
    undefined,
    schedulesByMedication[medicationId] ?? [],
  );
}

export function createSchedule(userId: number, payload: Record<string, unknown>) {
  void userId;
  return request<Schedule>("/schedules", {
    method: "POST",
    body: JSON.stringify(payload),
  }, {
    scheduleId: Date.now(),
    scheduledTime: String(payload.scheduledTime ?? "08:00"),
    doseAmount: String(payload.doseAmount ?? "1"),
    frequency: String(payload.frequency ?? "DAILY"),
    active: true,
  });
}

export function createSchedules(userId: number, payloads: Record<string, unknown>[]) {
  void userId;
  return request<Schedule[]>("/schedules/bulk", {
    method: "POST",
    body: JSON.stringify({ schedules: payloads }),
  }, payloads.map((payload, index) => ({
    scheduleId: Date.now() + index,
    scheduledTime: String(payload.scheduledTime ?? "08:00"),
    doseAmount: String(payload.doseAmount ?? "1"),
    frequency: String(payload.frequency ?? "DAILY"),
    active: true,
  })));
}

export function deactivateSchedule(userId: number, scheduleId: number) {
  void userId;
  return request<Schedule>(`/schedules/${scheduleId}/deactivate`, {
    method: "PATCH",
  });
}

export function updateSchedule(
  userId: number,
  scheduleId: number,
  payload: { scheduledTime: string; doseAmount: string },
) {
  void userId;
  return request<Schedule>(`/schedules/${scheduleId}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  });
}

export function logDose(userId: number, payload: Record<string, unknown>) {
  void userId;
  return request("/dose-logs", {
    method: "POST",
    body: JSON.stringify(payload),
  }, {
    doseLogId: Date.now(),
    scheduleId: Number(payload.scheduleId ?? 0),
    status: String(payload.status ?? "TAKEN"),
    updatedQuantity: 17,
  });
}

export function addRefill(userId: number, payload: Record<string, unknown>) {
  void userId;
  return request<RefillRecordResponse>("/refills", {
    method: "POST",
    body: JSON.stringify(payload),
  }, {
    refillId: Date.now(),
    medicationId: Number(payload.medicationId ?? 0),
    quantityAdded: Number(payload.quantityAdded ?? 30),
    updatedQuantity: Number(payload.quantityAdded ?? 30),
  });
}

export function getRefillAlerts(userId: number) {
  void userId;
  return request<RefillAlert[]>("/refills/alerts", undefined, refillAlerts);
}

export function getDoseHistory(userId: number) {
  void userId;
  const now = new Date();
  const startDate = new Date(Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), 1))
    .toISOString()
    .slice(0, 10);
  const endDate = new Date(Date.UTC(now.getUTCFullYear(), now.getUTCMonth() + 1, 0))
    .toISOString()
    .slice(0, 10);

  return request<DoseHistoryEntry[]>(
    `/dose-logs/history?startDate=${startDate}&endDate=${endDate}`,
    undefined,
    doseHistory,
  );
}

export function getCaregiverOverview(caregiverId: number) {
  return request<CaregiverPatientOverview[]>(`/caregiver/dashboard?caregiverId=${caregiverId}`, undefined, caregiverOverview);
}

export function getAdherenceReport(userId: number, month: string) {
  void userId;
  return request<AdherenceReport>(`/reports/adherence?month=${month}`, undefined, {
    ...adherenceReport,
    month,
  });
}

export function getMyProfile(userId: number) {
  return request<UserProfile>("/profile", undefined, profileByUserId[userId]);
}

export function updateMyProfile(userId: number, payload: { fullName: string; phone?: string | null }) {
  const fallback = profileByUserId[userId];
  return request<UserProfile>("/profile", {
    method: "PUT",
    body: JSON.stringify(payload),
  }, fallback ? {
    ...fallback,
    fullName: payload.fullName.trim(),
    phone: payload.phone?.trim() || null,
  } : undefined);
}

export function getCaregiverAccess(userId: number) {
  void userId;
  return request<CaregiverAccessRecord[]>("/caregiver-access", undefined, caregiverAccessRecords);
}

export function grantCaregiverAccess(userId: number, caregiverEmail: string) {
  void userId;
  return request<CaregiverAccessRecord>("/caregiver-access", {
    method: "POST",
    body: JSON.stringify({ caregiverEmail }),
  }, {
    accessId: Date.now(),
    caregiverUserId: 6,
    caregiverName: "Alex Johnson",
    caregiverEmail,
    accessStatus: "APPROVED",
    grantedAt: new Date().toISOString(),
  });
}

export function revokeCaregiverAccess(userId: number, accessId: number) {
  void userId;
  const fallback = caregiverAccessRecords.find((item) => item.accessId === accessId);
  return request<CaregiverAccessRecord>(`/caregiver-access/${accessId}/revoke`, {
    method: "PATCH",
  }, fallback ? { ...fallback, accessStatus: "REVOKED" } : undefined);
}

export function getAuditLogs() {
  return request<AuditLogEntry[]>("/admin/audit-logs", undefined, auditLogs);
}

export function getAdminUsers() {
  return request<AdminUser[]>("/admin/users", undefined, adminUsers);
}

export function deactivateUser(userId: number) {
  const fallback = adminUsers.find((user) => user.userId === userId);
  return request<AdminUser>(`/admin/users/${userId}/deactivate`, {
    method: "PATCH",
  }, fallback ? { ...fallback, active: false } : undefined);
}

export function reactivateUser(userId: number) {
  const fallback = adminUsers.find((user) => user.userId === userId);
  return request<AdminUser>(`/admin/users/${userId}/reactivate`, {
    method: "PATCH",
  }, fallback ? { ...fallback, active: true } : undefined);
}

export function getAdminRoleUsers() {
  return request<AdminUser[]>("/admin/roles/users", undefined, adminUsers);
}

export function assignUserRole(userId: number, roleName: string) {
  const fallback = adminUsers.find((user) => user.userId === userId);
  return request<AdminUser>(`/admin/users/${userId}/roles`, {
    method: "POST",
    body: JSON.stringify({ roleName }),
  }, fallback ? {
    ...fallback,
    roles: Array.from(new Set([...fallback.roles, roleName])).sort(),
  } : undefined);
}

export function removeUserRole(userId: number, roleName: string) {
  const fallback = adminUsers.find((user) => user.userId === userId);
  return request<AdminUser>(`/admin/users/${userId}/roles/${roleName}`, {
    method: "DELETE",
  }, fallback ? {
    ...fallback,
    roles: fallback.roles.filter((role) => role !== roleName).sort(),
  } : undefined);
}

export function buildDoseLogPayload(scheduleId: number, status: DoseLogStatus) {
  return { scheduleId, status };
}

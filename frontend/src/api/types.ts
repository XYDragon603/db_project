export type UserRole = "USER" | "CAREGIVER" | "ADMIN";
export type DoseLogStatus = "TAKEN" | "MISSED" | "SKIPPED" | "LATE";

export interface LoginResponse {
  userId: number;
  fullName: string;
  email: string;
  role: UserRole;
}

export interface UserProfile {
  userId: number;
  fullName: string;
  email: string;
  phone: string | null;
  active: boolean;
  createdAt: string;
}

export interface MedicationTask {
  scheduleId: number;
  medicationId: number;
  medicineName: string;
  dosage: string;
  scheduledTime: string;
  doseAmount: string;
  status: "PENDING" | "TAKEN" | "MISSED" | "SKIPPED" | "LATE";
}

export interface RefillAlert {
  medicationId: number;
  medicineName: string;
  currentQuantity: number;
  refillThreshold: number;
}

export interface AdherenceSummary {
  totalScheduledDoses: number;
  totalTakenDoses: number;
  totalMissedDoses: number;
  totalSkippedDoses: number;
  totalLateDoses: number;
  adherenceRate: number;
}

export interface MedicationAdherenceSummary {
  medicationId: number;
  medicineName: string;
  dosage: string;
  scheduledDoses: number;
  takenCount: number;
  missedCount: number;
  skippedCount: number;
  lateCount: number;
  adherenceRate: number;
}

export interface AdherenceReport {
  month: string;
  summary: AdherenceSummary;
  medications: MedicationAdherenceSummary[];
}

export interface DashboardData {
  todaysMedications: MedicationTask[];
  refillAlerts: RefillAlert[];
  adherenceSummary: AdherenceSummary;
}

export interface Medication {
  medicationId: number;
  medicineName: string;
  dosage: string;
  form: string;
  currentQuantity: number;
  refillThreshold: number;
  active: boolean;
  startDate?: string | null;
  endDate?: string | null;
  notes?: string | null;
}

export interface Schedule {
  scheduleId: number;
  scheduledTime: string;
  doseAmount: string;
  frequency: string;
  active: boolean;
}

export interface DoseHistoryEntry {
  doseLogId: number;
  scheduledDatetime: string;
  medicineName: string;
  dosage: string;
  status: string;
  actualTakenTime: string | null;
}

export interface CaregiverPatientOverview {
  patientUserId: number;
  patientName: string;
  todayPending: number;
  todayMissed: number;
  refillAlerts: number;
}

export interface CaregiverAccessRecord {
  accessId: number;
  caregiverUserId: number;
  caregiverName: string;
  caregiverEmail: string;
  accessStatus: "APPROVED" | "REVOKED" | "PENDING";
  grantedAt: string;
}

export interface AuditLogEntry {
  auditId: number;
  createdAt: string;
  actorName: string;
  action: string;
  targetTable: string;
  targetId: number;
  details: string;
}

export interface AdminUser {
  userId: number;
  fullName: string;
  email: string;
  phone: string | null;
  active: boolean;
  roles: string[];
  createdAt: string;
}

export interface RefillRecordResponse {
  refillId: number;
  medicationId: number;
  quantityAdded: number;
  updatedQuantity: number;
}

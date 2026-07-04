import type {
  AdherenceReport,
  AuditLogEntry,
  AdminUser,
  CaregiverAccessRecord,
  CaregiverPatientOverview,
  DashboardData,
  DoseHistoryEntry,
  LoginResponse,
  Medication,
  RefillAlert,
  Schedule,
  UserProfile,
} from "@/api/types";

export const demoUsers: Record<string, LoginResponse> = {
  "emily@example.com": { userId: 1, fullName: "Emily Johnson", email: "emily@example.com", role: "USER" },
  "alex.caregiver@example.com": { userId: 6, fullName: "Alex Johnson", email: "alex.caregiver@example.com", role: "CAREGIVER" },
  "admin@example.com": { userId: 8, fullName: "Admin User", email: "admin@example.com", role: "ADMIN" },
};

export const dashboardData: DashboardData = {
  todaysMedications: [
    { scheduleId: 301, medicationId: 101, medicineName: "Vitamin C", dosage: "500mg", scheduledTime: "08:00 AM", doseAmount: "1", status: "TAKEN" },
    { scheduleId: 302, medicationId: 102, medicineName: "Metformin", dosage: "500mg", scheduledTime: "01:00 PM", doseAmount: "1", status: "PENDING" },
    { scheduleId: 303, medicationId: 103, medicineName: "Atorvastatin", dosage: "10mg", scheduledTime: "08:00 PM", doseAmount: "1", status: "LATE" },
  ],
  refillAlerts: [
    { medicationId: 102, medicineName: "Metformin", currentQuantity: 4, refillThreshold: 5 },
    { medicationId: 104, medicineName: "Lisinopril", currentQuantity: 6, refillThreshold: 7 },
  ],
  adherenceSummary: {
    totalScheduledDoses: 54,
    totalTakenDoses: 47,
    totalMissedDoses: 5,
    totalSkippedDoses: 2,
    totalLateDoses: 1,
    adherenceRate: 87.04,
  },
};

export const medications: Medication[] = [
  { medicationId: 101, medicineName: "Vitamin C", dosage: "500mg", form: "Tablet", currentQuantity: 18, refillThreshold: 5, active: true, startDate: "2026-06-01", endDate: null, notes: "Morning supplement" },
  { medicationId: 102, medicineName: "Metformin", dosage: "500mg", form: "Tablet", currentQuantity: 4, refillThreshold: 5, active: true, startDate: "2026-06-10", endDate: null, notes: "Take after meals" },
  { medicationId: 103, medicineName: "Atorvastatin", dosage: "10mg", form: "Tablet", currentQuantity: 20, refillThreshold: 5, active: true, startDate: "2026-06-15", endDate: null, notes: "Evening dose" },
  { medicationId: 104, medicineName: "Lisinopril", dosage: "5mg", form: "Tablet", currentQuantity: 12, refillThreshold: 7, active: true, startDate: "2026-06-15", endDate: null, notes: "Monitor blood pressure" },
];

export const schedulesByMedication: Record<number, Schedule[]> = {
  101: [{ scheduleId: 301, scheduledTime: "08:00 AM", doseAmount: "1", frequency: "DAILY", active: true }],
  102: [{ scheduleId: 302, scheduledTime: "01:00 PM", doseAmount: "1", frequency: "DAILY", active: true }],
  103: [{ scheduleId: 303, scheduledTime: "08:00 PM", doseAmount: "1", frequency: "DAILY", active: true }],
};

export const refillAlerts: RefillAlert[] = dashboardData.refillAlerts;

export const doseHistory: DoseHistoryEntry[] = [
  { doseLogId: 401, scheduledDatetime: "2026-07-01T08:00:00Z", medicineName: "Vitamin C", dosage: "500mg", status: "TAKEN", actualTakenTime: "2026-07-01T08:02:00Z" },
  { doseLogId: 402, scheduledDatetime: "2026-07-01T13:00:00Z", medicineName: "Metformin", dosage: "500mg", status: "MISSED", actualTakenTime: null },
  { doseLogId: 403, scheduledDatetime: "2026-07-01T20:00:00Z", medicineName: "Atorvastatin", dosage: "10mg", status: "TAKEN", actualTakenTime: "2026-07-01T20:05:00Z" },
];

export const caregiverOverview: CaregiverPatientOverview[] = [
  { patientUserId: 1, patientName: "Emily Johnson", todayPending: 2, todayMissed: 1, refillAlerts: 1 },
  { patientUserId: 2, patientName: "Daniel Lee", todayPending: 1, todayMissed: 0, refillAlerts: 0 },
];

export const caregiverAccessRecords: CaregiverAccessRecord[] = [
  {
    accessId: 601,
    caregiverUserId: 6,
    caregiverName: "Alex Johnson",
    caregiverEmail: "alex.caregiver@example.com",
    accessStatus: "APPROVED",
    grantedAt: "2026-07-02T09:00:00Z",
  },
];

export const adherenceReport: AdherenceReport = {
  month: "2026-07",
  summary: {
    totalScheduledDoses: 54,
    totalTakenDoses: 47,
    totalMissedDoses: 5,
    totalSkippedDoses: 2,
    totalLateDoses: 1,
    adherenceRate: 87.04,
  },
  medications: [
    {
      medicationId: 101,
      medicineName: "Vitamin C",
      dosage: "500mg",
      scheduledDoses: 31,
      takenCount: 28,
      missedCount: 1,
      skippedCount: 1,
      lateCount: 1,
      adherenceRate: 90.32,
    },
    {
      medicationId: 102,
      medicineName: "Metformin",
      dosage: "500mg",
      scheduledDoses: 23,
      takenCount: 19,
      missedCount: 4,
      skippedCount: 1,
      lateCount: 0,
      adherenceRate: 82.61,
    },
  ],
};

export const profileByUserId: Record<number, UserProfile> = {
  1: {
    userId: 1,
    fullName: "Emily Johnson",
    email: "emily@example.com",
    phone: "555-0101",
    active: true,
    createdAt: "2026-06-01T09:00:00Z",
  },
  6: {
    userId: 6,
    fullName: "Alex Johnson",
    email: "alex.caregiver@example.com",
    phone: "555-0106",
    active: true,
    createdAt: "2026-06-01T09:30:00Z",
  },
  8: {
    userId: 8,
    fullName: "Admin User",
    email: "admin@example.com",
    phone: "555-0108",
    active: true,
    createdAt: "2026-06-01T10:00:00Z",
  },
};

export const auditLogs: AuditLogEntry[] = [
  { auditId: 701, createdAt: "2026-07-03T08:03:00Z", actorName: "Emily Johnson", action: "LOG_DOSE", targetTable: "dose_logs", targetId: 401, details: "Logged TAKEN dose" },
  { auditId: 705, createdAt: "2026-07-03T09:10:00Z", actorName: "Emily Johnson", action: "ADD_REFILL", targetTable: "refill_records", targetId: 502, details: "Added 30 tablets" },
  { auditId: 709, createdAt: "2026-07-03T10:22:00Z", actorName: "Admin User", action: "UPDATE_ROLE", targetTable: "user_roles", targetId: 8, details: "Confirmed admin assignment" },
];

export const adminUsers: AdminUser[] = [
  {
    userId: 8,
    fullName: "Admin User",
    email: "admin@example.com",
    phone: "555-0108",
    active: true,
    roles: ["ADMIN"],
    createdAt: "2026-06-01T10:00:00Z",
  },
  {
    userId: 6,
    fullName: "Alex Johnson",
    email: "alex.caregiver@example.com",
    phone: "555-0201",
    active: true,
    roles: ["CAREGIVER"],
    createdAt: "2026-06-01T09:30:00Z",
  },
  {
    userId: 2,
    fullName: "Daniel Lee",
    email: "daniel@example.com",
    phone: "555-0102",
    active: true,
    roles: ["USER"],
    createdAt: "2026-06-01T09:05:00Z",
  },
  {
    userId: 1,
    fullName: "Emily Johnson",
    email: "emily@example.com",
    phone: "555-0101",
    active: true,
    roles: ["USER"],
    createdAt: "2026-06-01T09:00:00Z",
  },
];

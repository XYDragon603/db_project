import type { MedicationCardProps } from "@/components/shared/medication-card";

export const medications: MedicationCardProps[] = [
  {
    name: "Vitamin C",
    dosage: "500mg",
    time: "08:00 AM",
    amount: "1 tablet",
    status: "TAKEN",
  },
  {
    name: "Metformin",
    dosage: "500mg",
    time: "01:00 PM",
    amount: "1 tablet",
    status: "PENDING",
  },
  {
    name: "Atorvastatin",
    dosage: "10mg",
    time: "08:00 PM",
    amount: "1 tablet",
    status: "LATE",
  },
];

export const schedules = [
  { time: "08:00 AM", meta: "DAILY - 1 tablet", active: true },
  { time: "08:00 PM", meta: "DAILY - 1 tablet", active: true },
];

export const refillAlerts = [
  { medication: "Metformin", currentQuantity: 4, refillThreshold: 5 },
  { medication: "Lisinopril", currentQuantity: 6, refillThreshold: 7 },
];

export const patients = [
  { name: "Emily Johnson", pending: 2, missed: 1, refillAlerts: 1 },
  { name: "Daniel Lee", pending: 1, missed: 0, refillAlerts: 0 },
];

export const auditRows = [
  {
    timestamp: "2026-07-02 08:03",
    actor: "Emily Johnson",
    action: "LOG_DOSE",
    target: "dose_logs",
  },
  {
    timestamp: "2026-07-02 09:10",
    actor: "Emily Johnson",
    action: "ADD_REFILL",
    target: "refill_records",
  },
];

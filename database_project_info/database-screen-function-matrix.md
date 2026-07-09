# MedMinder Database Screen–Function Matrix

This file maps the main MedMinder screens to the SQL functions or representative queries that supply their data.

| Screen | Screen Section / UI Component | Function Name | Tables Used | Example Result | Notes |
|---|---|---|---|---|---|
| Login | Sign-in form | `authenticateUser(email)` | `users`, `user_roles`, `roles` | `Emily Johnson | emily@example.com | USER | active` | Returns password hash for backend verification, not for UI display. |
| User Dashboard | Today's Medications card | `getTodayMedicationSchedule(userId)` | `medications`, `medication_schedules`, `dose_logs` | `08:00 | Vitamin C | 500mg | PENDING` | `PENDING` is derived when no `dose_logs` row exists yet. |
| User Dashboard | Refill Alerts card | `getRefillAlerts(userId)` | `medications` | `Metformin | current 4 | threshold 5 | shortage 1` | Uses `current_quantity <= refill_threshold`. |
| User Dashboard | Adherence Snapshot card | `getAdherenceSummary(userId, month)` | `medication_schedules`, `medications`, `dose_logs` | `total 118 | taken 92 | missed 14 | adherence 77.97%` | Counts only overlapping schedule days in the selected month. |
| Medication List | Medication list table/cards | `getUserMedications(userId)` | `medications` | `101 | Vitamin C | Tablet | qty 18` | Ordered by active status and medicine name. |
| Add Medication | Save medication form | `createMedication(userId, medicationData)` | `medications` | `new medication_id 125` | Representative parameterized `INSERT`, usually executed by app code. |
| Edit Medication | Load medication detail form | `getMedicationById(userId, medicationId)` | `medications` | `102 | Metformin | 500mg | notes: Take after meals` | Reads one owned medication record for editing. |
| Edit Medication | Save updated medication | `updateMedication(userId, medicationId, data)` | `medications`, `audit_logs` | `medication_id 102 | updated dosage 850mg | audit UPDATE_MEDICATION` | Typically updates fields in `medications` and writes an audit row. |
| Schedule Management | Current schedules list | `getMedicationSchedules(userId, medicationId)` | `medication_schedules` | `302 | 13:00 | DAILY | active` | Supports one medication detail view. |
| Schedule Management | Add daily schedule form | Representative `INSERT` into schedules | `medication_schedules`, `medications` | `new schedule_id 311` | Owner consistency requires `user_id` to match medication owner. |
| Dose Logging | Log dose action | `logDose(scheduleId, userId, status, scheduledDatetime, actualTakenTime)` | `dose_logs`, `medication_schedules`, `medications`, `audit_logs` | `dose_log_id 611 | updated_quantity 17` | Runs as one transaction. |
| Dose History | History list | `getDoseHistory(userId, startDate, endDate)` | `dose_logs`, `medication_schedules`, `medications` | `Jul 2 | Vitamin C | TAKEN` | Uses indexed `(user_id, scheduled_datetime)` path. |
| Refill Alerts | Refill alert list | `getRefillAlerts(userId)` | `medications` | `Metformin | qty 4 | threshold 5` | Also reused on dashboard. |
| Refill Alerts | Add refill action | `addRefillRecord(userId, medicationId, quantityAdded, note)` | `refill_records`, `medications`, `audit_logs` | `refill_id 701 | updated_quantity 34` | Verifies medication ownership before inserting the refill row. |
| Reports | Monthly adherence summary card | `getAdherenceSummary(userId, month)` | `medication_schedules`, `medications`, `dose_logs` | `taken 92 | late 8 | adherence 77.97%` | `total_scheduled_doses` is based on actual schedule overlap days. |
| Reports | Medication-level adherence table | Representative grouped report query | `medications`, `medication_schedules`, `dose_logs` | `Vitamin C | 31 scheduled | 28 taken` | Optional extra report detail. |
| Caregiver Dashboard | Patient overview cards | `getCaregiverPatientOverview(caregiverId)` | `caregiver_access`, `users`, `medications`, `medication_schedules`, `dose_logs` | `Emily Johnson | 4 meds | 1 refill alert` | Only approved caregiver relationships are included. |
| Caregiver Access | Access list | Representative access query | `caregiver_access`, `users` | `Alex Johnson | APPROVED` | Shows who can view a patient's records. |
| Admin Audit Logs | Audit log table | `getAuditLogs(filters)` | `audit_logs` | `UPDATE_PROFILE | users | target 1` | Filter by user, action, and date range. |
| Admin User Management | User list with roles | Representative admin user query | `users`, `user_roles`, `roles` | `admin@example.com | ADMIN | active` | Useful for admin status management. |

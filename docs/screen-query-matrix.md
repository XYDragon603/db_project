# MedMinder Screen-to-Query Matrix

## Purpose

This document maps the most important application screens to the database queries or SQL functions they require.

This is primarily for the Database course deliverable. API placeholders are intentionally not emphasized here.

## Screen Matrix

| Screen | Screen Section / UI Component | User Role | Main Data Needed | Database Tables Used | SQL Function / Query Name | Example Result | Priority |
|---|---|---|---|---|---|---|---|
| Login | Login form | Public | user identity, password hash, assigned role | users, user_roles, roles | authenticateUser(email) | one user row with role data | High |
| User Dashboard / Today's Medications | Today's Medications card | USER | today's schedule and current display status | medications, medication_schedules, dose_logs | getTodayMedicationSchedule(userId) | today's medication task list | High |
| User Dashboard / Today's Medications | Refill Alerts card | USER | low-stock medications | medications | getRefillAlerts(userId) | refill alert list | High |
| User Dashboard / Today's Medications | Adherence Snapshot card | USER | monthly adherence summary | dose_logs, medication_schedules | getAdherenceSummary(userId, month) | adherence metrics | High |
| Medication List | Medication card grid | USER | medications owned by the user | medications | getUserMedications(userId) | medication list | High |
| Add Medication | Medication form submit action | USER | insert medication and audit action | medications, audit_logs | createMedication(userId, medicationData) | new medication row | High |
| Schedule Management | Schedule list | USER | schedules for one medication | medication_schedules, medications | getMedicationSchedules(userId, medicationId) | schedule list | High |
| Dose Logging | Medication action modal / quick log action | USER | create logged dose event and update stock if needed | dose_logs, medication_schedules, medications, audit_logs | logDose(scheduleId, userId, status) | inserted log row and updated quantity | High |
| Dose History | History list or table | USER | historical doses by date range | dose_logs, medication_schedules, medications | getDoseHistory(userId, startDate, endDate) | dose history rows | High |
| Refill Alerts | Refill alert card list | USER | low-stock medications | medications | getRefillAlerts(userId) | refill alert list | High |
| Refill Alerts | Add Refill dialog submit action | USER | add refill record and increase quantity | refill_records, medications, audit_logs | addRefillRecord(userId, medicationId, quantityAdded) | refill row and new stock value | High |
| Reports | Adherence summary chart and stats | USER | monthly adherence summary | dose_logs, medication_schedules | getAdherenceSummary(userId, month) | summary totals and adherence rate | Medium |
| Caregiver Dashboard | Patient overview cards | CAREGIVER | approved patient overview, missed doses, refill risks | caregiver_access, users, dose_logs, medications, medication_schedules | getCaregiverPatientOverview(caregiverId) | patient overview rows | High |
| Audit Logs | Audit log table and filters | ADMIN | filtered audit records | audit_logs, users | getAuditLogs(filters) | audit list | High |
| Admin Dashboard | KPI cards and recent activity | ADMIN | summary counts, role distribution, latest activity | users, roles, user_roles, audit_logs | aggregate queries plus getAuditLogs(filters) | stats and latest log rows | Medium |
| User Management | User management table | ADMIN | users and their roles | users, user_roles, roles | user-role join query | user and role list | Medium |
| Role Management | Role summary table | ADMIN | roles and assignments | roles, user_roles, users | role assignment query | role summary rows | Medium |

## Demo-Critical Query Notes

### authenticateUser(email)
Used at login to retrieve the user account and assigned roles.

### getTodayMedicationSchedule(userId)
Returns today's medication tasks. `PENDING` is derived when a schedule has no matching dose log for the current scheduled datetime.

### getUserMedications(userId)
Returns all medications for the current user, including inventory values needed for refill awareness.

### createMedication(userId, medicationData)
Creates a medication record and should also create an audit log entry.

### getMedicationSchedules(userId, medicationId)
Returns all schedules tied to one medication and owner.

### logDose(scheduleId, userId, status)
This operation should run in one transaction:

- insert a `dose_logs` record
- if status is `TAKEN`, reduce `medications.current_quantity`
- insert an `audit_logs` record

### getDoseHistory(userId, startDate, endDate)
Returns a date-range history of logged doses.

### getRefillAlerts(userId)
Returns medications where current quantity is less than or equal to the refill threshold.

### addRefillRecord(userId, medicationId, quantityAdded)
Adds a refill record and increases current quantity.

### getAdherenceSummary(userId, month)
Returns monthly totals for scheduled doses, taken doses, missed doses, and adherence rate.

`total_scheduled_doses` should be derived from active `medication_schedules` in the selected month. `TAKEN`, `MISSED`, `SKIPPED`, and `LATE` totals should come from `dose_logs`.

### getCaregiverPatientOverview(caregiverId)
Returns overview data only for users with approved caregiver access.

### getAuditLogs(filters)
Returns filtered audit events for admin monitoring.

## Example Results

### getTodayMedicationSchedule(userId)

| scheduled_time | medicine_name | dosage | dose_amount | status |
|---|---|---|---:|---|
| 08:00 AM | Vitamin C | 500mg | 1 | TAKEN |
| 01:00 PM | Metformin | 500mg | 1 | PENDING |
| 08:00 PM | Atorvastatin | 10mg | 1 | PENDING |

### getUserMedications(userId)

| medication_id | medicine_name | dosage | form | current_quantity | refill_threshold |
|---|---|---|---|---:|---:|
| 101 | Vitamin C | 500mg | Tablet | 18 | 5 |
| 102 | Metformin | 500mg | Tablet | 4 | 5 |
| 103 | Atorvastatin | 10mg | Tablet | 20 | 5 |

### createMedication(userId, medicationData)

| medication_id | user_id | medicine_name | dosage | form | current_quantity | refill_threshold |
|---|---:|---|---|---|---:|---:|
| 104 | 1 | Lisinopril | 5mg | Tablet | 30 | 7 |

### getMedicationSchedules(userId, medicationId)

| schedule_id | medication_id | scheduled_time | dose_amount | frequency | is_active |
|---|---:|---|---:|---|---|
| 201 | 102 | 08:00 AM | 1 | DAILY | true |
| 202 | 102 | 08:00 PM | 1 | DAILY | true |

### logDose(scheduleId, userId, status)

| dose_log_id | schedule_id | user_id | scheduled_datetime | actual_taken_time | status | updated_quantity |
|---|---:|---:|---|---|---|---:|
| 9001 | 201 | 1 | 2026-07-02 08:00 | 2026-07-02 08:03 | TAKEN | 17 |

### getDoseHistory(userId, startDate, endDate)

| log_date | scheduled_time | medicine_name | status | actual_taken_time |
|---|---|---|---|---|
| 2026-07-01 | 08:00 AM | Vitamin C | TAKEN | 08:02 AM |
| 2026-07-01 | 01:00 PM | Metformin | MISSED | null |
| 2026-07-01 | 08:00 PM | Atorvastatin | TAKEN | 08:05 PM |

### getRefillAlerts(userId)

| medication_id | medicine_name | current_quantity | refill_threshold | alert_level |
|---|---|---:|---:|---|
| 102 | Metformin | 4 | 5 | LOW_STOCK |

### addRefillRecord(userId, medicationId, quantityAdded)

| refill_id | medication_id | user_id | refill_date | quantity_added | new_current_quantity |
|---|---:|---:|---|---:|---:|
| 4002 | 102 | 1 | 2026-07-02 | 30 | 34 |

### getAdherenceSummary(userId, month)

| month | total_scheduled_doses | total_taken_doses | total_missed_doses | adherence_rate |
|---|---:|---:|---:|---:|
| 2026-07 | 54 | 47 | 5 | 87.04 |

### getCaregiverPatientOverview(caregiverId)

| patient_name | today_pending | today_missed | refill_alerts | access_status |
|---|---:|---:|---:|---|
| Emily Johnson | 2 | 1 | 1 | APPROVED |
| Daniel Lee | 1 | 0 | 0 | APPROVED |

### getAuditLogs(filters)

| created_at | user_name | action | target_table | target_id |
|---|---|---|---|---:|
| 2026-07-02 08:03 | Emily Johnson | LOG_DOSE | dose_logs | 9001 |
| 2026-07-02 09:10 | Emily Johnson | ADD_REFILL | refill_records | 4002 |
| 2026-07-02 10:22 | Admin User | UPDATE_ROLE | user_roles | 301 |

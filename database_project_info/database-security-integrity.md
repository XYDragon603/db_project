# MedMinder Database Security and Integrity Notes

## 1. Password Storage

The `users` table stores `password_hash`, not plain passwords.
This supports safer authentication because the database never stores raw credentials.

## 2. Role-Based Access

Role access is modeled through:
- `roles`
- `user_roles`

This allows one user to be linked to one or more roles such as:
- `USER`
- `CAREGIVER`
- `ADMIN`

The database design separates user identity from role membership, which is cleaner and easier to maintain than storing one role column directly in `users`.

## 3. Owner Checks and Data Scope

Ownership is centered on `users.user_id`.
Key user-owned tables include:
- `medications`
- `medication_schedules`
- `dose_logs`
- `refill_records`

Important owner consistency rules:
- `medication_schedules.user_id` must match the owner of the related medication.
- `dose_logs.user_id` must match the owner of the related schedule.
- `refill_records.user_id` must match the owner of the related medication.

These rules are supported by composite foreign keys in the schema and are also reinforced in backend transaction logic.

## 4. Foreign Keys

The schema uses foreign keys to protect referential integrity.
Examples:
- `user_roles.user_id -> users.user_id`
- `user_roles.role_id -> roles.role_id`
- `medications.user_id -> users.user_id`
- `prescriptions.medication_id -> medications.medication_id`
- `medication_schedules (medication_id, user_id) -> medications (medication_id, user_id)`
- `dose_logs (schedule_id, user_id) -> medication_schedules (schedule_id, user_id)`
- `refill_records (medication_id, user_id) -> medications (medication_id, user_id)`
- `caregiver_access.user_id -> users.user_id`
- `caregiver_access.caregiver_id -> users.user_id`
- `audit_logs.user_id -> users.user_id`

This prevents orphan records and keeps cross-table relationships valid.

## 5. Check Constraints

The schema includes practical check constraints to stop invalid data from being stored.
Examples:
- `current_quantity >= 0`
- `refill_threshold >= 0`
- `dose_amount > 0`
- `frequency IN ('DAILY')`
- `dose_logs.status IN ('TAKEN', 'MISSED', 'SKIPPED', 'LATE')`
- `caregiver_access.access_status IN ('PENDING', 'APPROVED', 'REVOKED')`
- caregiver cannot grant access to themselves: `user_id <> caregiver_id`
- `end_date >= start_date` where both dates exist

These constraints reduce data-cleaning work later and make the database safer even if application validation fails.

## 6. Soft Deactivation

MedMinder prefers soft deactivation over hard deletion for important operational records.
Examples:
- `users.is_active`
- `medications.is_active`
- `medication_schedules.is_active`

This preserves historical data for:
- dose history
- refill records
- audit logs
- admin review

It also supports classroom demo scenarios where records should stay traceable.

## 7. Audit Logs

The `audit_logs` table records important system actions.
Fields include:
- acting `user_id`
- `action`
- `target_table`
- `target_id`
- `details`
- `created_at`

This supports:
- accountability
- admin review
- debugging
- security traceability

Representative actions include:
- `LOG_DOSE`
- `ADD_REFILL`
- `UPDATE_PROFILE`
- `UPDATE_MEDICATION`
- `DEACTIVATE_USER`
- `REACTIVATE_USER`

## 8. Caregiver Privacy Boundaries

Caregiver access is controlled by the `caregiver_access` table.
Only rows with `access_status = 'APPROVED'` should be used to expose patient summary data.
This ensures caregivers are limited to explicitly approved relationships.

## 9. Admin Privacy Boundaries

Admin users manage system-level functions such as:
- audit logs
- user status
- role assignment

The database design does not assume that admin users should freely edit all private medication data.
This follows the principle of least privilege.

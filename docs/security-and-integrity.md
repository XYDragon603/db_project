# MedMinder Security and Integrity

## Purpose

This document explains how the MedMinder data model supports security, ownership control, and data integrity.

## Security Design

### Authentication Storage

- Passwords are stored as `password_hash`
- Plain-text passwords are never stored

### Role-Based Access Control

- Roles are stored in `roles`
- User-role assignments are stored in `user_roles`
- This supports `USER`, `CAREGIVER`, and `ADMIN` permissions without mixing access rules into the `users` table

### Ownership Control

- Every medication belongs to one user through `medications.user_id`
- Every schedule belongs to a medication and user
- Every dose log belongs to a schedule and user
- Every refill record belongs to a medication and user

This structure helps ensure users can only access their own medication data.

### Owner Consistency Rules

Because some child tables store `user_id` for query performance, the system must also preserve ownership consistency:

- `medication_schedules.user_id` must match `medications.user_id` of the related medication
- `dose_logs.user_id` must match the owner of the related schedule
- `refill_records.user_id` must match the owner of the related medication

These rules should be enforced through database constraints where practical and through service-layer validation inside transactions.

### Caregiver Access Control

- Caregivers can only view patient data when a matching `caregiver_access` row exists with `access_status = 'APPROVED'`
- Caregiver pages are read-only by default
- No medication editing is allowed through caregiver access

### Admin Access

- Admin users can view system-level summaries, user-role data, and audit logs
- Admin access is intended for system management, not unrestricted private medication editing

### Audit Logging

Sensitive actions should create entries in `audit_logs`, including:

- ADD_MEDICATION
- UPDATE_MEDICATION
- DELETE_MEDICATION
- LOG_DOSE
- ADD_REFILL
- GRANT_CAREGIVER_ACCESS
- REVOKE_CAREGIVER_ACCESS

This supports monitoring, review, and accountability.

### Privacy Limitation

Admin users should access system-level summaries, user-role management, and audit logs. Detailed private medication records should not be freely exposed to admins unless there is a clearly defined administrative reason.

This follows the principle of least privilege.

## Data Integrity Rules

### Value Constraints

- `current_quantity >= 0`
- `refill_threshold >= 0`
- `dose_amount > 0`
- `scheduled_time` is required
- `quantity_added > 0`

### Input Validation

User input should be validated before data is saved:

- valid email format
- non-negative quantity fields
- `dose_amount > 0`
- allowed status values only
- users cannot submit or update records for another user's `user_id`

### Status Constraints

- `dose_logs.status` must be one of `TAKEN`, `MISSED`, `SKIPPED`, `LATE`
- `caregiver_access.access_status` must be one of `PENDING`, `APPROVED`, `REVOKED`

### PENDING Clarification

`PENDING` should be derived in `getTodayMedicationSchedule(userId)` when a scheduled dose does not yet have a matching dose log.

This avoids storing an unnecessary placeholder event in `dose_logs`.

### Relationship Constraints

- every medication must belong to a valid user
- every schedule must belong to a valid medication and valid user
- every dose log must belong to a valid schedule and valid user
- every refill record must belong to a valid medication and valid user

### Uniqueness Constraints

- `users.email` must be unique
- `user_roles (user_id, role_id)` must be unique
- `caregiver_access (user_id, caregiver_id)` must be unique

## Transaction Integrity

Operations that update multiple tables should run in one database transaction.

### logDose(scheduleId, userId, status)

This operation should:

- insert a `dose_logs` row
- reduce `medications.current_quantity` if status is `TAKEN`
- insert an `audit_logs` row

All three steps should succeed or fail together.

### addRefillRecord(userId, medicationId, quantityAdded)

This operation should:

- insert a `refill_records` row
- increase `medications.current_quantity`
- insert an `audit_logs` row

All three steps should succeed or fail together.

## Soft Delete Policy

The system should prefer soft delete for important user-owned records:

- users can be deactivated with `is_active = false`
- medications can be marked inactive instead of deleting historical records
- audit logs should not be automatically deleted

## Course Separation

### Database Course Focus

This course should focus on:

- ERD
- normalized schema
- queries and SQL functions
- screen-to-query mapping
- constraints
- security explanation
- optimization

It should not focus heavily on APIs.

### SSC Course Focus

The SSC implementation can use:

- simple REST API placeholders
- Spring Boot
- React
- PostgreSQL
- Hibernate ORM
- Spring Security
- CI/CD
- Trivy
- Semgrep

The API layer should stay simple and only support the full-stack demo.

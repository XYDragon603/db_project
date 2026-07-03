# MedMinder Project Specification

## Project Overview

MedMinder is a responsive healthcare SaaS web application for medication schedule tracking, dose logging, refill reminders, caregiver access, and admin monitoring.

MedMinder is designed for personal medication tracking and educational project demonstration, not for clinical decision-making or professional medical use.

The project must stay aligned with two course goals:

- Database course: normalized schema, ERD, SQL functions and queries, screen-to-query mapping, example results, constraints, security explanation, and optimization
- SSC course: Spring Boot backend, React frontend, PostgreSQL, Hibernate ORM, Spring Security, CI/CD, Trivy, Semgrep, and deployment-ready structure

MedMinder does not provide medical diagnosis, drug interaction checking, medication recommendations, pharmacy ordering, prescription approval, or medical validation.

## Product Direction

The product uses one shared visual system across three experiences:

- User App: mobile-first medication reminder experience inside a responsive web app
- Caregiver App: read-only healthcare dashboard for approved patient monitoring
- Admin App: SaaS-style management dashboard for users, roles, audit logs, and system reports

## Visual System

- English-only UI
- Soft blue and white medical SaaS palette
- Rounded elevated cards
- Clean spacing
- Readable typography
- Calm and trustworthy tone
- No dark mode
- No neon colors
- No cluttered layouts
- No generic Bootstrap-like styling

## Core Roles

### User

- Register and log in
- Manage medications
- Create medication schedules
- View today's medication tasks
- Log doses as TAKEN, MISSED, SKIPPED, or LATE
- View dose history
- Track refill alerts
- Manage caregiver access
- View adherence reports

### Caregiver

- Log in
- View only the data of users with approved caregiver access
- Review medication status, missed doses, and refill alerts
- No medication or schedule editing by default

### Admin

- Manage users and roles
- View audit logs
- View system-level reports and summary statistics
- Admin access is limited to system management, audit review, and high-level reports
- Admin should not view or modify detailed private medication records unless required by a clearly defined administrative rule

## Reusable Frontend Components

- AppShell
- UserLayout
- DashboardLayout
- Sidebar
- Topbar
- MobileBottomNav
- PageHeader
- StatCard
- Card
- MedicationCard
- ScheduleCard
- RefillAlertCard
- PatientCard
- StatusBadge
- DataTable
- FilterBar
- EmptyState
- FormInput
- FormSelect
- DateInput
- Dialog / Modal
- PrimaryButton
- SecondaryButton
- DangerButton

## Full Page List

### User App

1. Login
2. Register
3. User Dashboard / Today's Medications
4. Medication List
5. Add Medication
6. Edit Medication
7. Schedule Management
8. Dose History
9. Refill Alerts
10. Caregiver Access
11. Reports
12. Profile / Settings

### Caregiver App

1. Caregiver Dashboard
2. Authorized Users
3. Patient Medication Status
4. Missed Dose List
5. Refill Alert View

### Admin App

1. Admin Dashboard
2. User Management
3. Role Management
4. Audit Logs
5. System Reports

## Demo-Critical Scope

The first implementation phase should prioritize:

1. Login
2. User Dashboard / Today's Medications
3. Medication List
4. Add Medication
5. Schedule Management
6. Dose Logging flow
7. Refill Alerts
8. Dose History
9. Caregiver Dashboard
10. Audit Logs

Secondary pages can be implemented after the demo-critical flow is stable. The first demo should prioritize a complete working flow over implementing every page.

## Layout Behavior

### User App

- Mobile-first layout
- Bottom navigation on small screens
- Large readable medication cards
- Clear dose status actions
- Refill alerts and adherence summary as secondary support modules

### Caregiver App

- Read-only dashboard and patient cards
- No edit buttons unless a future explicit permission model is added
- Focus on today's summary, missed doses, and refill alerts

### Admin App

- Dashboard and table-oriented layout
- Search and filter-first management screens
- Clean styled tables, not default HTML table presentation

## Scope Boundaries

### Prescription Scope

The `prescriptions` table stores only optional prescription metadata:

- prescription_id
- medication_id
- prescribed_by
- clinic_name
- issue_date
- instructions

The project will not implement:

- prescription file upload
- pharmacy verification
- approval workflow
- real medical validation

### Dose Status Scope

For dashboard behavior, `PENDING` should be treated as a derived display status when a scheduled dose does not yet have a matching `dose_logs` record for the current scheduled datetime.

Actual logged dose statuses are:

- TAKEN
- MISSED
- SKIPPED
- LATE

This keeps `dose_logs` focused on actual recorded events while allowing the UI to display pending tasks through query logic.

## Course Alignment

### Database Course Focus

- ERD
- Normalized tables
- SQL functions and queries
- Screen-to-query and screen-to-function mapping
- Example query results
- Constraints
- Security explanation
- Optimization explanation

The Database report should focus on data design and queries, not APIs.

### SSC Course Focus

- Spring Boot backend
- React frontend
- PostgreSQL
- Hibernate ORM
- Spring Security
- CI/CD
- Trivy
- Semgrep
- Deployment-ready project structure

REST APIs should stay simple and only support the full-stack demo implementation.

## Repository Structure

Recommended structure:

- `backend/`
- `frontend/`
- `docs/`
- `.github/workflows/`

## Implementation Rule

Documentation must be reviewed before implementation code begins.

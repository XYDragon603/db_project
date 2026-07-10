# MedMinder Database Presentation Outline

A concise 8–10 slide structure for a 10–15 minute database presentation.

## Slide 1. Project Overview
- What MedMinder is
- Why a medication tracking system needs a database
- Main roles: User, Caregiver, Admin

## Slide 2. Database Scope and Goals
- Store medications, schedules, dose logs, refills, caregiver access, and audit logs
- Support both daily app screens and admin/system oversight
- Keep the design practical for a student full-stack project

## Slide 3. Core Schema / Main Tables
- `users`
- `roles`, `user_roles`
- `medications`
- `prescriptions`
- `medication_schedules`
- `dose_logs`
- `refill_records`
- `caregiver_access`
- `audit_logs`

## Slide 4. Key Relationships
- user to medications
- medication to schedules
- schedule to dose logs
- medication to refill records
- caregiver to approved patients
- user to audit logs

## Slide 5. Normalization and Integrity
- 1NF, 2NF, 3NF in practical terms
- owner consistency
- foreign keys
- check constraints
- unique constraints

## Slide 6. Important Screen Queries
- login/authentication query
- today's medication schedule
- medication list and edit medication query
- dose history
- refill alerts
- monthly adherence report

## Slide 7. Security and Access Control
- `password_hash`
- role-based access
- user owner checks
- approved caregiver scope only
- admin audit visibility

## Slide 8. Transaction Logic
- `logDose(...)` updates dose logs, quantity, and audit logs in one transaction
- `addRefillRecord(...)` first checks medication ownership, then updates refill records, quantity, and audit logs
- why transactions matter for consistency

## Slide 9. Optimization and Deployment
- heavy queries: dose history, reports, audit logs, caregiver dashboard
- indexes used to improve filtering and joins
- live Render PostgreSQL database supports the working demo
- fake seed data is loaded into the live database for demonstration

## Slide 10. Lessons Learned / Conclusion
- balancing normalized design with real screen needs
- using database rules to support security and integrity
- connecting database design to a working Spring Boot + React system
- Render PostgreSQL made the schema visible in a live deployed demo

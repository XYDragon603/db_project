# MedMinder Implementation Priorities

## Goal

This document defines a realistic implementation order for MedMinder so the project stays polished, aligned with both courses, and achievable within student project scope.

## Phase 1: Demo-Critical Core

### Sprint 1: Project Shell + Design System

- create `backend/`, `frontend/`, `docs/`, `.github/workflows/`
- set up React + Vite + Tailwind + shadcn-style component structure
- create `AppShell`, `UserLayout`, `DashboardLayout`, `Sidebar`, `Topbar`, `MobileBottomNav`
- create reusable UI components
- add basic GitHub Actions placeholders

Start Spring Security with a simple role-based authentication structure. Do not overcomplicate the first implementation pass.

### Sprint 2: Database Schema + Seed Data

- create PostgreSQL schema for core tables
- add constraints and indexes
- add fake seed data
- prepare database functions or query methods for demo-critical screens

### Sprint 3: User Core Flow

- Login
- User Dashboard / Today's Medications
- Medication List
- Add Medication
- Schedule Management
- Dose Logging
- Refill Alerts
- Dose History

### Sprint 4: Caregiver + Admin Demo

- Caregiver Dashboard
- Audit Logs
- basic role-based routing

## Phase 2: Secondary Pages

### Pages

- Register
- Edit Medication
- Caregiver Access
- Reports
- Profile / Settings
- Authorized Users
- Patient Medication Status
- Missed Dose List
- Refill Alert View
- User Management
- Role Management
- System Reports

### Database Focus

- prescriptions table
- broader role and user management queries
- adherence reporting refinement
- additional caregiver detail queries

## Phase 3: Polish and Course Deliverables

- seed data
- database documentation
- ER diagram
- README
- deployment guide
- CI/CD polish
- Trivy scan explanation
- Semgrep scan explanation
- presentation screenshots

## Priority Rule

Implementation should start only after these documentation files are reviewed:

- docs/specs/medminder-project-spec.md
- docs/database-design.md
- docs/screen-query-matrix.md
- docs/security-and-integrity.md
- docs/implementation-priorities.md

## Practical Scope Reminder

The Database course emphasizes schema, queries, integrity, and optimization.  
The SSC course emphasizes the full-stack shell and simple end-to-end integration.

The project should avoid overbuilding the API layer or adding features outside the approved scope.

# Sprint 3 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the MedMinder demo-critical user core flow across the backend and frontend with minimal, working REST APIs and routed pages.

**Architecture:** The backend will add a simple demo authentication structure, focused services, and thin controllers for the user core flow, caregiver dashboard, and admin audit logs. The frontend will replace the design preview with routed pages backed by a small API client and English-only demo-critical screens built from the existing shared component system.

**Tech Stack:** Spring Boot, Spring Data JPA, Spring Security, React, Vite, TypeScript, Tailwind CSS

---

## File Structure

### Create

- `backend/src/main/java/com/medminder/config/DataInitializer.java`
- `backend/src/main/java/com/medminder/config/DemoUserDetailsService.java`
- `backend/src/main/java/com/medminder/domain/repository/RoleRepository.java`
- `backend/src/main/java/com/medminder/domain/repository/UserRoleRepository.java`
- `backend/src/main/java/com/medminder/service/auth/AuthService.java`
- `backend/src/main/java/com/medminder/service/dashboard/DashboardService.java`
- `backend/src/main/java/com/medminder/service/medication/MedicationService.java`
- `backend/src/main/java/com/medminder/service/schedule/ScheduleService.java`
- `backend/src/main/java/com/medminder/service/dose/DoseLogService.java`
- `backend/src/main/java/com/medminder/service/refill/RefillService.java`
- `backend/src/main/java/com/medminder/service/caregiver/CaregiverDashboardService.java`
- `backend/src/main/java/com/medminder/service/admin/AdminAuditService.java`
- `backend/src/main/java/com/medminder/web/auth/AuthController.java`
- `backend/src/main/java/com/medminder/web/dashboard/UserDashboardController.java`
- `backend/src/main/java/com/medminder/web/medication/MedicationController.java`
- `backend/src/main/java/com/medminder/web/schedule/ScheduleController.java`
- `backend/src/main/java/com/medminder/web/dose/DoseLogController.java`
- `backend/src/main/java/com/medminder/web/refill/RefillController.java`
- `backend/src/main/java/com/medminder/web/caregiver/CaregiverDashboardController.java`
- `backend/src/main/java/com/medminder/web/admin/AdminAuditController.java`
- `backend/src/main/java/com/medminder/web/dto/...` for request and response models
- `backend/src/test/java/com/medminder/service/...` focused service tests
- `frontend/src/api/client.ts`
- `frontend/src/api/types.ts`
- `frontend/src/api/mock-data.ts`
- `frontend/src/hooks/use-demo-auth.ts`
- `frontend/src/components/shared/loading-state.tsx`
- `frontend/src/components/shared/section-card.tsx`
- `frontend/src/pages/login-page.tsx`
- `frontend/src/pages/user-dashboard-page.tsx`
- `frontend/src/pages/medication-list-page.tsx`
- `frontend/src/pages/add-medication-page.tsx`
- `frontend/src/pages/schedule-management-page.tsx`
- `frontend/src/pages/refill-alerts-page.tsx`
- `frontend/src/pages/dose-history-page.tsx`
- `frontend/src/pages/caregiver-dashboard-page.tsx`
- `frontend/src/pages/admin-audit-logs-page.tsx`
- `frontend/src/router.tsx`

### Modify

- `backend/src/main/java/com/medminder/config/SecurityConfig.java`
- `backend/src/main/resources/application.yml`
- `frontend/package.json`
- `frontend/src/App.tsx`
- `frontend/src/layouts/user-layout.tsx`
- `frontend/src/layouts/dashboard-layout.tsx`
- `README.md`

## Tasks

### Task 1: Backend auth and role plumbing
- [ ] Add role repositories and a simple user details service
- [ ] Update Spring Security for basic role-based endpoint protection
- [ ] Add auth DTOs and login endpoint returning demo profile data
- [ ] Add a backend test for login lookup behavior

### Task 2: Backend medication, dashboard, and history services
- [ ] Add dashboard, medication, schedule, dose, and refill DTOs
- [ ] Implement user dashboard, medication list, add medication, schedule, refill, and dose history services
- [ ] Implement `logDose` transaction behavior with audit logging
- [ ] Add focused service tests for derived pending status and dose logging stock reduction

### Task 3: Backend caregiver and admin endpoints
- [ ] Implement caregiver dashboard read-only overview service
- [ ] Implement admin audit log listing service
- [ ] Protect caregiver and admin endpoints by role
- [ ] Add repository-driven tests where behavior is non-trivial

### Task 4: Frontend routing and demo auth flow
- [ ] Add React Router and a small auth hook for role selection
- [ ] Replace the preview-only app root with routed screens
- [ ] Build a login page that routes to user, caregiver, or admin demo flows
- [ ] Keep all UI text English-only

### Task 5: Frontend demo-critical pages
- [ ] Build user dashboard, medication list, add medication, schedule management, refill alerts, and dose history pages
- [ ] Build caregiver dashboard and admin audit log page
- [ ] Reuse the shared layouts and components from Sprint 1
- [ ] Add a small API client with mock fallback so the frontend remains runnable before full backend integration

### Task 6: Verification
- [ ] Run backend compile
- [ ] Run frontend type check
- [ ] Run frontend production build
- [ ] Summarize the working Sprint 3 demo-critical flow and remaining gaps for Sprint 4

# Sprint 1 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the MedMinder project shell and a reusable healthcare SaaS frontend design system without implementing the full page flow yet.

**Architecture:** Sprint 1 creates the repository structure, a lightweight Spring Boot backend shell, a React + Vite + Tailwind frontend shell, and the shared UI foundation for User, Caregiver, and Admin experiences. The frontend will use a single design system with role-specific layouts, while the backend stays intentionally simple and ready for later domain work.

**Tech Stack:** React, Vite, TypeScript, Tailwind CSS, Spring Boot, Maven, GitHub Actions

---

## File Structure

### Create

- `backend/pom.xml`
- `backend/src/main/java/com/medminder/MedMinderApplication.java`
- `backend/src/main/java/com/medminder/config/SecurityConfig.java`
- `backend/src/main/java/com/medminder/web/HealthController.java`
- `backend/src/main/resources/application.yml`
- `backend/src/test/java/com/medminder/MedMinderApplicationTests.java`
- `frontend/package.json`
- `frontend/tsconfig.json`
- `frontend/tsconfig.node.json`
- `frontend/vite.config.ts`
- `frontend/postcss.config.js`
- `frontend/tailwind.config.ts`
- `frontend/index.html`
- `frontend/src/main.tsx`
- `frontend/src/App.tsx`
- `frontend/src/index.css`
- `frontend/src/lib/utils.ts`
- `frontend/src/data/demo.ts`
- `frontend/src/components/ui/button.tsx`
- `frontend/src/components/ui/card.tsx`
- `frontend/src/components/ui/input.tsx`
- `frontend/src/components/ui/select.tsx`
- `frontend/src/components/ui/dialog.tsx`
- `frontend/src/components/shared/app-shell.tsx`
- `frontend/src/components/shared/sidebar.tsx`
- `frontend/src/components/shared/topbar.tsx`
- `frontend/src/components/shared/mobile-bottom-nav.tsx`
- `frontend/src/components/shared/page-header.tsx`
- `frontend/src/components/shared/stat-card.tsx`
- `frontend/src/components/shared/status-badge.tsx`
- `frontend/src/components/shared/medication-card.tsx`
- `frontend/src/components/shared/schedule-card.tsx`
- `frontend/src/components/shared/refill-alert-card.tsx`
- `frontend/src/components/shared/patient-card.tsx`
- `frontend/src/components/shared/data-table.tsx`
- `frontend/src/components/shared/filter-bar.tsx`
- `frontend/src/components/shared/empty-state.tsx`
- `frontend/src/components/shared/form-input.tsx`
- `frontend/src/components/shared/form-select.tsx`
- `frontend/src/components/shared/date-input.tsx`
- `frontend/src/layouts/user-layout.tsx`
- `frontend/src/layouts/dashboard-layout.tsx`
- `frontend/src/pages/design-system-preview.tsx`
- `.github/workflows/frontend.yml`
- `.github/workflows/backend.yml`

### Modify

- `docs/implementation-priorities.md`

## Task 1: Create project shell

**Files:**
- Create: `backend/...`
- Create: `frontend/...`
- Create: `.github/workflows/frontend.yml`
- Create: `.github/workflows/backend.yml`

- [ ] **Step 1: Create the top-level directories**

Run:

```powershell
New-Item -ItemType Directory -Force backend, frontend, .github, .github/workflows
```

Expected: the directories exist in the workspace.

- [ ] **Step 2: Add the Spring Boot shell files**

Create a minimal application class, a simple health controller, and a lightweight security config that permits health checks and prepares role-based auth for later.

- [ ] **Step 3: Add the Vite + React shell files**

Create the frontend config files, root HTML, entrypoint, and base CSS with the MedMinder design tokens.

- [ ] **Step 4: Add basic GitHub Actions placeholders**

Create one frontend workflow and one backend workflow with simple install or compile placeholder checks.

- [ ] **Step 5: Verify the file structure**

Run:

```powershell
Get-ChildItem -Recurse -File backend, frontend, .github
```

Expected: shell files are present in all three areas.

## Task 2: Build the shared design system foundation

**Files:**
- Create: `frontend/src/index.css`
- Create: `frontend/src/lib/utils.ts`
- Create: `frontend/src/components/ui/*.tsx`

- [ ] **Step 1: Define the visual tokens**

Add CSS variables for the soft blue and white healthcare palette, radius scale, shadows, typography rhythm, and spacing.

- [ ] **Step 2: Add utility helpers**

Create a small `cn` utility for class merging and composition.

- [ ] **Step 3: Create the core UI primitives**

Build button, card, input, select, and dialog primitives that can be reused by the role-specific layouts and pages.

- [ ] **Step 4: Verify TypeScript syntax**

Run:

```powershell
cmd /c "cd /d E:\Desktop\dbssc project\frontend && npx tsc --noEmit"
```

Expected: no TypeScript syntax errors.

## Task 3: Build layouts and shared MedMinder components

**Files:**
- Create: `frontend/src/components/shared/*.tsx`
- Create: `frontend/src/layouts/user-layout.tsx`
- Create: `frontend/src/layouts/dashboard-layout.tsx`

- [ ] **Step 1: Build the shell and navigation components**

Create `AppShell`, `Sidebar`, `Topbar`, and `MobileBottomNav`.

- [ ] **Step 2: Build content presentation components**

Create `PageHeader`, `StatCard`, `StatusBadge`, `MedicationCard`, `ScheduleCard`, `RefillAlertCard`, `PatientCard`, `EmptyState`, `FilterBar`, and `DataTable`.

- [ ] **Step 3: Build the form wrappers**

Create `FormInput`, `FormSelect`, and `DateInput` so later screens share the same field language.

- [ ] **Step 4: Build `UserLayout` and `DashboardLayout`**

Use the shared shell components to support user, caregiver, and admin surfaces with different density and navigation behavior.

- [ ] **Step 5: Verify TypeScript syntax again**

Run:

```powershell
cmd /c "cd /d E:\Desktop\dbssc project\frontend && npx tsc --noEmit"
```

Expected: no TypeScript syntax errors after adding all components.

## Task 4: Create a design system preview page

**Files:**
- Create: `frontend/src/data/demo.ts`
- Create: `frontend/src/pages/design-system-preview.tsx`
- Modify: `frontend/src/App.tsx`

- [ ] **Step 1: Add demo data**

Create small fake datasets for medication cards, schedules, refill alerts, patient cards, and table rows.

- [ ] **Step 2: Create the preview page**

Compose the layouts and components into one preview surface that demonstrates the visual system for User, Caregiver, and Admin without building all pages.

- [ ] **Step 3: Wire the preview into the app root**

Render the preview page from `App.tsx`.

- [ ] **Step 4: Verify TypeScript syntax**

Run:

```powershell
cmd /c "cd /d E:\Desktop\dbssc project\frontend && npx tsc --noEmit"
```

Expected: no TypeScript syntax errors.

## Task 5: Verify Sprint 1 output

**Files:**
- Create: none
- Modify: none

- [ ] **Step 1: Check backend Java syntax**

Run:

```powershell
cmd /c "cd /d E:\Desktop\dbssc project\backend && mvn -q -DskipTests compile"
```

Expected: backend shell compiles, or stops with a clear missing-tool error if Maven is unavailable.

- [ ] **Step 2: Check frontend TypeScript syntax**

Run:

```powershell
cmd /c "cd /d E:\Desktop\dbssc project\frontend && npx tsc --noEmit"
```

Expected: TypeScript passes.

- [ ] **Step 3: Summarize any environment blockers**

Record whether Maven, npm, or local package installation is blocked so Sprint 2 can account for it.

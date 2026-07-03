# Sprint 1 Preview Verification

## Purpose

This document explains how to run and verify the Sprint 1 MedMinder preview.

Sprint 1 does not implement all application pages. It provides the project shell, shared healthcare SaaS design system, core layouts, and a preview surface for the User, Caregiver, and Admin experiences.

## Verified Build Status

The Sprint 1 scaffold has been verified with:

- frontend type check: `npm run check`
- frontend production build: `npm run build`
- backend compile check: `mvn -q -DskipTests compile`

## How to Run the Frontend Preview

From the project root:

```bash
cd frontend
npm install
npm run dev
```

Vite will print a local development URL, usually:

```text
http://localhost:5173/
```

Open that URL in a browser.

## What the Preview Should Show

The preview page should display these sections:

### 1. User Experience Preview

- soft blue and white healthcare SaaS styling
- sidebar on large screens
- mobile bottom navigation behavior
- Today's Medications cards
- Schedule Management card preview
- Refill alert cards
- Adherence stat card

### 2. Caregiver Experience Preview

- read-only caregiver dashboard layout
- patient cards
- missed and pending overview
- no edit-focused actions

### 3. Admin Experience Preview

- SaaS-style dashboard layout
- KPI stat cards
- filter bar
- styled audit log table

### 4. Shared Components Preview

- form fields
- select input
- date input
- primary, secondary, and danger buttons
- empty state
- modal preview

## Sprint 1 Acceptance Checklist

Sprint 1 is considered successful if:

- the frontend starts without errors
- the preview page renders all three role experiences
- the UI stays English-only
- the visual system is consistent across all sections
- the frontend passes type checking
- the frontend builds successfully
- the backend shell compiles successfully

## Scope Reminder

Sprint 1 is intentionally limited to:

- project shell
- backend shell
- frontend design system
- shared layouts
- reusable components
- preview verification

It does not yet implement the full demo-critical user flow.
